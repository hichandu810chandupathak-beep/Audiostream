package com.bookstream.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AuthRed = Color(0xFFE50914)
private val AuthBg = Color(0xFF0E0E10)
private val AuthMuted = Color(0xFF9A9AA3)

@Composable
fun AuthScreen(onContinue: (rememberSession: Boolean) -> Unit, onGuest: () -> Unit) {
    var signUp by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) AlertDialog(
        onDismissRequest = { showDialog = false },
        title = { Text(dialogTitle) },
        text = { Text(dialogMessage) },
        confirmButton = { TextButton(onClick = { showDialog = false }) { Text("OK", color = AuthRed) } },
        containerColor = Color(0xFF202025), titleContentColor = Color.White, textContentColor = Color(0xFFD0D0D6)
    )
    Column(Modifier.fillMaxSize().background(AuthBg).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(18.dp))
        Text("BookStream", color = Color.White, fontSize = 31.sp, fontWeight = FontWeight.ExtraBold)
        Text("Stories that stay with you.", color = AuthMuted, fontSize = 14.sp, modifier = Modifier.padding(top = 6.dp, bottom = 24.dp))
        Surface(color = Color(0xFF19191E), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(if (signUp) "Create your account" else "Welcome back", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                Text(if (signUp) "Start your next listening journey." else "Sign in to continue listening.", color = AuthMuted, fontSize = 13.sp)
                TabRow(selectedTabIndex = if (signUp) 1 else 0, containerColor = Color(0xFF19191E), contentColor = AuthRed) {
                    Tab(selected = !signUp, onClick = { signUp = false; message = "" }, text = { Text("Sign In") })
                    Tab(selected = signUp, onClick = { signUp = true; message = "" }, text = { Text("Sign Up") })
                }
                if (signUp) AuthField(name, { name = it }, "Full name")
                AuthField(email, { email = it }, "Email address")
                AuthField(phone, { phone = it }, "Phone number (optional)")
                OutlinedTextField(value = password, onValueChange = { password = it }, modifier = Modifier.fillMaxWidth(),
                    label = { Text("Password") },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = { Text(if (showPassword) "Hide" else "Show", color = AuthRed, modifier = Modifier.clickable { showPassword = !showPassword }.padding(8.dp)) },
                    singleLine = true, colors = authFieldColors())
                if (message.isNotBlank()) Text(message, color = Color(0xFFFF777F), fontSize = 12.sp)
                Button(onClick = {
                    val validEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
                    message = when {
                        signUp && name.trim().length < 2 -> "Please enter your name."
                        !validEmail -> "Enter a valid email address."
                        password.length < 6 -> "Password must contain at least 6 characters."
                        else -> ""
                    }
                    if (message.isEmpty()) onContinue(true)
                }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = AuthRed)) {
                    Text(if (signUp) "Create Account" else "Sign In", fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = {
                    val target = email.ifBlank { phone }
                    dialogTitle = "Reset your password"
                    dialogMessage = if (target.isBlank()) "Enter your registered email or phone number first. Reset delivery requires a configured authentication provider."
                    else "Reset requested for $target. This build uses local demo sessions; connect an authentication provider to send a real reset link or SMS."
                    showDialog = true
                }, modifier = Modifier.align(Alignment.End)) { Text("Forgot Password?", color = Color(0xFFFF777F)) }
                Row(verticalAlignment = Alignment.CenterVertically) { Spacer(Modifier.weight(1f)); Text("OR", color = AuthMuted, fontSize = 11.sp); Spacer(Modifier.weight(1f)) }
                Button(onClick = {
                    dialogTitle = "Google sign-in"
                    dialogMessage = "The Google button is styled and wired, but OAuth/Firebase credentials are not configured. No Google account has been authenticated."
                    showDialog = true
                }, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF202124))) {
                    Text("G", color = Color(0xFF4285F4), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp); Spacer(Modifier.width(12.dp)); Text("Continue with Google", fontWeight = FontWeight.SemiBold)
                }
                Button(onClick = {
                    dialogTitle = "Phone OTP"
                    dialogMessage = if (phone.filter(Char::isDigit).length < 10) "Enter a valid phone number. Sending and verifying real OTPs requires an SMS authentication provider."
                    else "Phone number $phone is entered, but this app is not connected to an SMS/OTP provider. No SMS has been sent."
                    showDialog = true
                }, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B2B32))) {
                    Text("☎  Continue with Phone / OTP", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        Spacer(Modifier.height(18.dp))
        Text("Browse as Guest  →", color = Color(0xFFFF777F), fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable(onClick = onGuest).padding(12.dp))
        Text("Email sign-in here stores a local demo session only. It is not secure server authentication.", color = AuthMuted, fontSize = 11.sp, lineHeight = 16.sp)
    }
}

@Composable
private fun AuthField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(), label = { Text(label) }, singleLine = true, colors = authFieldColors())
}
@Composable
private fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = AuthRed,
    unfocusedBorderColor = Color(0xFF494951), focusedLabelColor = AuthRed, unfocusedLabelColor = AuthMuted, cursorColor = AuthRed
)
