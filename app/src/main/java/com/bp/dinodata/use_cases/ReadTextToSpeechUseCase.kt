package com.bp.dinodata.use_cases

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log

class ReadTextToSpeechUseCase(context: Context): IHasAudioResources {
    private var isInitSuccessful = false
    private var ttsId: Int = 0

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
    operator fun invoke(text: String): Boolean {
        if (isInitSuccessful) {
            if (ttsEngine.isSpeaking) {
                ttsEngine.stop()
            }
            Log.d("TTS", "Play $text")
            ttsEngine.speak(text, TextToSpeech.QUEUE_FLUSH, null, ttsId.toString())
            ttsId++
            return true
        }
        else {
            Log.d("TTS", "No TTS initialised!")
            return false
        }
    }

    /** Shut-down the TTS-engine, clearing its resources. */
    override fun close() {
        if (isInitSuccessful) {
            ttsEngine.shutdown()
            isInitSuccessful = false
        }
    }
}