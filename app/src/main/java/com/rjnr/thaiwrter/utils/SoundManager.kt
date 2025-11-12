package com.rjnr.thaiwrter.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log


class SoundManager(appContext: Context) {
    private val context = appContext.applicationContext
    private var mediaPlayer: MediaPlayer? = null

    /**
     * Plays the sound for a given character ID.
     * This will stop any currently playing sound and start the new one.
     */
    fun playSoundForCharacter(characterId: Int) {
        // Stop and release any sound that is already playing
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            Log.w("SoundManager", "Error stopping previous media player", e)
            mediaPlayer = null
        }

        // Find the resource ID just like before
        val resId = context.resources.getIdentifier(
            "char_${characterId}",
            "raw",
            context.packageName
        )

        if (resId == 0) {
            Log.w("SoundManager", "Missing raw/char_${characterId}")
            return
        }

        // Create and play the sound
        try {
            mediaPlayer = MediaPlayer.create(context, resId)

            // Set a listener to clean up when it's done
            mediaPlayer?.setOnCompletionListener {
                it.release()
                mediaPlayer = null
            }

            mediaPlayer?.start()
            Log.d("SoundManager", "MediaPlayer playing char $characterId (resId $resId)")
        } catch (e: Exception) {
            Log.e("SoundManager", "MediaPlayer failed to start", e)
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    /**
     * Releases the media player when the app is closed or ViewModel is cleared.
     */
    fun release() {
        try {
            mediaPlayer?.release()
        } catch (e: Exception) {
            Log.e("SoundManager", "Error on release", e)
        }
        mediaPlayer = null
    }
}

//class SoundManager(appContext: Context) {
//
//    private val context = appContext.applicationContext
//
//    private val soundPool: SoundPool = SoundPool.Builder()
//        .setMaxStreams(6)
//        .setAudioAttributes(
//            AudioAttributes.Builder()
//                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
//                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
//                .build()
//        )
//        .build()
//
//    // Map<CharacterId, SoundId>
//    private val soundMap = mutableMapOf<Int, Int>()
//    // Track which SoundIds are fully loaded
//    private val loaded = mutableSetOf<Int>()
//
//    init {
//        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
//            if (status == 0) {
//                loaded += sampleId
//                Log.d("SoundManager", "Loaded soundId=$sampleId")
//            } else {
//                Log.e("SoundManager", "Failed load soundId=$sampleId status=$status")
//            }
//        }
//    }
//
//    fun loadSoundsForCharacters(characters: List<ThaiCharacter>) {
//        // Load on main or IO is fine; SoundPool decodes internally.
//        characters.forEach { ch ->
//            val resId = context.resources.getIdentifier("char_${ch.id}", "raw", context.packageName)
//            Log.d("SoundManager", "Loading char_${ch.id} resId=$resId")
//            if (resId == 0) {
//                Log.w("SoundManager", "Missing raw/char_${ch.id}")
//                return@forEach
//            }
//            val soundId = soundPool.load(context, resId, 1)
//            if (soundId == 0) {
//                Log.e("SoundManager", "load() returned 0 for char_${ch.id}")
//            } else {
//                soundMap[ch.id] = soundId
//            }
//        }
//    }
//
//    fun playSoundForCharacter(characterId: Int) {
//        val soundId = soundMap[characterId]
//        if (soundId == null) {
//            Log.w("SoundManager", "No soundId for char $characterId")
//            return
//        }
//        if (soundId !in loaded) {
//            // Optional: queue or retry later
//            Log.d("SoundManager", "Not loaded yet for char $characterId")
//            return
//        }
//        val streamId = soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
//        Log.d("SoundManager", "Playing char $characterId soundId=$soundId streamId=$streamId")
//        if (streamId == 0) {
//            Log.e("SoundManager", "play() failed for char $characterId (soundId=$soundId)")
//        }
//    }
//
//    fun release() {
//        soundPool.release()
//        soundMap.clear()
//        loaded.clear()
//    }
//}
