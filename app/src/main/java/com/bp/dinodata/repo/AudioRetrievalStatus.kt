package com.bp.dinodata.repo

import java.io.File

sealed class AudioRetrievalStatus {
    data class Success(val file: File): AudioRetrievalStatus()
    data object NoNetwork: AudioRetrievalStatus()
    data object ErrorFileNotFound: AudioRetrievalStatus()
}

sealed class AudioPlayStatus {
    data object Success : AudioPlayStatus()

    data class MediaPlayerError(val type: Int, val extra: Int) : AudioPlayStatus()

    data object MissingNetwork: AudioPlayStatus()
    data object FileNotFound: AudioPlayStatus()
    data object UnknownError: AudioPlayStatus()
}