package com.bp.dinodata.use_cases

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.core.net.toUri
import com.bp.dinodata.repo.AudioRepository
import java.io.File


interface IHasAudioResources {
    // Release all resources held by this object
    fun close()
}



class AudioPronunciationUseCases(
    val playPrerecordedAudio: PlayPrerecordedAudioUseCase,
    val readTextToSpeech: ReadTextToSpeechUseCase
): IHasAudioResources {

    override fun close() {
        readTextToSpeech.close()
        playPrerecordedAudio.close()
    }
}



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


class PlayPrerecordedAudioUseCase(
    private val context: Context,
    private val audioRepository: AudioRepository
): IHasAudioResources {
    private var mediaPlayer: MediaPlayer = MediaPlayer()

    operator fun invoke(
        genusName: String,
        callback: (Boolean) -> Unit
    ) {
        audioRepository.getAudioForGenus(genusName,
            callback = { ttsFile ->
                playAudioFromFileAsync(
                    ttsFile,
                    onPlay = { callback(true) },
                    onFail = { callback(false) }
                )
            },
            onError = { callback(false) }
        )
    }

    private fun playAudioFromFileAsync(
        file: File,
        onPlay: () -> Unit,
        onFail: () -> Unit
    ) {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.setDataSource(context, file.toUri())
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener { mp ->
            // When the player is prepared, start it immediately
            mp.start()
        }
        mediaPlayer.setOnCompletionListener { onPlay() }
        mediaPlayer.setOnErrorListener { _, type, extra ->
            Log.d("TTSUseCases", "AudioError (type=$type, extra=$extra)")
            onFail()
            // Indicate that this error was handled, to avoid further exceptions
            true
        }
    }

    override fun close() {
        mediaPlayer.release()
    }
}