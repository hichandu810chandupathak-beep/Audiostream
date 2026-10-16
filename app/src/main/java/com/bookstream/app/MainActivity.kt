package com.bookstream.app

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bookstream.app.ui.BookStreamTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = AndroidColor.rgb(14, 14, 16)
        window.navigationBarColor = AndroidColor.rgb(14, 14, 16)
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = 0

        setContent {
            BookStreamTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BookStreamApp()
                }
            }
        }
    }
}

@Composable
private fun BookStreamApp() {
    com.bookstream.app.ui.BookStreamRoot()
}
