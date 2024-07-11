package com.bp.dinodata.use_cases

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.core.net.toUri
import com.bp.dinodata.R
import com.bp.dinodata.repo.AudioRepository
import java.io.File
import java.util.Locale


/**
 * A collection of UseCases for managing and interfacing with a Text-To-Speech (TTS) engine.
 * This can be used to pronounce names or read text aloud.
 *
 * @see TextToSpeechUseCases.playText
 */
class TextToSpeechUseCases(
    private val context: Context,
    private val audioRepository: AudioRepository
) {
    private var isInitSuccessful = false

    private var mediaPlayer: MediaPlayer = MediaPlayer()

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


    fun fetchTTSandPlayGenus(
        genusName: String,
        callback: (Boolean) -> Unit
    ) {
        audioRepository.getAudioForGenus(genusName,
            callback = { ttsFile ->
                val success = playAudioFromFile(
                    ttsFile,
                    onPlay = { callback(true) },
                    onFail = { callback(false) }
                )
                callback(success)
            }, onError = {
                callback(false)
            }
        )
    }


    private fun playAudioFromFile(
        file: File,
        onPlay: () -> Unit,
        onFail: () -> Unit
    ): Boolean {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.setDataSource(context, file.toUri())
        mediaPlayer.prepare()
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener { onPlay() }
        mediaPlayer.setOnErrorListener { _, type, extra ->
            Log.d("TTSUseCases", "AudioError (type=$type, extra=$extra)")
            onFail()
            true
        }
        return true
    }

    /** Shut-down the TTS-engine, clearing its resources. */
    fun close() {
        if (isInitSuccessful) {
            ttsEngine.shutdown()
            isInitSuccessful = false
        }
    }
}