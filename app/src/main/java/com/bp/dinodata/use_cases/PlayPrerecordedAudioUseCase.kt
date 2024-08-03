package com.bp.dinodata.use_cases

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.bp.dinodata.repo.AudioPlayStatus
import com.bp.dinodata.repo.AudioRetrievalStatus
import com.bp.dinodata.repo.IAudioRepository
import java.io.File

class PlayPrerecordedAudioUseCase(
    private val context: Context,
    private val audioRepository: IAudioRepository
): IHasAudioResources {
    private var _mediaPlayer: MediaPlayer? = MediaPlayer()
    private var _lastUriPlayed: Uri? = null

    companion object {
        const val TAG = "MediaPlayerUseCase"
    }

    operator fun invoke(
        genusName: String,
        onCompletion: (AudioPlayStatus) -> Unit
    ) {
        audioRepository.getAudioForGenus(genusName,
            onCompletion = { status ->

                when (status) {
                    is AudioRetrievalStatus.Success ->  {
                        playAudioFromFileAsync(
                            status.file,
                            onCompletion = onCompletion
                        )
                    }
                    AudioRetrievalStatus.ErrorFileNotFound -> {
                        onCompletion(AudioPlayStatus.FileNotFound)
                    }
                    AudioRetrievalStatus.NoNetwork -> {
                        onCompletion(AudioPlayStatus.MissingNetwork)
                    }
                }
            }
        )
    }

    private fun playAudioFromFileAsync(
        file: File,
        onCompletion: (AudioPlayStatus) -> Unit,
    ) {
        val uri = file.toUri()

        val mp = _mediaPlayer ?: MediaPlayer()

        if (_mediaPlayer == null) {
            Log.i(TAG, "No MediaPlayer. Recreating...")
            _mediaPlayer = mp
        }

        if (mp.isPlaying) {
            mp.stop()
        }

        if (uri != _lastUriPlayed) {
            mp.reset()
            // If we've switched to a new request, we must reset, then add the new datasource
            mp.setDataSource(context, uri)
            // Now, re-prepare the new source
            mp.prepareAsync()
        }
        else {
            // The player is in PlaybackCompleted state, so do NOT prepare it again
            mp.start()
        }

        mp.setOnPreparedListener { mediaPlayer ->
            // When the player is prepared, start it immediately
            mediaPlayer.start()
        }
        mp.setOnCompletionListener {
            // Copy the last successfully played URI
            _lastUriPlayed = uri
            onCompletion(AudioPlayStatus.Success)
        }
        mp.setOnErrorListener { _, type, extra ->
            Log.d("TTSUseCases", "AudioError (type=$type, extra=$extra)")
            onCompletion(AudioPlayStatus.MediaPlayerError(type, extra))
            // Indicate that this error was handled, to avoid further exceptions
            true
        }
    }

    override fun close() {
        Log.i(TAG, "Released mediaPlayer")
        _mediaPlayer?.release()
        _mediaPlayer = null
    }
}