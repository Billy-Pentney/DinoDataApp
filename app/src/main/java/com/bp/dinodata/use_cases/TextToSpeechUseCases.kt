package com.bp.dinodata.use_cases

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log

class TextToSpeechUseCases(
    context: Context
) {
    private var isInitSuccessful = false
    private var ttsEngine: TextToSpeech = TextToSpeech(
            context
    ) { status ->
        isInitSuccessful = (status == TextToSpeech.SUCCESS)
    }

    fun playText(text: String, id: String) {
        if (isInitSuccessful) {
            if (ttsEngine.isSpeaking) {
                ttsEngine.stop()
            }
            Log.d("TTS", "Play $text")
            ttsEngine.speak(text, TextToSpeech.QUEUE_FLUSH, null, id)
        }
        else {
            Log.d("TTS", "No TTS initialised!")
        }
    }
}