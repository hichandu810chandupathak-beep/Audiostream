package com.bookstream.app.ui

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AuthRed = Color(0xFFE50914)
private val AuthBg = Color(0xFF0E0E10)
private val AuthMuted = Color(0xFF9A9AA3)

@Composable
fun AuthScreen(
    onSubmit: (String, String, String, Boolean) -> String?,
    onGuest: () -> Unit,
    onGoogle: (String) -> Unit,
    onPhoneOtp: (String, String) -> String?,
    onResetPassword: (String, String, String) -> String?
) {
    val context = LocalContext.current
    var signUp by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var phoneMode by remember { mutableStateOf(false) }
    var otpSent by remember { mutableStateOf(false) }
    var googleDialog by remember { mutableStateOf(false) }
    var googleEmail by remember { mutableStateOf("") }
    var googleMessage by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("123456") }
    var resetDialog by remember { mutableStateOf(false) }
    var resetIdentifier by remember { mutableStateOf("") }
    var resetPassword by remember { mutableStateOf("") }
    var resetConfirm by remember { mutableStateOf("") }
    var resetMessage by remember { mutableStateOf("") }

    if (googleDialog) {
        AlertDialog(
            onDismissRequest = { googleDialog = false; googleMessage = "" },
            title = { Text("Choose Google account") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Confirm an email to continue. This demo does not connect to Google or verify the account.", fontSize = 13.sp)
                    OutlinedTextField(value = googleEmail, onValueChange = { googleEmail = it.trim(); googleMessage = "" }, label = { Text("Google account email") }, singleLine = true)
                    if (googleMessage.isNotBlank()) Text(googleMessage, color = AuthRed, fontSize = 12.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(googleEmail.trim()).matches()) googleMessage = "Enter a valid email address."
                    else { googleDialog = false; googleMessage = ""; onGoogle(googleEmail.trim()) }
                }) { Text("Continue", color = AuthRed) }
            },
            dismissButton = { TextButton(onClick = { googleDialog = false; googleMessage = "" }) { Text("Cancel") } }
        )
    }

    if (resetDialog) {
        AlertDialog(
            onDismissRequest = { resetDialog = false; resetMessage = "" },
            title = { Text("Reset password") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Update the password stored for your local account.", fontSize = 13.sp)
                    OutlinedTextField(value = resetIdentifier, onValueChange = { resetIdentifier = it }, label = { Text("Registered email or phone") }, singleLine = true)
                    OutlinedTextField(value = resetPassword, onValueChange = { resetPassword = it }, label = { Text("Enter new password") }, visualTransformation = PasswordVisualTransformation(), singleLine = true)
                    OutlinedTextField(value = resetConfirm, onValueChange = { resetConfirm = it }, label = { Text("Confirm new password") }, visualTransformation = PasswordVisualTransformation(), singleLine = true)
                    if (resetMessage.isNotBlank()) Text(resetMessage, color = AuthRed, fontSize = 12.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    resetMessage = when {
                        resetPassword.length < 6 -> "Password must contain at least 6 characters."
                        resetPassword != resetConfirm -> "Passwords do not match."
                        else -> onResetPassword(resetIdentifier, resetPassword, resetConfirm) ?: ""
                    }
                    if (resetMessage.isEmpty()) {
                        Toast.makeText(context, "Password updated successfully!", Toast.LENGTH_SHORT).show()
                        resetDialog = false
                        identifier = resetIdentifier
                        password = resetPassword
                        resetPassword = ""; resetConfirm = ""; resetMessage = ""
                    }
                }) { Text("Update Password", color = AuthRed) }
            },
            dismissButton = { TextButton(onClick = { resetDialog = false }) { Text("Cancel") } }
        )
    }

    Column(Modifier.fillMaxSize().background(AuthBg).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(18.dp))
        Text("BookStream", color = Color.White, fontSize = 31.sp, fontWeight = FontWeight.ExtraBold)
        Text("Stories that stay with you.", color = AuthMuted, fontSize = 14.sp, modifier = Modifier.padding(top = 6.dp, bottom = 24.dp))
        Surface(color = Color(0xFF19191E), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(when { phoneMode -> "Continue with phone"; signUp -> "Create your account"; else -> "Welcome back" }, color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                Text(if (phoneMode) { if (otpSent) "Demo OTP sent. Enter the 6-digit code to verify." else "Enter your 10-digit mobile number to request an OTP." } else if (signUp) "Create a private account on this device." else "Sign in with your registered email or phone.", color = AuthMuted, fontSize = 13.sp)
                if (!phoneMode) {
                    TabRow(selectedTabIndex = if (signUp) 1 else 0, containerColor = Color(0xFF19191E), contentColor = AuthRed) {
                        Tab(selected = !signUp, onClick = { signUp = false; message = "" }, text = { Text("Sign In") })
                        Tab(selected = signUp, onClick = { signUp = true; message = ""; phoneMode = false }, text = { Text("Sign Up") })
                    }
                    if (signUp) AuthField(name, { name = it }, "Full name")
                    AuthField(identifier, { identifier = it }, "Email address or phone number")
                    OutlinedTextField(value = password, onValueChange = { password = it }, modifier = Modifier.fillMaxWidth(),
                        label = { Text("Password (6+ characters)") },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = { Text(if (showPassword) "Hide" else "Show", color = AuthRed, modifier = Modifier.clickable { showPassword = !showPassword }.padding(8.dp)) },
                        singleLine = true, colors = authFieldColors())
                    if (message.isNotBlank()) Text(message, color = Color(0xFFFF777F), fontSize = 12.sp, lineHeight = 17.sp)
                    Button(onClick = {
                        message = onSubmit(name, identifier, password, signUp) ?: ""
                        if (message.isEmpty()) password = ""
                    }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = AuthRed)) {
                        Text(if (signUp) "Create Account" else "Sign In", fontWeight = FontWeight.Bold)
                    }
                    TextButton(onClick = { resetIdentifier = identifier; resetDialog = true; resetMessage = "" }, modifier = Modifier.align(Alignment.End)) { Text("Forgot Password?", color = Color(0xFFFF777F)) }
                } else {
                    OutlinedTextField(
                        value = identifier,
                        onValueChange = { identifier = it.filter(Char::isDigit).take(10); message = "" },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Enter 10-digit mobile number") },
                        singleLine = true,
                        colors = authFieldColors()
                    )
                    if (otpSent) {
                        OutlinedTextField(value = otp, onValueChange = { otp = it.filter(Char::isDigit).take(6) },
                            modifier = Modifier.fillMaxWidth(), label = { Text("6-digit OTP") }, singleLine = true, colors = authFieldColors())
                    }
                    if (message.isNotBlank()) Text(message, color = Color(0xFFFF777F), fontSize = 12.sp)
                    if (!otpSent) {
                        Button(onClick = { otp = "123456"; otpSent = true; message = "Demo OTP is 123456. No SMS is sent." },
                            enabled = identifier.length == 10, modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = AuthRed)) {
                            Text("Send OTP", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(onClick = { message = if (otp.length != 6) "Enter the 6-digit OTP." else onPhoneOtp(identifier, otp) ?: "" },
                            enabled = otp.length == 6, modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = AuthRed)) {
                            Text("Verify OTP", fontWeight = FontWeight.Bold)
                        }
                    }
                    Text("Demo only: OTP 123456 is simulated; no SMS is sent.", color = AuthMuted, fontSize = 11.sp)
                    TextButton(onClick = { phoneMode = false; otpSent = false; message = "" }) { Text("Back to email/password", color = Color(0xFFFF777F)) }
                }
            }
        }
        if (!phoneMode) {
            Spacer(Modifier.height(14.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(Modifier.weight(1f), color = Color(0xFF38383F))
                Text("  OR CONTINUE WITH  ", color = AuthMuted, fontSize = 10.sp)
                HorizontalDivider(Modifier.weight(1f), color = Color(0xFF38383F))
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = { googleEmail = identifier.takeIf { it.contains("@") } ?: ""; googleDialog = true; googleMessage = "" }, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(13.dp)) {
                Text("G   Continue with Google", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(9.dp))
            OutlinedButton(onClick = { phoneMode = true; otpSent = false; identifier = ""; message = ""; otp = "" }, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(13.dp)) {
                Text("☎   Continue with Phone / OTP", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("Browse as Guest  →", color = Color(0xFFFF777F), fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable(onClick = onGuest).padding(12.dp))
        Text("Local demo accounts are encrypted on this device. Google account confirmation and OTP are simulated; no Google sign-in or SMS verification occurs.", color = AuthMuted, fontSize = 11.sp, lineHeight = 16.sp)
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
