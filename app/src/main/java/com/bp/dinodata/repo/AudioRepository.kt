package com.bp.dinodata.repo

import android.util.Log
import com.google.firebase.storage.StorageReference
import java.io.File

class AudioRepository(
    private val storage: StorageReference
) {
    private val genusToAudioFileMap: MutableMap<String, String> = mutableMapOf()

    fun getAudioForGenus(
        genusName: String,
        callback: (File) -> Unit,
        onError: () -> Unit
    ) {
//        if (genusName in genusToAudioFileMap.keys) {
//            // TODO - load from the cached file here
//        }

        val audioType = "mp3"
        val localPrefix = "tts_"

        val remoteDir = "audio/genera"

        val fname = "$genusName.$audioType".lowercase()

        Log.d("AudioRepository", "Attempt to retrieve audio from $fname")

        // Create a reference with an initial file path and name
        val pathReference = storage.child("$remoteDir/$fname")

        val localFile = File.createTempFile(localPrefix, audioType)

        pathReference
            .getFile(localFile)
            .addOnSuccessListener {
                genusToAudioFileMap[genusName] = localFile.name
                // Local temp file has been created
                callback(localFile)
            }.addOnFailureListener { exc ->
                // Handle any errors
                Log.d("AudioRepo", "Error occurred fetching audio for \'$genusName\'", exc)
                onError()
            }
    }

}