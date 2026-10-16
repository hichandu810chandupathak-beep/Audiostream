package com.bookstream.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class AudioBook(
    val title: String,
    val author: String,
    val category: String,
    val color: Color,
    val duration: String = "6 hr 24 min"
)

private val bookCatalog = listOf(
    AudioBook("Atomic Habits", "James Clear", "Self-Help", Color(0xFF315C52)),
    AudioBook("The Silent Patient", "Alex Michaelides", "Thriller", Color(0xFF563D66)),
    AudioBook("Dune", "Frank Herbert", "Sci-Fi", Color(0xFF96613B)),
    AudioBook("Deep Work", "Cal Newport", "Business", Color(0xFF344E78)),
    AudioBook("The Psychology of Money", "Morgan Housel", "Business", Color(0xFF7A4B3D)),
    AudioBook("Project Hail Mary", "Andy Weir", "Sci-Fi", Color(0xFF3B6874))
)

@Composable
fun HomeScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    var currentBook by remember { mutableStateOf(bookCatalog[0]) }
    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0.36f) }
    var isBookmarked by remember { mutableStateOf(false) }
    var searchOpen by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFF0E0E10),
        bottomBar = {
            Column {
                MiniPlayer(
                    book = currentBook,
                    playing = isPlaying,
                    progress = progress,
                    onPlayPause = { isPlaying = !isPlaying },
                    onProgressChange = { progress = it }
                )
                NavigationBar(containerColor = Color(0xFF18181C), contentColor = Color.White) {
                    val tabs = listOf("Home" to "⌂", "Search" to "⌕", "My Library" to "▤", "Profile" to "●")
                    tabs.forEachIndexed { index, tab ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = {
                                selectedTab = index
                                if (index == 1) searchOpen = !searchOpen
                            },
                            icon = { Text(tab.second, fontSize = 22.sp, fontWeight = FontWeight.Bold) },
                            label = { Text(tab.first, fontSize = 10.sp, maxLines = 1) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .background(Color(0xFF0E0E10))
                .padding(top = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Book", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Stream", color = Color(0xFFE50914), fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Text("YOUR NEXT GREAT LISTEN", color = Color(0xFF9A9AA3), fontSize = 9.sp, letterSpacing = 1.5.sp)
                }
                Surface(
                    modifier = Modifier.size(42.dp).clickable { searchOpen = !searchOpen },
                    shape = CircleShape,
                    color = Color(0xFF242429)
                ) { Box(contentAlignment = Alignment.Center) { Text("⌕", color = Color.White, fontSize = 28.sp) } }
                Spacer(Modifier.width(10.dp))
                Surface(
                    modifier = Modifier.size(42.dp).clickable { selectedTab = 3 },
                    shape = CircleShape,
                    color = Color(0xFFE50914)
                ) { Box(contentAlignment = Alignment.Center) { Text("C", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) } }
            }

            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(bottom = 8.dp)
            ) {
                if (searchOpen) {
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
                        color = Color(0xFF222228), shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Search audiobooks, authors and narrators", color = Color(0xFFB8B8C0), fontSize = 13.sp, modifier = Modifier.padding(16.dp))
                    }
                }
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Good evening", color = Color(0xFF9A9AA3), fontSize = 13.sp, modifier = Modifier.padding(start = 20.dp, top = 12.dp))
                    Text("Find your next escape.", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 20.dp, top = 4.dp, bottom = 16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF292127))
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth().background(Color(0xFF292127)).padding(20.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth(0.72f)) {
                                Text("TRENDING NOW", color = Color(0xFFFF626B), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.8.sp)
                                Spacer(Modifier.height(10.dp))
                                Text("Atomic Habits", color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 31.sp)
                                Text("James Clear  •  5 hr 35 min", color = Color(0xFFD0C7CC), fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
                                Spacer(Modifier.height(18.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Button(
                                        onClick = { currentBook = bookCatalog[0]; isPlaying = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE50914)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) { Text("▶  Play now", color = Color.White, fontWeight = FontWeight.Bold) }
                                    Spacer(Modifier.width(10.dp))
                                    Surface(
                                        modifier = Modifier.size(44.dp).clickable { isBookmarked = !isBookmarked },
                                        color = Color(0xFF44363D), shape = CircleShape
                                    ) { Box(contentAlignment = Alignment.Center) { Text(if (isBookmarked) "♥" else "♡", color = Color.White, fontSize = 23.sp) } }
                                }
                            }
                            Box(
                                modifier = Modifier.align(Alignment.CenterEnd).size(width = 78.dp, height = 150.dp)
                                    .clip(RoundedCornerShape(12.dp)).background(Color(0xFF45695B)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("A\nT\nO\nM\nI\nC", color = Color.White, fontSize = 12.sp, lineHeight = 14.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                    BookSection(
                        title = "Continue Listening",
                        books = listOf(bookCatalog[0], bookCatalog[3], bookCatalog[4]),
                        currentBook = currentBook,
                        onBookClick = { currentBook = it; isPlaying = true },
                        showProgress = true
                    )
                    BookSection(
                        title = "Popular Audiobooks",
                        books = bookCatalog,
                        currentBook = currentBook,
                        onBookClick = { currentBook = it; isPlaying = true }
                    )
                    BookSection(
                        title = "Top Narrators & Stories",
                        books = listOf(bookCatalog[1], bookCatalog[5], bookCatalog[2]),
                        currentBook = currentBook,
                        onBookClick = { currentBook = it; isPlaying = true }
                    )

                    Text("Categories", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 20.dp, top = 8.dp, bottom = 12.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp)) {
                        items(listOf("Self-Help", "Thriller", "Sci-Fi", "Business")) { category ->
                            Surface(
                                modifier = Modifier.clickable { selectedTab = 1; searchOpen = true },
                                color = Color(0xFF242429), shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(category, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp))
                            }
                        }
                    }
                    Spacer(Modifier.height(18.dp))
                }
            }
        }
    }
}

