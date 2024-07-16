package com.bp.dinodata.repo

import java.io.File

interface IAudioRepository {
    fun getAudioForGenus(
        genusName: String,
        callback: (File) -> Unit,
        onError: () -> Unit
    )
}