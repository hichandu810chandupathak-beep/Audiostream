package com.bookstream.app.ui

import android.os.Handler
import android.os.Looper
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.net.URL
import java.util.concurrent.Executors
import androidx.compose.ui.graphics.Color

/** Google Books + Open Library public search. Full copyrighted book files are not provided by these search endpoints. */
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
            try {
                val books = LinkedHashMap<String, BookItem>()
                searchGoogle(clean).forEach { books[it.id] = it }
                searchOpenLibrary(clean).forEach { books.putIfAbsent(it.id, it) }
                main.post { if (request == latestRequest) callback(books.values.toList(), null) }
            } catch (e: Exception) {
                main.post { if (request == latestRequest) callback(emptyList(), e.message ?: "Book search is temporarily unavailable.") }
            }
        }
    }

    private fun getJson(url: String): JSONObject {
        val connection = (URL(url).openConnection() as HttpURLConnection)
        connection.connectTimeout = 8000
        connection.readTimeout = 10000
        connection.setRequestProperty("User-Agent", "BookStream-Android/1.0")
        return try {
            if (connection.responseCode !in 200..299) throw IllegalStateException("HTTP ${connection.responseCode}")
            JSONObject(connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() })
        } finally { connection.disconnect() }
    }

    private fun searchGoogle(query: String): List<BookItem> {
        val q = URLEncoder.encode(query, "UTF-8")
        val root = getJson("https://www.googleapis.com/books/v1/volumes?q=$q&maxResults=20&printType=books")
        val items = root.optJSONArray("items") ?: return emptyList()
        return (0 until items.length()).mapNotNull { i ->
            val item = items.optJSONObject(i) ?: return@mapNotNull null
            val info = item.optJSONObject("volumeInfo") ?: return@mapNotNull null
            val title = info.optString("title").takeIf { it.isNotBlank() } ?: return@mapNotNull null
            val authorsArray = info.optJSONArray("authors")
            val authors = if (authorsArray == null) "Unknown author" else (0 until authorsArray.length()).map { authorsArray.optString(it) }.joinToString(", ")
            val description = info.optString("description").takeIf { it.isNotBlank() } ?: "No description supplied by Google Books."
            val images = info.optJSONObject("imageLinks")
            val cover = images?.optString("thumbnail")?.replace("http://", "https://")?.takeIf { it.startsWith("https://") }
            val preview = info.optString("previewLink").takeIf { it.startsWith("http") }
            val language = info.optString("language", "en")
            val tag = when (language) { "hi" -> "hi-IN"; "en" -> "en-IN"; "bn" -> "bn-IN"; "mr" -> "mr-IN"; "ur" -> "ur-IN"; "sa" -> "sa-IN"; else -> language }
            BookItem(
                id = "google-" + item.optString("id", i.toString()),
                title = title, author = authors, category = info.optJSONArray("categories")?.optString(0) ?: "Online books",
                duration = "Online preview", synopsis = description,
                fullStoryText = description,
                coverColor = Color(0xFF465A72), icon = "📚", languageTag = tag,
                coverUrl = cover, previewUrl = preview, isOnline = true
            )
        }
    }

    private fun searchOpenLibrary(query: String): List<BookItem> {
        val q = URLEncoder.encode(query, "UTF-8")
        val root = getJson("https://openlibrary.org/search.json?q=$q&limit=20")
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
            val workUrl = "https://openlibrary.org$key"
            BookItem(
                id = "open-" + key.removePrefix("/works/"), title = title, author = author,
                category = "Open Library", duration = "Catalogue entry",
                synopsis = "Open Library catalogue entry. The public search API does not guarantee full text for this edition.",
                fullStoryText = "$title. By $author. This is an Open Library catalogue record. Open the edition link to check reading or borrowing availability.",
                coverColor = Color(0xFF526A5D), icon = "📖", languageTag = "en-IN",
                coverUrl = cover, previewUrl = workUrl, isOnline = true
            )
        }
    }
}
