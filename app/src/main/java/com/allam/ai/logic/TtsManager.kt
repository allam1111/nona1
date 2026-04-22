package com.allam.ai.logic

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TtsManager(context: Context, private val onInitComplete: () -> Unit) {

    private var tts: TextToSpeech? = null
    private val TAG = "TtsManager"

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Default to Arabic/Egypt if available
                val result = tts?.setLanguage(Locale("ar", "EG"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts?.language = Locale.ENGLISH
                }
                onInitComplete()
            } else {
                Log.e(TAG, "Initialization failed")
            }
        }
    }

    fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun stop() {
        tts?.stop()
    }

    fun destroy() {
        tts?.shutdown()
    }
}
