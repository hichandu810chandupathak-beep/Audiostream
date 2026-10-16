package com.bookstream.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val HBg = Color(0xFF0E0E10)
private val HRed = Color(0xFFE50914)
private val HMuted = Color(0xFF9A9AA3)

@Composable
fun HomeScreen(currentBookId: String, isPlaying: Boolean, libraryIds: Set<String>, onBookClick: (String) -> Unit, onPlay: (String) -> Unit, onPause: () -> Unit, onResume: () -> Unit, onLibraryClick: (String) -> Unit, userDisplayName: String = "BookStream reader", isGuest: Boolean = true, onLogout: () -> Unit = {}, onlineResults: List<BookItem> = emptyList(), onSearchOnline: (String) -> Unit = {}, onlineError: String? = null) {
    var tab by remember { mutableIntStateOf(0) }
    var query by remember { mutableStateOf("") }
    var progress by remember { mutableFloatStateOf(.28f) }
    val current = BookData.find(currentBookId) ?: BookData.catalog.first()
    val books = BookData.catalog.filter { book -> query.isBlank() || book.title.contains(query.trim(), true) || book.author.contains(query.trim(), true) || book.category.contains(query.trim(), true) || book.languageName.contains(query.trim(), true) || book.synopsis.contains(query.trim(), true) || book.fullStoryText.contains(query.trim(), true) }
    Scaffold(containerColor = HBg, bottomBar = {
        Column {
            Surface(color = Color(0xFF222228)) {
                Column(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 7.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(42.dp).clip(RoundedCornerShape(9.dp)).background(current.coverColor), contentAlignment = Alignment.Center) { Text(current.icon, fontSize = 22.sp) }
                        Column(Modifier.weight(1f).clickable { onBookClick(current.id) }.padding(start = 10.dp)) {
                            Text(current.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(current.author, color = HMuted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Surface(Modifier.size(40.dp).clickable { if (isPlaying) onPause() else onResume() }, color = HRed, shape = CircleShape) {
                            Box(contentAlignment = Alignment.Center) { Text(if (isPlaying) "Ⅱ" else "▶", color = Color.White, fontSize = 17.sp) }
                        }
                    }
                    Slider(value = progress, onValueChange = { progress = it }, modifier = Modifier.fillMaxWidth().height(22.dp), colors = SliderDefaults.colors(thumbColor = HRed, activeTrackColor = HRed, inactiveTrackColor = Color.DarkGray))
                }
            }
            NavigationBar(containerColor = Color(0xFF18181C)) {
                listOf("Home" to "⌂", "Search" to "⌕", "My Library" to "▤", "Profile" to "●").forEachIndexed { i, item ->
                    NavigationBarItem(selected = tab == i, onClick = { tab = i }, icon = { Text(item.second, fontSize = 21.sp) }, label = { Text(item.first, fontSize = 10.sp, maxLines = 1) })
                }
            }
        }
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).background(HBg).verticalScroll(rememberScrollState())) {
            Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Row { Text("Book", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold); Text("Stream", color = HRed, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold) }
                    Text("YOUR NEXT GREAT LISTEN", color = HMuted, fontSize = 9.sp, letterSpacing = 1.5.sp)
                }
                Surface(Modifier.size(42.dp).clickable { tab = 1 }, color = Color(0xFF25252B), shape = CircleShape) { Box(contentAlignment = Alignment.Center) { Text("⌕", color = Color.White, fontSize = 27.sp) } }
                Spacer(Modifier.width(10.dp))
                Surface(Modifier.size(42.dp).clickable { tab = 3 }, color = HRed, shape = CircleShape) { Box(contentAlignment = Alignment.Center) { Text("C", color = Color.White, fontWeight = FontWeight.Bold) } }
            }
            when (tab) {
                0 -> {
                    Text("Good evening", color = HMuted, fontSize = 13.sp, modifier = Modifier.padding(start = 20.dp))
                    Text("Find your next escape.", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 20.dp, top = 4.dp, bottom = 16.dp))
                    val featured = BookData.catalog.first()
                    Card(Modifier.fillMaxWidth().padding(horizontal = 18.dp).clickable { onBookClick(featured.id) }, shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF292127))) {
                        Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("TRENDING NOW", color = Color(0xFFFF626B), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                                Spacer(Modifier.height(8.dp))
                                Text(featured.title, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                                Text(featured.author, color = Color(0xFFD0C7CC), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                                Spacer(Modifier.height(12.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Button(onClick = { onPlay(featured.id) }, colors = ButtonDefaults.buttonColors(containerColor = HRed), shape = RoundedCornerShape(12.dp)) { Text("▶ Play", color = Color.White, fontWeight = FontWeight.Bold) }
                                    Text(if (featured.id in libraryIds) "♥ Saved" else "♡ Save", color = Color.White, modifier = Modifier.clickable { onLibraryClick(featured.id) }.padding(10.dp))
                                }
                            }
                            Box(Modifier.width(76.dp).height(138.dp).clip(RoundedCornerShape(13.dp)).background(featured.coverColor), contentAlignment = Alignment.Center) { Text(featured.icon, fontSize = 36.sp) }
                        }
                    }
                    Spacer(Modifier.height(18.dp))
                    BookSection("Continue Listening", BookData.catalog.take(4), currentBookId, onBookClick, true)
                    BookSection("Popular Audiobooks", BookData.catalog, currentBookId, onBookClick)
                    BookSection("Top Narrators & Stories", BookData.catalog.filter { it.author == "Munshi Premchand" || it.author == "Rabindranath Tagore" || it.author == "Mahadevi Verma" || it.author == "A. P. J. Abdul Kalam" }, currentBookId, onBookClick)
                    Text("Categories", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 20.dp, top = 14.dp, bottom = 12.dp))
                    LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(BookData.languages + BookData.categories) { category ->
                            Surface(color = Color(0xFF25252B), shape = RoundedCornerShape(20.dp), modifier = Modifier.clickable { query = category; tab = 1 }) {
                                Text(category, color = Color.White, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
                            }
                        }
                    }
                }
                1 -> {
                    Text("Discover", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(20.dp))
                    OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), label = { Text("Search books, authors, categories") }, singleLine = true)
                    Button(onClick = { onSearchOnline(query) }, modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = HRed)) { Text("Search Google Books + Open Library", color = Color.White) }
                    BookSection("Offline & Curated Results", books, currentBookId, onBookClick)
                    if (onlineError != null) Text(onlineError, color = Color(0xFFFF777F), fontSize = 12.sp, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
                    BookSection("Online Book Results", onlineResults, currentBookId, onBookClick)
                }
                2 -> {
                    Text("My Library", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(20.dp))
                    val saved = BookData.catalog.filter { it.id in libraryIds }
                    if (saved.isEmpty()) Text("Your saved books will appear here. Tap Save on a book to add it.", color = HMuted, modifier = Modifier.padding(20.dp))
                    else BookSection("Saved for later", saved, currentBookId, onBookClick)
                }
                else -> {
                    Text("Your Profile", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(20.dp))
                    Surface(color = Color(0xFF1B1B20), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                        Column(Modifier.padding(20.dp)) {
                            Text("📚  " + userDisplayName, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(if (isGuest) "Guest session · this device" else "Local account · this device", color = HMuted, modifier = Modifier.padding(top = 8.dp))
                            Text(libraryIds.size.toString() + " saved books", color = Color(0xFFFF777F), modifier = Modifier.padding(top = 16.dp))
                            Spacer(Modifier.height(18.dp))
                            Button(onClick = onLogout, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = HRed), shape = RoundedCornerShape(12.dp)) { Text("Log out", color = Color.White, fontWeight = FontWeight.Bold) }
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun BookSection(title: String, books: List<BookItem>, currentBookId: String, onBookClick: (String) -> Unit, showProgress: Boolean = false) {
    Column {
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(title, color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text(books.size.toString() + " titles", color = HMuted, fontSize = 11.sp)
        }
        LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(books, key = { it.id }) { book ->
                Column(Modifier.width(142.dp).clickable { onBookClick(book.id) }) {
                    Box(Modifier.fillMaxWidth().height(if (showProgress) 126.dp else 154.dp).clip(RoundedCornerShape(15.dp)).background(book.coverColor).padding(12.dp)) {
                        if (book.coverUrl != null) AsyncImage(model = book.coverUrl, contentDescription = book.title, modifier = Modifier.matchParentSize(), contentScale = ContentScale.Crop)
                        Text(book.category.uppercase(), color = Color.White.copy(alpha = .75f), fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        Text(book.icon, fontSize = 28.sp, modifier = Modifier.align(Alignment.Center))
                        Text(book.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.align(Alignment.BottomStart), maxLines = 3, overflow = TextOverflow.Ellipsis)
                    }
                    if (showProgress) LinearProgressIndicator(progress = { if (book.id == currentBookId) .36f else .58f }, modifier = Modifier.fillMaxWidth().padding(top = 7.dp).height(4.dp).clip(CircleShape), color = HRed, trackColor = Color(0xFF37373E))
                    Text(book.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 8.dp))
                    Text(book.author, color = HMuted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 3.dp))
                }
            }
        }
    }
}
