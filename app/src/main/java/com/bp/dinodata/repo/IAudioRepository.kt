package com.bp.dinodata.repo

interface IAudioRepository {
    fun getAudioForGenus(
        genusName: String,
        onCompletion: (AudioRetrievalStatus) -> Unit,
    )
}