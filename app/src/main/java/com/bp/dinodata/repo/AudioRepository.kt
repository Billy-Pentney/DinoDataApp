package com.bp.dinodata.repo

import android.util.Log
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException


enum class AudioType {
    mp3
}

class AudioRepository(
    private val storage: StorageReference,
    private val cacheDir: File,
    private val connectionChecker: IConnectionChecker
) : IAudioRepository {
    private val genusToAudioFileMap: MutableMap<String, String> = mutableMapOf()

    private val fNamePrefix = "tts_"
    private val regex = """$fNamePrefix([a-z]+)_\d+\.mp3""".toRegex()

    companion object {
        const val REMOTE_DIR = "audio/genera"
        const val LOG_TAG = "AudioRepo"
    }

    private val hasNetConnectivity
        get() = connectionChecker.hasNetworkAccess()

    init {
        prepopulateFileMap()
    }

    /**
     * Populates the genusToAudioFileMap property with the names of all existing audio files
     * in the temporary cache directory. This ensures that audios which have previously
     * been downloaded when the app was run in the past will continue to be re-used, even
     * when the app is restarted.
     */
    private fun prepopulateFileMap() {
        val files = cacheDir.listFiles() ?: emptyArray()
        var totalSuccessful = 0
        for (file in files) {
            val fName = file.name
            val genusName = extractGenusNameFromFilename(fName)
            if (genusName != null) {
                storeReferenceToAudio(genusName, file)
                totalSuccessful++
                Log.i(LOG_TAG, "Found cached file \'$fName\', with genus: \'$genusName\'")
            }
        }
        Log.i(LOG_TAG, "Successfully loaded $totalSuccessful audio files from cache")
    }

    /**
     * Attempts to retrieve the Text-to-speech audio file pronouncing the given genus name.
     * If the file has previously been played and is cached locally, then it will be retrieved
     * from the local copy; otherwise, if a network connection is present, this function
     * attempts to download the file from the firebase storage repository. Otherwise, if no
     * network is detected, the function will not attempt to retrieve the file.
     * @param genusName A string representing the name of the genus whose audio should be played.
     * @param onCompletion A callback which will be invoked with a status object representing the
     * result of the operation. If successful, this object contains a reference to the local audio
     * file; otherwise, it will indicate the reason for failure.
     */
    override fun getAudioForGenus(
        genusName: String,
        onCompletion: (AudioRetrievalStatus) -> Unit,
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
                onCompletion(AudioRetrievalStatus.Success(file))
                return
            }
            catch (ex: IOException) {
                Log.e(LOG_TAG, "Error when opening local temp file \'$tempPath\'", ex)
            }
        }

        if (!hasNetConnectivity) {
            Log.d(LOG_TAG, "Unable to fetch audio data due to lack of network connection")
            onCompletion(AudioRetrievalStatus.NoNetwork)
            return
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
                    onCompletion(AudioRetrievalStatus.Success(localAudioFile))
                }.addOnFailureListener { exc ->
                    // Handle any errors
                    Log.d(LOG_TAG, "Error occurred when fetching audio for \'$genusLower\'", exc)
                    onCompletion(AudioRetrievalStatus.ErrorFileNotFound)
                }
        }
        catch (ex: IOException) {
            Log.e(LOG_TAG, "Error when creating new temporary file", ex)
            onCompletion(AudioRetrievalStatus.ErrorFileNotFound)
        }
    }

    /**
     * Create a firebase storage reference for the TTS audio file for the given genus.
     * */
    private fun makePathReferenceFor(
        genusName: String,
        fileType: AudioType = AudioType.mp3
    ): StorageReference {
        val fName = "$genusName.$fileType".lowercase()
        return storage.child("$REMOTE_DIR/$fName")
    }

    /**
     * Create a new local temporary file to store the given genus audio.
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

    /**
     * Given the name of a temporary cached audio file, attempt to extract the genus name to which
     * that audio corresponds, e.g.
     *  "tts_acrocanthosaurus_1234asa019q30.mp3" would return "acrocanthosaurus" while
     *      "abcdjaso_102o01230103.mp3" would return null.
     * @param fileName The name of the file.
     * @return If successful, the genus name; otherwise null.
     * */
    private fun extractGenusNameFromFilename(fileName: String): String? {
        // Expects a string of the form "tts_<GENUS>_<ID>.mp3"
        val match = regex.find(fileName)
        return match?.let { it.groups[1]?.value }
    }

    private fun storeReferenceToAudio(genusName: String, file: File) {
        genusToAudioFileMap[genusName.lowercase()] = file.path
    }
}