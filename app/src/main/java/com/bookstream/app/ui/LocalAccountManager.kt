package com.bookstream.app.ui

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.MessageDigest
import java.security.SecureRandom

data class LocalUserSession(val name: String, val identifier: String, val isGuest: Boolean)

object LocalAccountManager {
    private const val STORE = "bookstream_secure_accounts"
    private const val ACTIVE = "session_active"
    private const val NAME = "session_name"
    private const val IDENTIFIER = "session_identifier"
    private const val GUEST = "session_guest"
    private const val LIBRARY = "library_ids"

    private fun prefs(context: Context): SharedPreferences {
        val app = context.applicationContext
        val key = MasterKey.Builder(app).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
        return EncryptedSharedPreferences.create(app, STORE, key,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
    }
    private fun normalize(value: String): String {
        val input = value.trim()
        return if (input.contains("@")) input.lowercase() else input.filter(Char::isDigit)
    }
    private fun validIdentifier(value: String): Boolean {
        val normalized = normalize(value)
        return if (normalized.contains("@")) android.util.Patterns.EMAIL_ADDRESS.matcher(normalized).matches()
        else value.all { it.isDigit() || it in "+- ()" } && normalized.length in 10..15
    }
    private fun digest(value: String): String =
        MessageDigest.getInstance("SHA-256").digest(value.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
    private fun prefix(identifier: String) = "account." + digest(normalize(identifier)) + "."
    private fun saveSession(p: SharedPreferences, name: String, identifier: String, guest: Boolean) {
        p.edit().putBoolean(ACTIVE, true).putString(NAME, name)
            .putString(IDENTIFIER, identifier).putBoolean(GUEST, guest).apply()
    }
    fun register(context: Context, name: String, identifier: String, password: String): String? = try {
        val cleanName = name.trim()
        val normalized = normalize(identifier)
        when {
            cleanName.length < 2 -> "Please enter your full name."
            !validIdentifier(identifier) -> "Enter a valid email address or a phone number with 10–15 digits."
            password.length < 6 -> "Password must contain at least 6 characters."
            else -> {
                val p = prefs(context)
                val key = prefix(normalized)
                if (p.contains(key + "password")) "An account already exists. Please sign in."
                else {
                    val salt = Base64.encodeToString(ByteArray(16).also { SecureRandom().nextBytes(it) }, Base64.NO_WRAP)
                    p.edit().putString(key + "name", cleanName).putString(key + "identifier", normalized)
                        .putString(key + "salt", salt).putString(key + "password", digest(salt + password)).apply()
                    saveSession(p, cleanName, normalized, false)
                    null
                }
            }
        }
    } catch (_: Exception) { "Secure local storage is unavailable. Please restart the app and try again." }

    fun signIn(context: Context, identifier: String, password: String): String? = try {
        if (!validIdentifier(identifier) || password.isEmpty()) "Enter your registered email/phone and password."
        else {
            val p = prefs(context)
            val normalized = normalize(identifier)
            val key = prefix(normalized)
            val salt = p.getString(key + "salt", null)
            val stored = p.getString(key + "password", null)
            if (salt == null || stored == null || digest(salt + password) != stored) {
                "Invalid credentials. Check your email/phone and password."
            } else {
                saveSession(p, p.getString(key + "name", "BookStream reader") ?: "BookStream reader", normalized, false)
                null
            }
        }
    } catch (_: Exception) { "Secure local storage is unavailable. Please restart the app and try again." }

    fun signInGuest(context: Context) {
        runCatching { saveSession(prefs(context), "Guest reader", "guest", true) }
    }
    fun currentSession(context: Context): LocalUserSession? = runCatching {
        val p = prefs(context)
        if (!p.getBoolean(ACTIVE, false)) null else LocalUserSession(
            p.getString(NAME, "BookStream reader") ?: "BookStream reader",
            p.getString(IDENTIFIER, "") ?: "", p.getBoolean(GUEST, false))
    }.getOrNull()
    fun logout(context: Context) {
        runCatching { prefs(context).edit().putBoolean(ACTIVE, false).remove(NAME).remove(IDENTIFIER).remove(GUEST).apply() }
    }
    fun googleDemoSession(context: Context) {
        runCatching { saveSession(prefs(context), "Google User", "google-demo@bookstream.local", false) }
    }

    fun signInPhoneOtp(context: Context, phone: String, otp: String): String? = try {
        val normalized = normalize(phone)
        when {
            !phone.all { it.isDigit() || it in "+- ()" } || normalized.length !in 10..15 -> "Enter a valid phone number."
            otp != "123456" -> "Invalid OTP. For demo testing use 123456."
            else -> {
                val p = prefs(context)
                val key = prefix(normalized)
                if (!p.contains(key + "name")) {
                    val salt = Base64.encodeToString(ByteArray(16).also { SecureRandom().nextBytes(it) }, Base64.NO_WRAP)
                    p.edit().putString(key + "name", "Phone User").putString(key + "identifier", normalized)
                        .putString(key + "salt", salt).putString(key + "password", digest(salt + SecureRandom().nextLong().toString())).apply()
                }
                saveSession(p, p.getString(key + "name", "Phone User") ?: "Phone User", normalized, false)
                null
            }
        }
    } catch (_: Exception) { "Secure local storage is unavailable. Please try again." }

    fun resetPassword(context: Context, identifier: String, newPassword: String): String? = try {
        val normalized = normalize(identifier)
        when {
            !validIdentifier(identifier) -> "Enter the registered email or phone number."
            newPassword.length < 6 -> "Password must contain at least 6 characters."
            else -> {
                val p = prefs(context)
                val key = prefix(normalized)
                if (!p.contains(key + "password") || !p.contains(key + "salt")) "No local account was found for that email or phone."
                else {
                    val salt = Base64.encodeToString(ByteArray(16).also { SecureRandom().nextBytes(it) }, Base64.NO_WRAP)
                    p.edit().putString(key + "salt", salt).putString(key + "password", digest(salt + newPassword)).apply()
                    null
                }
            }
        }
    } catch (_: Exception) { "Could not update the password in secure local storage." }

    fun savedBookIds(context: Context): Set<String> =
        runCatching { prefs(context).getStringSet(LIBRARY, emptySet())?.toSet() ?: emptySet() }.getOrDefault(emptySet())
    fun saveBookIds(context: Context, ids: Set<String>) {
        runCatching { prefs(context).edit().putStringSet(LIBRARY, ids.toSet()).apply() }
    }
}