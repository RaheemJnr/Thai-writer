package com.rjnr.thaiwrter.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import com.rjnr.thaiwrter.data.models.ThaiCharacter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SoundManager(private val context: Context) {
    private var soundPool: SoundPool? = null
    private val soundMap = mutableMapOf<Int, Int>() // Map Character ID to SoundPool ID

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        soundPool?.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) {
                Log.d("SoundManager", "Sound loaded: $sampleId")
            } else {
                Log.e("SoundManager", "Error loading sound: $sampleId, status: $status")
            }
        }
    }

    // Load sounds for a list of characters
    fun loadSoundsForCharacters(characters: List<ThaiCharacter>) {
        CoroutineScope(Dispatchers.IO).launch {
            characters.forEach { character ->
                try {
                    val resId = context.resources.getIdentifier(
                        "char_${character.id}", "raw", context.packageName
                    )
                    if (resId != 0) {
                        val soundId = soundPool?.load(context, resId, 1)
                        if (soundId != null) {
                            soundMap[character.id] = soundId
                        }
                    } else {
                        Log.w("SoundManager", "Audio file not found for char_${character.id}")
                    }
                } catch (e: Exception) {
                    Log.e("SoundManager", "Error loading sound for char_${character.id}", e)
                }
            }
        }
    }

    fun playSoundForCharacter(characterId: Int) {
        soundMap[characterId]?.let { soundId ->
            soundPool?.play(soundId, 1f, 1f, 1, 0, 1f)
        } ?: Log.w("SoundManager", "Sound not loaded for character ID: $characterId")
    }

    fun release() {
        soundPool?.release()
        soundPool = null
    }
}