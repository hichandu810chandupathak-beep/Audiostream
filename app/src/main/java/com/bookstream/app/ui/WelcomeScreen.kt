package com.bookstream.app.ui

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Background = Color(0xFF0E0E10)
private val SurfaceDark = Color(0xFF18181C)
private val Accent = Color(0xFFE50914)
private val TextPrimary = Color(0xFFF7F7F8)
private val TextSecondary = Color(0xFF9A9AA3)

private enum class AppRoute { Welcome, Auth, Home, Detail }

@Composable
fun BookStreamRoot(initialSession: LocalUserSession? = null) {
    val context = LocalContext.current
    var activeSession by remember { mutableStateOf(initialSession) }
    var route by remember { mutableStateOf(if (initialSession != null) AppRoute.Home else AppRoute.Welcome) }
    var selectedBookId by remember { mutableStateOf(BookData.catalog.first().id) }
    var selectedOnlineBook by remember { mutableStateOf<BookItem?>(null) }
    var currentBookId by remember { mutableStateOf(BookData.catalog.first().id) }
    var isPlaying by remember { mutableStateOf(false) }
    var libraryIds by remember { mutableStateOf(LocalAccountManager.savedBookIds(context)) }
    var onlineResults by remember { mutableStateOf<List<BookItem>>(emptyList()) }
    var onlineError by remember { mutableStateOf<String?>(null) }
    val narrator = remember { AudioNarratorManager(context.applicationContext) }

    DisposableEffect(narrator) {
        narrator.setStateListener { playing, id -> isPlaying = playing; if (id != null) currentBookId = id }
        onDispose { narrator.setStateListener(null); narrator.release() }
    }

    fun bookById(id: String): BookItem? = BookData.find(id) ?: onlineResults.firstOrNull { it.id == id }
    fun playBook(id: String) {
        bookById(id)?.let { book ->
            currentBookId = id
            isPlaying = true
            narrator.speak(book.fullStoryText, id, book.languageTag)
        }
    }
    Crossfade(targetState = route, label = "bookStreamNavigation") { screen ->
        when (screen) {
            AppRoute.Welcome -> WelcomeScreen(onGetStarted = { route = AppRoute.Auth })
            AppRoute.Auth -> AuthScreen(
                onSubmit = { name, identifier, password, signUp ->
                    val error = if (signUp) LocalAccountManager.register(context, name, identifier, password) else LocalAccountManager.signIn(context, identifier, password)
                    if (error == null) { activeSession = LocalAccountManager.currentSession(context); route = AppRoute.Home }
                    error
                },
                onGuest = { LocalAccountManager.signInGuest(context); activeSession = LocalAccountManager.currentSession(context); route = AppRoute.Home },
                onGoogle = { email ->
                    val error = LocalAccountManager.googleDemoSession(context, email)
                    if (error == null) { activeSession = LocalAccountManager.currentSession(context); route = AppRoute.Home }
                },
                onPhoneOtp = { phone, otp ->
                    val error = LocalAccountManager.signInPhoneOtp(context, phone, otp)
                    if (error == null) { activeSession = LocalAccountManager.currentSession(context); route = AppRoute.Home }
                    error
                },
                onResetPassword = { identifier, newPassword, _ -> LocalAccountManager.resetPassword(context, identifier, newPassword) }
            )
            AppRoute.Home -> HomeScreen(
                currentBookId = currentBookId, isPlaying = isPlaying, libraryIds = libraryIds,
                userDisplayName = activeSession?.name ?: "BookStream reader", isGuest = activeSession?.isGuest ?: true,
                onBookClick = { id ->
                    selectedOnlineBook = onlineResults.firstOrNull { it.id == id }
                    selectedBookId = id
                    route = AppRoute.Detail
                },
                onPlay = { playBook(it) },
                onPause = { narrator.pause(); isPlaying = false },
                onResume = { bookById(currentBookId)?.let { narrator.resumeOrSpeak(it.fullStoryText, currentBookId, it.languageTag); isPlaying = true } },
                onLibraryClick = { id ->
                    val updated = if (id in libraryIds) libraryIds - id else libraryIds + id
                    libraryIds = updated; LocalAccountManager.saveBookIds(context, updated)
                },
                onLogout = {
                    narrator.stop(); LocalAccountManager.logout(context); activeSession = null; isPlaying = false
                    onlineResults = emptyList(); route = AppRoute.Auth
                },
                onlineResults = onlineResults,
                onlineError = onlineError,
                onSearchOnline = { query ->
                    onlineError = null
                    DynamicBookRepository.search(query) { results, error ->
                        onlineResults = results
                        onlineError = error ?: if (results.isEmpty()) "No online books found. Try another title or author." else null
                    }
                }
            )
            AppRoute.Detail -> BookDetailScreen(
                bookId = selectedBookId, bookOverride = selectedOnlineBook,
                isPlaying = isPlaying && currentBookId == selectedBookId,
                isInLibrary = selectedBookId in libraryIds,
                onBack = { route = AppRoute.Home },
                onPlay = { playBook(it) },
                onPause = { narrator.pause(); isPlaying = false },
                onToggleLibrary = { id ->
                    val updated = if (id in libraryIds) libraryIds - id else libraryIds + id
                    libraryIds = updated; LocalAccountManager.saveBookIds(context, updated)
                }
            )
        }
    }
}

