package com.bp.dinodata.use_cases

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log

/**
 * A collection of UseCases for managing and interfacing with a Text-To-Speech (TTS) engine.
 * This can be used to pronounce names or read text aloud.
 *
 * @see TextToSpeechUseCases.playText
 */
class TextToSpeechUseCases(context: Context) {
    private var isInitSuccessful = false

    private var ttsEngine: TextToSpeech = TextToSpeech(context) { status ->
        isInitSuccessful = (status == TextToSpeech.SUCCESS)
    }

    /**
     * Initiates TTS to read a given string aloud. By default, any previously-queued/in-progress
     * tasks are stopped immediately and replaced by this new text.
     * @param text The text to be read.
     * @param id A unique identifier for this reading.
     * @return True if the given text has been passed to the TTS engine;
     * false if the TTS engine is inactive.
     */
    fun playText(text: String, id: String): Boolean {
        if (isInitSuccessful) {
            if (ttsEngine.isSpeaking) {
                ttsEngine.stop()
            }
            Log.d("TTS", "Play $text")
            ttsEngine.speak(text, TextToSpeech.QUEUE_FLUSH, null, id)
            return true
        }
        else {
            Log.d("TTS", "No TTS initialised!")
            return false
        }
    }
}