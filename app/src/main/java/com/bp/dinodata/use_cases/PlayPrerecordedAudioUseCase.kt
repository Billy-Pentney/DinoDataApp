package com.bp.dinodata.use_cases

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.bp.dinodata.repo.AudioRepository
import java.io.File

class PlayPrerecordedAudioUseCase(
    private val context: Context,
    private val audioRepository: AudioRepository
): IHasAudioResources {
    private var mediaPlayer: MediaPlayer = MediaPlayer()

    private var lastUri: Uri? = null

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
        val uri = file.toUri()

        if (uri != lastUri) {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.reset()
            // If we've switched to a new request, we must reset, then add the new datasource
            mediaPlayer.setDataSource(context, uri)
            // Now, re-prepare the new source
            mediaPlayer.prepareAsync()
        }
        else {
            // The player is in PlaybackCompleted state, so do NOT prepare it again
            mediaPlayer.start()
        }

        mediaPlayer.setOnPreparedListener { mp ->
            // When the player is prepared, start it immediately
            mp.start()
        }
        mediaPlayer.setOnCompletionListener {
            // Copy the last successfully played URI
            lastUri = uri
            onPlay()
        }
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