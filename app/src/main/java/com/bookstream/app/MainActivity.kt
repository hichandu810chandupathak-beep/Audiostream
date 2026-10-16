package com.bookstream.app

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.bookstream.app.ui.BookStreamRoot
import com.bookstream.app.ui.BookStreamTheme
import com.bookstream.app.ui.LocalAccountManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = AndroidColor.rgb(14, 14, 16)
        window.navigationBarColor = AndroidColor.rgb(14, 14, 16)
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = 0
        val initialSession = LocalAccountManager.currentSession(this)
        setContent {
            BookStreamTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    BookStreamRoot(initialSession = initialSession)
                }
            }
        }
    }
}
