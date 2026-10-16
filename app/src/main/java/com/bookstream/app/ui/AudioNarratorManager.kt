package com.bookstream.app.ui

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

/**
 * Native Android TTS owned by BookStreamRoot, so playback can continue while Compose screens change.
 * This is screen-session playback, not a foreground service that survives app closure.
 */
class AudioNarratorManager(context: Context) : TextToSpeech.OnInitListener {
    private var engine: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var ready = false
    private var released = false
    private var pending: Triple<String, String, String>? = null
    private var activeText = ""
    private var activeBookId: String? = null
    private var activeLanguage = "hi-IN"
    private var paused = false
    private var listener: ((Boolean, String?) -> Unit)? = null

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS || released) return
        ready = true
        engine?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) { listener?.invoke(true, activeBookId) }
            override fun onDone(utteranceId: String?) {
                if (utteranceId?.endsWith("-last") == true) listener?.invoke(false, activeBookId)
            }
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) { listener?.invoke(false, activeBookId) }
            override fun onError(utteranceId: String?, errorCode: Int) { listener?.invoke(false, activeBookId) }
        })
        pending?.let { (text, id, lang) -> pending = null; speak(text, id, lang) }
    }

    fun setStateListener(callback: ((Boolean, String?) -> Unit)?) { listener = callback }

    fun speak(text: String, bookId: String, languageTag: String = "hi-IN") {
        if (released) return
        activeText = text
        activeBookId = bookId
        activeLanguage = languageTag
        paused = false
        if (!ready) { pending = Triple(text, bookId, languageTag); return }
        val tts = engine ?: return
        val locale = Locale.forLanguageTag(languageTag)
        val result = tts.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts.setLanguage(Locale.getDefault())
        }
        tts.stop()
        val chunks = text.chunked(2500)
        chunks.forEachIndexed { index, chunk ->
            val id = if (index == chunks.lastIndex) "$bookId-last" else "$bookId-$index"
            tts.speak(chunk, if (index == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD, null, id)
        }
        listener?.invoke(true, bookId)
    }

    fun pause() {
        if (released) return
        engine?.stop()
        paused = true
        listener?.invoke(false, activeBookId)
    }

    /** Android TextToSpeech has no universal pause/resume API; resume restarts this narration. */
    fun resumeOrSpeak(text: String, bookId: String, languageTag: String = activeLanguage) {
        if (paused || activeBookId != bookId) speak(text, bookId, languageTag) else speak(text, bookId, languageTag)
    }

    fun stop() {
        if (released) return
        pending = null
        engine?.stop()
        paused = false
        listener?.invoke(false, activeBookId)
    }

    fun currentBookId(): String? = activeBookId

    fun release() {
        if (released) return
        released = true
        pending = null
        listener = null
        engine?.stop()
        engine?.shutdown()
        engine = null
        ready = false
    }
}