@Composable
fun BookStreamTheme(content: @Composable () -> Unit) {
    val colors = androidx.compose.material3.darkColorScheme(
        primary = Accent,
        onPrimary = Color.White,
        background = Background,
        onBackground = TextPrimary,
        surface = SurfaceDark,
        onSurface = TextPrimary,
        secondary = Color(0xFFFF626B),
        onSecondary = Color.White
    )
    MaterialTheme(colorScheme = colors, content = content)
}

@Composable
private fun WelcomeScreen(onGetStarted: () -> Unit) {
    val context = LocalContext.current

    // Optional welcome cue: missing or invalid res/raw/welcome_audio.mp3 is a safe no-op.
    DisposableEffect(context) {
        var player: MediaPlayer? = null
        try {
            val audioId = context.resources.getIdentifier(
                "welcome_audio",
                "raw",
                context.packageName
            )
            if (audioId != 0) {
                player = MediaPlayer.create(context, audioId)
                player?.setOnCompletionListener { completed ->
                    runCatching { completed.release() }
                    if (player === completed) player = null
                }
                player?.start()
            }
        } catch (_: Exception) {
            runCatching { player?.release() }
            player = null
        }

        onDispose {
            val activePlayer = player
            player = null
            if (activePlayer != null) {
                runCatching { if (activePlayer.isPlaying) activePlayer.stop() }
                runCatching { activePlayer.release() }
            }
        }
    }

    var logoVisible by remember { mutableStateOf(false) }
    var taglineVisible by remember { mutableStateOf(false) }
    var buttonVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        logoVisible = true
        kotlinx.coroutines.delay(350)
        taglineVisible = true
        kotlinx.coroutines.delay(250)
        buttonVisible = true
    }

    val pulse by rememberInfiniteTransition(label = "ctaPulse").animateFloat(
        initialValue = 1f,
        targetValue = 1.025f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ctaScale"
    )

    Box(
        modifier = Modifier.fillMaxSize().background(Background).padding(horizontal = 28.dp, vertical = 24.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = logoVisible,
                enter = fadeIn(tween(650)) + scaleIn(initialScale = 0.78f, animationSpec = tween(700))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(Accent),
                        contentAlignment = Alignment.Center
                    ) {
                        BookMark(modifier = Modifier.size(50.dp))
                    }
                    Spacer(Modifier.height(24.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Book",
                            color = TextPrimary,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-1.5).sp
                        )
                        Text(
                            text = "Stream",
                            color = Accent,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-1.5).sp
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "AUDIOBOOKS. WITHOUT LIMITS.",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.2.sp
                    )
                }
            }

            Spacer(Modifier.height(26.dp))
            AnimatedVisibility(
                visible = taglineVisible,
                enter = fadeIn(tween(650)) + slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(650)
                )
            ) {
                Text(
                    text = "The Netflix of Audiobooks",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(34.dp))
            AnimatedVisibility(
                visible = buttonVisible,
                enter = fadeIn(tween(500)) + slideInVertically(initialOffsetY = { it / 3 })
            ) {
                Button(
                    onClick = onGetStarted,
                    modifier = Modifier
                        .width(220.dp)
                        .height(56.dp)
                        .graphicsLayer(scaleX = pulse, scaleY = pulse),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Accent,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    Text("Get Started", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Developed with ❤️ by Chandan Pathak",
                color = TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = "BookStream v1.0",
                color = Color(0xFF696971),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun BookMark(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val book = Path().apply {
            moveTo(size.width * 0.12f, size.height * 0.16f)
            lineTo(size.width * 0.42f, size.height * 0.20f)
            quadraticTo(size.width * 0.50f, size.height * 0.22f, size.width * 0.50f, size.height * 0.30f)
            lineTo(size.width * 0.50f, size.height * 0.86f)
            quadraticTo(size.width * 0.34f, size.height * 0.76f, size.width * 0.12f, size.height * 0.78f)
            close()
            moveTo(size.width * 0.88f, size.height * 0.16f)
            lineTo(size.width * 0.58f, size.height * 0.20f)
            quadraticTo(size.width * 0.50f, size.height * 0.22f, size.width * 0.50f, size.height * 0.30f)
            lineTo(size.width * 0.50f, size.height * 0.86f)
            quadraticTo(size.width * 0.66f, size.height * 0.76f, size.width * 0.88f, size.height * 0.78f)
            close()
        }
        drawPath(book, color = Color.White, style = Stroke(width = size.width * 0.055f))
        for (i in 0..2) {
            val y = size.height * (0.37f + i * 0.12f)
            drawLine(Color.White, androidx.compose.ui.geometry.Offset(size.width * 0.20f, y), androidx.compose.ui.geometry.Offset(size.width * 0.40f, y + size.height * 0.025f), strokeWidth = size.width * 0.035f)
            drawLine(Color.White, androidx.compose.ui.geometry.Offset(size.width * 0.60f, y + size.height * 0.025f), androidx.compose.ui.geometry.Offset(size.width * 0.80f, y), strokeWidth = size.width * 0.035f)
        }
    }
}

@Composable
private fun BrowsePlaceholder(onBack: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = Background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Welcome to", color = TextSecondary, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            Text("BookStream", color = TextPrimary, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(12.dp))
            Text(
                "Your audiobook library is coming soon.",
                color = TextSecondary,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(28.dp))
            Button(
                onClick = onBack,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Accent)
            ) {
                Text("Back to Welcome")
            }
        }
    }
}
