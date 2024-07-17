package com.bp.dinodata.repo

import android.util.Log
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException


class AudioRepository(
    private val storage: StorageReference,
    private val cacheDir: File
) : IAudioRepository {
    private val genusToAudioFileMap: MutableMap<String, String> = mutableMapOf()

    private val fNamePrefix = "tts_"
    private val regex = """$fNamePrefix([a-z]+)_\d+\.mp3""".toRegex()

    companion object {
        const val REMOTE_DIR = "audio/genera"
        const val LOG_TAG = "AudioRepo"
    }

    init {
        prepopulateFileMap()
    }

    private fun prepopulateFileMap() {
        val files = cacheDir.listFiles() ?: emptyArray()
        for (file in files) {
            val fName = file.name
            val genusName = extractGenusNameFromFilename(fName)
            if (genusName != null) {
                storeReferenceToAudio(genusName, file)
                Log.i(LOG_TAG, "Found cached file \'$fName\', with genus: \'$genusName\'")
            }
        }
    }

    override fun getAudioForGenus(
        genusName: String,
        callback: (File) -> Unit,
        onError: () -> Unit
    ) {
        val genusLower = genusName.lowercase()

        if (genusLower in genusToAudioFileMap.keys) {
            // If we've previously downloaded the audio for this genus,
            // then reload it from the previous file

            Log.d(LOG_TAG, "Genus \'$genusLower\' previously used!")
            val tempPath = genusToAudioFileMap[genusLower]!!
            try {
                val file = File(tempPath)
                Log.d(LOG_TAG, "Loaded file from temp storage")
                callback(file)
                return
            }
            catch (ex: IOException) {
                Log.e(LOG_TAG, "Error when opening local temp file \'$tempPath\'", ex)
            }
        }

        // Otherwise, we'll have to fetch it and store it in a temp file

        Log.d("AudioRepo", "Audio for genus \'$genusLower\' has not been used in this session")
        val pathReference = makePathReferenceFor(genusLower)

        try {
            val localAudioFile = makeNewCacheAudioFile(genusLower)
            Log.d(LOG_TAG, "Creating cache file at ${localAudioFile.name} for genus \'$genusName\'")

            // Now fetch that data from Firebase
            pathReference
                .getFile(localAudioFile)
                .addOnSuccessListener {
                    Log.d(LOG_TAG, "Successfully retrieved audio for genus \'$genusLower\'")
                    // On success, cache the given file, so we can look it up on subsequent requests
                    // Local temp file has been created
                    storeReferenceToAudio(genusLower, localAudioFile)
                    callback(localAudioFile)
                }.addOnFailureListener { exc ->
                    // Handle any errors
                    Log.d(LOG_TAG, "Error occurred when fetching audio for \'$genusLower\'", exc)
                    onError()
                }
        }
        catch (ex: IOException) {
            Log.e(LOG_TAG, "Error when creating new temporary file", ex)
            onError()
            return
        }
    }

    // Create a reference with an initial file path and name
    private fun makePathReferenceFor(genusName: String): StorageReference {
        val fName = "$genusName.mp3".lowercase()
        return storage.child("$REMOTE_DIR/$fName")
    }

    /** Create a new local temporary file to store the given genus audio.
     * This creates and returns a file in the Cache Directory with a name of the form:
     * tts_<GENUS>_<ID>.mp3
     *      e.g. "tts_achelousaurus_3212091030901020.mp3"
     * */
    private fun makeNewCacheAudioFile(genusName: String, fType: String = "mp3"): File {
        return File.createTempFile(
            "$fNamePrefix${genusName}_",
            ".$fType",
            cacheDir
        )
    }

    /** Given the name of a temporary file, attempt to extract the genus name.
     * If the given filename does not match the expected format, return null. */
    private fun extractGenusNameFromFilename(fileName: String): String? {
        // Expects a string of the form "tts_<GENUS>_<ID>.mp3"
        val match = regex.find(fileName)
        return match?.let { it.groups[1]?.value }
    }

    private fun storeReferenceToAudio(genusName: String, file: File) {
        genusToAudioFileMap[genusName.lowercase()] = file.path
    }
}