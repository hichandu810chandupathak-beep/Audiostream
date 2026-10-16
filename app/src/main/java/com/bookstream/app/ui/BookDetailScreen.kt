package com.bookstream.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BookDetailScreen(bookId: String, isPlaying: Boolean, isInLibrary: Boolean, onBack: () -> Unit, onPlay: (String) -> Unit, onPause: () -> Unit, onToggleLibrary: (String) -> Unit) {
    val book = BookData.find(bookId) ?: BookData.catalog.first()
    var showText by remember(bookId) { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().background(Color(0xFF0E0E10)).verticalScroll(rememberScrollState())) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("‹  Back", color = Color(0xFFFF777F), fontSize = 16.sp) }
            Spacer(Modifier.weight(1f))
            Text("BOOK DETAILS", color = Color(0xFF9A9AA3), fontSize = 10.sp, letterSpacing = 1.5.sp)
            Spacer(Modifier.width(12.dp))
        }
        Box(Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(245.dp).clip(RoundedCornerShape(26.dp)).background(book.coverColor), contentAlignment = Alignment.Center) {
            Text(book.icon, fontSize = 76.sp)
            Surface(Modifier.align(Alignment.BottomStart).padding(18.dp), color = Color(0xAA000000), shape = RoundedCornerShape(10.dp)) {
                Text(book.category, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp))
            }
        }
        Column(Modifier.padding(horizontal = 22.dp, vertical = 18.dp)) {
            Text(book.title, color = Color.White, fontSize = 29.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 34.sp)
            Text("by " + book.author, color = Color(0xFFFF777F), fontSize = 15.sp, modifier = Modifier.padding(top = 7.dp))
            Text("◷  " + book.duration + "   •   " + book.languageTag, color = Color(0xFF9A9AA3), fontSize = 12.sp, modifier = Modifier.padding(top = 10.dp))
            Text(book.synopsis, color = Color(0xFFD0D0D6), fontSize = 14.sp, lineHeight = 22.sp, modifier = Modifier.padding(top = 16.dp))
            Spacer(Modifier.height(20.dp))
            Button(onClick = { if (isPlaying) onPause() else onPlay(book.id) }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE50914))) {
                Text(if (isPlaying) "Ⅱ  Pause Narration" else "▶  Play Narration", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = { showText = !showText }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF29292F))) {
                    Text(if (showText) "Hide Text" else "Read Full Text", color = Color.White)
                }
                Button(onClick = { onToggleLibrary(book.id) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = if (isInLibrary) Color(0xFF345D4D) else Color(0xFF29292F))) {
                    Text(if (isInLibrary) "♥ In Library" else "♡ Add to Library", color = Color.White)
                }
            }
            if (showText) {
                Spacer(Modifier.height(20.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1B20)), shape = RoundedCornerShape(18.dp)) {
                    Column(Modifier.padding(18.dp)) {
                        Text("Narration text", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                        Text("Original summary and introduction; not the complete copyrighted edition.", color = Color(0xFF9A9AA3), fontSize = 11.sp, modifier = Modifier.padding(top = 5.dp, bottom = 14.dp))
                        Text(book.fullStoryText, color = Color(0xFFE0E0E5), fontSize = 15.sp, lineHeight = 25.sp)
                    }
                }
            }
            Spacer(Modifier.height(28.dp))
        }
    }
}
