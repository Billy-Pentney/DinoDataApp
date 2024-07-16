package com.bp.dinodata.use_cases


interface IHasAudioResources {
    // Release all resources held by this object
    fun close()
}



class AudioPronunciationUseCases(
    val playPrerecordedAudio: PlayPrerecordedAudioUseCase,
//    val readTextToSpeech: ReadTextToSpeechUseCase
): IHasAudioResources {

    override fun close() {
//        readTextToSpeech.close()
        playPrerecordedAudio.close()
    }
}


