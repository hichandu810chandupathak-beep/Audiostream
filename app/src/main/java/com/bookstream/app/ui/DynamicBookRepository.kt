package com.bookstream.app.ui

import android.os.Handler
import android.os.Looper
import androidx.compose.ui.graphics.Color
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.net.URL
import java.util.concurrent.Executors

/** Best-effort public catalogue search. Providers may rate-limit; local curated results remain available. */
object DynamicBookRepository {
    private val executor = Executors.newSingleThreadExecutor()
    private val main = Handler(Looper.getMainLooper())
    @Volatile private var latestRequest = 0

    fun search(query: String, callback: (List<BookItem>, String?) -> Unit) {
        val clean = query.trim()
        if (clean.length < 2) {
            main.post { callback(emptyList(), null) }
            return
        }
        val request = ++latestRequest
        executor.execute {
            val books = LinkedHashMap<String, BookItem>()
            var googleFailed = false
            var libraryFailed = false
            try {
                searchGoogle(clean).forEach { books[it.id] = it }
            } catch (_: Exception) {
                googleFailed = true
            }
            // Always try Open Library, including immediately after a Google Books HTTP 429.
            try {
                searchOpenLibrary(clean).forEach { books.putIfAbsent(it.id, it) }
            } catch (_: Exception) {
                libraryFailed = true
            }
            val message = if (books.isEmpty() && googleFailed && libraryFailed) {
                "Online search is temporarily busy. Your offline and curated catalogue is still available."
            } else null
            main.post { if (request == latestRequest) callback(books.values.toList(), message) }
        }
    }

    private fun getJson(url: String): JSONObject {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.connectTimeout = 10000
        connection.readTimeout = 12000
        connection.requestMethod = "GET"
        connection.setRequestProperty("User-Agent", "BookStreamApp/1.0 (Android)")
        connection.setRequestProperty("Accept", "application/json")
        return try {
            val status = connection.responseCode
            if (status !in 200..299) {
                if (status == 429) throw IllegalStateException("Provider rate limited")
                throw IllegalStateException("Catalogue provider unavailable")
            }
            JSONObject(connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() })
        } finally {
            connection.disconnect()
        }
    }

    private fun encoded(query: String): String = URLEncoder.encode(query.trim(), "UTF-8")

    private fun searchGoogle(query: String): List<BookItem> {
        val root = getJson("https://www.googleapis.com/books/v1/volumes?q=${encoded(query)}&maxResults=20&printType=books")
        val items = root.optJSONArray("items") ?: return emptyList()
        return (0 until items.length()).mapNotNull { i ->
            val item = items.optJSONObject(i) ?: return@mapNotNull null
            val info = item.optJSONObject("volumeInfo") ?: return@mapNotNull null
            val title = info.optString("title").takeIf { it.isNotBlank() } ?: return@mapNotNull null
            val authorsArray = info.optJSONArray("authors")
            val authors = if (authorsArray == null || authorsArray.length() == 0) "Unknown author"
                else (0 until authorsArray.length()).map { authorsArray.optString(it) }.joinToString(", ")
            val description = info.optString("description").takeIf { it.isNotBlank() } ?: "No description supplied by Google Books."
            val images = info.optJSONObject("imageLinks")
            val cover = images?.optString("thumbnail")?.replace("http://", "https://")?.takeIf { it.startsWith("https://") }
            val preview = info.optString("previewLink").takeIf { it.startsWith("http") }
            val language = info.optString("language", "en")
            val tag = when (language) { "hi" -> "hi-IN"; "en" -> "en-IN"; "bn" -> "bn-IN"; "mr" -> "mr-IN"; "ur" -> "ur-IN"; "sa" -> "sa-IN"; else -> language }
            BookItem(
                id = "google-" + item.optString("id", i.toString()), title = title, author = authors,
                category = info.optJSONArray("categories")?.optString(0) ?: "Online books",
                duration = "Online preview", synopsis = description, fullStoryText = description,
                coverColor = Color(0xFF465A72), icon = "📚", languageTag = tag,
                coverUrl = cover, previewUrl = preview, isOnline = true
            )
        }
    }

    private fun searchOpenLibrary(query: String): List<BookItem> {
        val root = getJson("https://openlibrary.org/search.json?q=${encoded(query)}&limit=20")
        val docs = root.optJSONArray("docs") ?: return emptyList()
        return (0 until docs.length()).mapNotNull { i ->
            val doc = docs.optJSONObject(i) ?: return@mapNotNull null
            val title = doc.optString("title").takeIf { it.isNotBlank() } ?: return@mapNotNull null
            val key = doc.optString("key").takeIf { it.startsWith("/works/") } ?: return@mapNotNull null
            val authorsArray = doc.optJSONArray("author_name")
            val author = if (authorsArray == null || authorsArray.length() == 0) "Unknown author"
                else (0 until minOf(3, authorsArray.length())).joinToString(", ") { authorsArray.optString(it) }
            val coverId = doc.optInt("cover_i", 0)
            val cover = if (coverId > 0) "https://covers.openlibrary.org/b/id/$coverId-M.jpg" else null
            BookItem(
                id = "open-" + key.removePrefix("/works/"), title = title, author = author,
                category = "Open Library", duration = "Catalogue entry",
                synopsis = "Open Library catalogue entry. Full text depends on the edition's availability.",
                fullStoryText = "$title. By $author. This is an Open Library catalogue record. Open the edition link to check reading or borrowing availability.",
                coverColor = Color(0xFF526A5D), icon = "📖", languageTag = "en-IN",
                coverUrl = cover, previewUrl = "https://openlibrary.org$key", isOnline = true
            )
        }
    }
}
