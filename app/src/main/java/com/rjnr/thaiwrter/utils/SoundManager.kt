package com.rjnr.thaiwrter.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log
import com.rjnr.thaiwrter.data.models.ThaiCharacter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SoundManager(appContext: Context) {

    private val context = appContext.applicationContext

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(6)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
        )
        .build()

    // Map<CharacterId, SoundId>
    private val soundMap = mutableMapOf<Int, Int>()
    // Track which SoundIds are fully loaded
    private val loaded = mutableSetOf<Int>()

    init {
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) {
                loaded += sampleId
                Log.d("SoundManager", "Loaded soundId=$sampleId")
            } else {
                Log.e("SoundManager", "Failed load soundId=$sampleId status=$status")
            }
        }
    }

    fun loadSoundsForCharacters(characters: List<ThaiCharacter>) {
        // Load on main or IO is fine; SoundPool decodes internally.
        characters.forEach { ch ->
            val resId = context.resources.getIdentifier("char_${ch.id}", "raw", context.packageName)
            Log.d("SoundManager", "Loading char_${ch.id} resId=$resId")
            if (resId == 0) {
                Log.w("SoundManager", "Missing raw/char_${ch.id}")
                return@forEach
            }
            val soundId = soundPool.load(context, resId, 1)
            if (soundId == 0) {
                Log.e("SoundManager", "load() returned 0 for char_${ch.id}")
            } else {
                soundMap[ch.id] = soundId
            }
        }
    }

    fun playSoundForCharacter(characterId: Int) {
        val soundId = soundMap[characterId]
        if (soundId == null) {
            Log.w("SoundManager", "No soundId for char $characterId")
            return
        }
        if (soundId !in loaded) {
            // Optional: queue or retry later
            Log.d("SoundManager", "Not loaded yet for char $characterId")
            return
        }
        val streamId = soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
        Log.d("SoundManager", "Playing char $characterId soundId=$soundId streamId=$streamId")
        if (streamId == 0) {
            Log.e("SoundManager", "play() failed for char $characterId (soundId=$soundId)")
        }
    }

    fun release() {
        soundPool.release()
        soundMap.clear()
        loaded.clear()
    }
}

//class SoundManager(private val context: Context) {
//    private var mediaPlayer: MediaPlayer? = null
//
//    fun playSoundForCharacter(characterId: Int) {
//        // Stop and release any previously playing sound
//        stopAndReleasePlayer()
//
//        // Find the audio resource ID by name (e.g., "char_1")
//        val resId = context.resources.getIdentifier(
//            "char_$characterId", "raw", context.packageName
//        )
//
//        if (resId == 0) {
//            Log.e("SoundManager", "Audio file not found for char_$characterId")
//            return
//        }
//
//        // Create a new MediaPlayer instance for the specific sound
//        mediaPlayer = MediaPlayer.create(context, resId)
//
//        Log.d("SoundManager", "Audio duration: ${mediaPlayer?.duration}ms")
//
//
//        // Set a listener to release resources once playback is complete
//        mediaPlayer?.setOnCompletionListener {
//            Log.d("SoundManager", "Playback complete. Releasing MediaPlayer.")
//            it.release()
//            mediaPlayer = null
//        }
//
//        // Set a listener for errors
//        mediaPlayer?.setOnErrorListener { mp, what, extra ->
//            Log.e("SoundManager", "MediaPlayer Error: what=$what, extra=$extra")
//            stopAndReleasePlayer()
//            true // Indicates we've handled the error
//        }
//
//        // Start playing the sound
//        try {
//            mediaPlayer?.start()
//            Log.d("SoundManager", "Playing sound for char_$characterId")
//        } catch (e: IllegalStateException) {
//            Log.e("SoundManager", "Error starting MediaPlayer", e)
//            stopAndReleasePlayer()
//        }
//    }
//
//    private fun stopAndReleasePlayer() {
//        mediaPlayer?.let {
//            if (it.isPlaying) {
//                it.stop()
//            }
//            it.release()
//        }
//        mediaPlayer = null
//    }
//
//    // Call this when the app is closing or the ViewModel is cleared
//    fun release() {
//        stopAndReleasePlayer()
//    }
//}