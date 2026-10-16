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
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BookDetailScreen(bookId: String, isPlaying: Boolean, isInLibrary: Boolean, onBack: () -> Unit,
    onPlay: (String) -> Unit, onPause: () -> Unit, onToggleLibrary: (String) -> Unit) {
    val book = BookData.find(bookId) ?: BookData.catalog.first()
    var readerMode by remember(bookId) { mutableStateOf(false) }
    if (readerMode) {
        BookReaderScreen(book, isPlaying, { readerMode = false }, { onPlay(book.id) }, onPause)
        return
    }
    Column(Modifier.fillMaxSize().background(Color(0xFF0E0E10)).verticalScroll(rememberScrollState())) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("‹ Back", color = Color(0xFFFF777F), fontSize = 16.sp) }
            Spacer(Modifier.weight(1f))
            Text("BOOK DETAILS", color = Color(0xFF9A9AA3), fontSize = 10.sp, letterSpacing = 1.5.sp)
        }
        Box(Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(230.dp).clip(RoundedCornerShape(26.dp)).background(book.coverColor),
            contentAlignment = Alignment.Center) {
            if (book.coverUrl != null) AsyncImage(model = book.coverUrl, contentDescription = book.title, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Text(book.icon, fontSize = 76.sp)
            Surface(Modifier.align(Alignment.BottomStart).padding(18.dp), color = Color(0xAA000000), shape = RoundedCornerShape(10.dp)) {
                Text(book.category, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp))
            }
        }
        Column(Modifier.padding(horizontal = 22.dp, vertical = 18.dp)) {
            Text(book.title, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 34.sp)
            Text("लेखक: " + book.author, color = Color(0xFFFF777F), fontSize = 15.sp, modifier = Modifier.padding(top = 7.dp))
            Text("◷  " + book.duration + "   •   " + book.languageName, color = Color(0xFF9A9AA3), fontSize = 12.sp, modifier = Modifier.padding(top = 10.dp))
            Text(book.synopsis, color = Color(0xFFD0D0D6), fontSize = 15.sp, lineHeight = 24.sp, modifier = Modifier.padding(top = 16.dp))
            Spacer(Modifier.height(20.dp))
            Button(onClick = { if (isPlaying) onPause() else onPlay(book.id) }, modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE50914))) {
                Text(if (isPlaying) "Ⅱ Narration रोकें" else "▶ Narration सुनें", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = { readerMode = true }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF29292F))) { Text("📖 Read Book", color = Color.White) }
                Button(onClick = { onToggleLibrary(book.id) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isInLibrary) Color(0xFF345D4D) else Color(0xFF29292F))) {
                    Text(if (isInLibrary) "♥ Saved" else "♡ Save", color = Color.White)
                }
            }
            Spacer(Modifier.height(20.dp))
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1B20)), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(18.dp)) {
                    Text("पढ़ने का पाठ", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                    Text("मौलिक पुनर्कथन और साहित्यिक परिचय; इसे मूल रचना का पूर्ण पाठ न समझें।",
                        color = Color(0xFF9A9AA3), fontSize = 11.sp, modifier = Modifier.padding(top = 5.dp, bottom = 14.dp))
                    Text(book.fullStoryText, color = Color(0xFFE0E0E5), fontSize = 15.sp, lineHeight = 25.sp)
                }
            }
            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun BookReaderScreen(book: BookItem, isPlaying: Boolean, onBack: () -> Unit, onPlay: () -> Unit, onPause: () -> Unit) {
    var darkMode by remember(book.id) { mutableStateOf(true) }
    var fontSize by remember(book.id) { mutableFloatStateOf(19f) }
    var lineSpacing by remember(book.id) { mutableFloatStateOf(1.55f) }
    val background = if (darkMode) Color(0xFF111114) else Color(0xFFFFF8EB)
    val foreground = if (darkMode) Color(0xFFECE7DE) else Color(0xFF28231D)
    val secondary = if (darkMode) Color(0xFFAAA5A0) else Color(0xFF766B60)
    Column(Modifier.fillMaxSize().background(background)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("‹ वापस", color = if (darkMode) Color(0xFFFF777F) else Color(0xFFB21C28)) }
            Column(Modifier.weight(1f).padding(horizontal = 4.dp)) {
                Text(book.title, color = foreground, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1)
                Text(book.author, color = secondary, fontSize = 11.sp, maxLines = 1)
            }
            TextButton(onClick = { darkMode = !darkMode }) { Text(if (darkMode) "☀ Light" else "☾ Dark", color = foreground) }
        }
        HorizontalDivider(color = secondary.copy(alpha = .25f))
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("A−", color = foreground, fontSize = 12.sp)
                Slider(value = fontSize, onValueChange = { fontSize = it }, valueRange = 16f..30f, modifier = Modifier.weight(1f).height(34.dp),
                    colors = SliderDefaults.colors(thumbColor = Color(0xFFE50914), activeTrackColor = Color(0xFFE50914)))
                Text("A+", color = foreground, fontSize = 16.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("पंक्ति दूरी", color = secondary, fontSize = 11.sp)
                Slider(value = lineSpacing, onValueChange = { lineSpacing = it }, valueRange = 1.25f..2f, modifier = Modifier.weight(1f).height(32.dp),
                    colors = SliderDefaults.colors(thumbColor = Color(0xFFE50914), activeTrackColor = Color(0xFFE50914)))
                Text(String.format("%.2f", lineSpacing), color = secondary, fontSize = 10.sp)
            }
        }
        HorizontalDivider(color = secondary.copy(alpha = .25f))
        Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 24.dp)) {
            Text(book.title, color = foreground, fontSize = (fontSize + 7).sp, fontWeight = FontWeight.Bold, lineHeight = (fontSize * lineSpacing * 1.12f).sp)
            Text(book.author + " • " + book.languageName, color = secondary, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp, bottom = 22.dp))
            Text(book.fullStoryText, color = foreground, fontSize = fontSize.sp, lineHeight = (fontSize * lineSpacing).sp)
        }
        Surface(color = if (darkMode) Color(0xFF202025) else Color(0xFFF1E7D7)) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("पढ़ें और सुनें", color = foreground, fontSize = 12.sp, modifier = Modifier.weight(1f))
                Button(onClick = { if (isPlaying) onPause() else onPlay() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE50914))) {
                    Text(if (isPlaying) "Ⅱ रोकें" else "▶ सुनें", color = Color.White)
                }
            }
        }
    }
}