package com.bookstream.app.ui

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

/** Native Android TTS queued sentence-by-sentence for Hindi and supported regional languages. */
class AudioNarratorManager(context: Context) : TextToSpeech.OnInitListener {
    private var engine: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var ready = false
    private var released = false
    private var pending: Triple<String, String, String>? = null
    private var activeBookId: String? = null
    private var activeLanguage = "hi-IN"
    private var lastUtteranceId: String? = null
    private var playSequence = 0
    private var listener: ((Boolean, String?) -> Unit)? = null

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS || released) return
        ready = true
        engine?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                if (utteranceId?.startsWith("$playSequence-") == true) listener?.invoke(true, activeBookId)
            }
            override fun onDone(utteranceId: String?) {
                if (utteranceId == lastUtteranceId) listener?.invoke(false, activeBookId)
            }
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                if (utteranceId == lastUtteranceId) listener?.invoke(false, activeBookId)
            }
            override fun onError(utteranceId: String?, errorCode: Int) {
                if (utteranceId == lastUtteranceId) listener?.invoke(false, activeBookId)
            }
        })
        pending?.let { (text, id, lang) -> pending = null; speak(text, id, lang) }
    }
    fun setStateListener(callback: ((Boolean, String?) -> Unit)?) { listener = callback }
    fun speak(text: String, bookId: String, languageTag: String = "hi-IN") {
        if (released) return
        activeBookId = bookId
        activeLanguage = languageTag
        playSequence += 1
        if (!ready) { pending = Triple(text, bookId, languageTag); return }
        val tts = engine ?: return
        val locale = if (languageTag.startsWith("hi")) Locale("hi", "IN") else Locale.forLanguageTag(languageTag)
        val result = tts.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) tts.setLanguage(Locale.getDefault())
        tts.stop()
        val sentences = text.trim().split(Regex("(?<=[.!?।॥])\\s+")).map { it.trim() }.filter { it.isNotEmpty() }
        val queue = if (sentences.isEmpty()) listOf(text) else sentences
        val sequence = playSequence
        lastUtteranceId = sequence.toString() + "-" + queue.lastIndex + "-0"
        queue.forEachIndexed { index, sentence ->
            val pieces = if (sentence.length > 850) sentence.chunked(700) else listOf(sentence)
            pieces.forEachIndexed { pieceIndex, piece ->
                val id = "$sequence-$index-$pieceIndex"
                if (index == queue.lastIndex && pieceIndex == pieces.lastIndex) lastUtteranceId = id
                tts.speak(piece, if (index == 0 && pieceIndex == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD, null, id)
            }
        }
        listener?.invoke(true, bookId)
    }
    fun pause() {
        if (released) return
        engine?.stop()
        listener?.invoke(false, activeBookId)
    }
    fun resumeOrSpeak(text: String, bookId: String, languageTag: String = activeLanguage) = speak(text, bookId, languageTag)
    fun stop() {
        if (released) return
        pending = null
        engine?.stop()
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
