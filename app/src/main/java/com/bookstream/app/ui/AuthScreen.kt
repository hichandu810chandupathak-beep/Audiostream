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
fun AuthScreen(onSubmit: (String, String, String, Boolean) -> String?, onGuest: () -> Unit) {
    var signUp by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().background(AuthBg).verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(18.dp))
        Text("BookStream", color = Color.White, fontSize = 31.sp, fontWeight = FontWeight.ExtraBold)
        Text("Stories that stay with you.", color = AuthMuted, fontSize = 14.sp, modifier = Modifier.padding(top = 6.dp, bottom = 24.dp))
        Surface(color = Color(0xFF19191E), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(if (signUp) "Create your account" else "Welcome back", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                Text(if (signUp) "Create a private account on this device." else "Sign in with your registered email or phone.", color = AuthMuted, fontSize = 13.sp)
                TabRow(selectedTabIndex = if (signUp) 1 else 0, containerColor = Color(0xFF19191E), contentColor = AuthRed) {
                    Tab(selected = !signUp, onClick = { signUp = false; message = "" }, text = { Text("Sign In") })
                    Tab(selected = signUp, onClick = { signUp = true; message = "" }, text = { Text("Sign Up") })
                }
                if (signUp) AuthField(name, { name = it }, "Full name")
                AuthField(identifier, { identifier = it }, "Email address or phone number")
                OutlinedTextField(value = password, onValueChange = { password = it }, modifier = Modifier.fillMaxWidth(),
                    label = { Text("Password (6+ characters)") },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = { Text(if (showPassword) "Hide" else "Show", color = AuthRed,
                        modifier = Modifier.clickable { showPassword = !showPassword }.padding(8.dp)) },
                    singleLine = true, colors = authFieldColors())
                if (message.isNotBlank()) Text(message, color = Color(0xFFFF777F), fontSize = 12.sp, lineHeight = 17.sp)
                Button(onClick = {
                    message = onSubmit(name, identifier, password, signUp) ?: ""
                    if (message.isEmpty()) password = ""
                }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AuthRed)) {
                    Text(if (signUp) "Create Account" else "Sign In", fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = {
                    message = "Password reset needs verified email/SMS delivery, which local-only accounts do not provide. No reset message has been sent."
                }, modifier = Modifier.align(Alignment.End)) { Text("Forgot Password?", color = Color(0xFFFF777F)) }
            }
        }
        Spacer(Modifier.height(18.dp))
        Text("Browse as Guest  →", color = Color(0xFFFF777F), fontSize = 15.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(onClick = onGuest).padding(12.dp))
        Text("Accounts are encrypted and stored on this device. They are not synced online.", color = AuthMuted, fontSize = 11.sp, lineHeight = 16.sp)
    }
}
@Composable
private fun AuthField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(),
        label = { Text(label) }, singleLine = true, colors = authFieldColors())
}
@Composable
private fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = AuthRed,
    unfocusedBorderColor = Color(0xFF494951), focusedLabelColor = AuthRed, unfocusedLabelColor = AuthMuted, cursorColor = AuthRed
)
