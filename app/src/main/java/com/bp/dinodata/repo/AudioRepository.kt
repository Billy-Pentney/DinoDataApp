package com.bp.dinodata.repo

import android.util.Log
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException


class AudioRepository(
    private val storage: StorageReference
) {
    private val genusToAudioFileMap: MutableMap<String, String> = mutableMapOf()

    companion object {
        const val REMOTE_DIR = "audio/genera"
        const val LOG_TAG = "AudioRepo"
    }



    fun getAudioForGenus(
        genusName: String,
        callback: (File) -> Unit,
        onError: () -> Unit
    ) {
        if (genusName in genusToAudioFileMap.keys) {
            // If we've previously downloaded the audio for this genus,
            // then reload it from the previous file

            Log.d(LOG_TAG, "Genus \'$genusName\' previously used!")
            val tempPath = genusToAudioFileMap[genusName]!!
            try {
                val file = File(tempPath)
                Log.d(LOG_TAG, "Loaded file from temp storage")
                callback(file)
            }
            catch (ex: IOException) {
                Log.e(LOG_TAG, "Error when opening local temp file \'$tempPath\'", ex)
                onError()
            }
        }
        else {
            // Otherwise, we'll have to fetch it and store it in a temp file

            Log.d("AudioRepo", "Audio for genus \'$genusName\' not previously used")
            val fType = "mp3"
            val fName = "$genusName.$fType".lowercase()
            val localPrefix = "tts_"
            Log.d(LOG_TAG, "Creating new temp audio file for genus \'$fName\'")

            // Create a reference with an initial file path and name
            val pathReference = storage.child("$REMOTE_DIR/$fName")

            try {
                val localFile = File.createTempFile(localPrefix, fType)
                // Now fetch that data from Firebase
                pathReference
                    .getFile(localFile)
                    .addOnSuccessListener {
                        Log.d(LOG_TAG, "Successfully retrieved audio for genus \'$genusName\'")
                        // On success, cache the given file, so we can look it up on subsequent requests
                        genusToAudioFileMap[genusName] = localFile.path
                        // Local temp file has been created
                        callback(localFile)
                    }.addOnFailureListener { exc ->
                        // Handle any errors
                        Log.d(LOG_TAG, "Error occurred when fetching audio for \'$genusName\'", exc)
                        onError()
                    }
            }
            catch (ex: IOException) {
                Log.e(LOG_TAG, "Error when creating new temporary file", ex)
                onError()
                return
            }




        }
    }
}