@Composable
private fun BookSection(
    title: String,
    books: List<AudioBook>,
    currentBook: AudioBook,
    onBookClick: (AudioBook) -> Unit,
    showProgress: Boolean = false
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("See all  ›", color = Color(0xFFFF626B), fontSize = 12.sp)
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp)) {
            items(books) { book ->
                Column(modifier = Modifier.width(142.dp).clickable { onBookClick(book) }) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(if (showProgress) 128.dp else 154.dp)
                            .clip(RoundedCornerShape(15.dp)).background(book.color).padding(12.dp)
                    ) {
                        Text(book.category.uppercase(), color = Color.White.copy(alpha = 0.75f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(
                            book.title,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            lineHeight = 21.sp,
                            modifier = Modifier.align(Alignment.BottomStart),
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (showProgress) {
                        androidx.compose.material3.LinearProgressIndicator(
                            progress = { if (book.title == currentBook.title) 0.36f else 0.58f },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp).height(4.dp).clip(CircleShape),
                            color = Color(0xFFE50914),
                            trackColor = Color(0xFF37373E)
                        )
                    }
                    Text(book.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 8.dp))
                    Text(book.author, color = Color(0xFF9A9AA3), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 3.dp))
                }
            }
        }
    }
}

@Composable
private fun MiniPlayer(
    book: AudioBook,
    playing: Boolean,
    progress: Float,
    onPlayPause: () -> Unit,
    onProgressChange: (Float) -> Unit
) {
    Surface(color = Color(0xFF222228), tonalElevation = 8.dp) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(44.dp).clip(RoundedCornerShape(9.dp)).background(book.color),
                    contentAlignment = Alignment.Center
                ) { Text("♫", color = Color.White, fontSize = 23.sp) }
                Column(modifier = Modifier.weight(1f).padding(start = 10.dp)) {
                    Text(book.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(book.author, color = Color(0xFF9A9AA3), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Surface(
                    modifier = Modifier.size(42.dp).clickable(onClick = onPlayPause),
                    color = Color(0xFFE50914), shape = CircleShape
                ) { Box(contentAlignment = Alignment.Center) { Text(if (playing) "Ⅱ" else "▶", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) } }
            }
            Slider(
                value = progress.coerceIn(0f, 1f),
                onValueChange = onProgressChange,
                modifier = Modifier.fillMaxWidth().height(24.dp),
                colors = androidx.compose.material3.SliderDefaults.colors(
                    thumbColor = Color(0xFFE50914),
                    activeTrackColor = Color(0xFFE50914),
                    inactiveTrackColor = Color(0xFF45454D)
                )
            )
        }
    }
}
