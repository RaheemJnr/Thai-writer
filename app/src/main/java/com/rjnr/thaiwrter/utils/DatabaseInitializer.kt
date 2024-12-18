package com.rjnr.thaiwrter.utils

import android.content.Context
import android.util.Log
import com.rjnr.thaiwrter.data.local.AppDatabase
import com.rjnr.thaiwrter.data.models.CharactersWrapper
import com.rjnr.thaiwrter.data.models.toEntity
import kotlinx.serialization.json.Json

//class DatabaseInitializer(
//    private val context: Context,
//    private val database: AppDatabase
//) {
//    suspend fun initializeDatabase() {
//        val json = context.assets.open("database/initial_characters.json").bufferedReader().use {
//            it.readText()
//        }
//
//        val type = object : TypeToken<List<ThaiCharacter>>() {}.type
//        val characters: List<ThaiCharacter> = Gson().fromJson(json, type)
//
//        database.thaiCharacterDao().insertAll(characters)
//    }
//}

//class DatabaseInitializer(
//    private val context: Context,
//    private val database: AppDatabase
//) {
//    private val json = Json {
//        ignoreUnknownKeys = true
//        prettyPrint = true
//        isLenient = true
//    }
//
//    suspend fun initializeDatabase() {
//        try {
//            val jsonString = context.assets.open("database/initial_characters.json")
//                .bufferedReader()
//                .use { it.readText() }
//
//            // Parse the outer structure
//            val charactersWrapper = json.decodeFromString<CharactersWrapper>(jsonString)
//
//            // Insert into database
//            database.thaiCharacterDao()
//                .insertAll(charactersWrapper.thai_characters.map { it.toEntity() })
//        } catch (e: Exception) {
//            Log.e("DatabaseInitializer", "Error initializing database", e)
//            throw e
//        }
//    }
//}

class DatabaseInitializer(
    private val context: Context,
    private val database: AppDatabase
) {
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
    }

    suspend fun initializeDatabase() {
        try {
            // Check if we need to initialize
            if (database.thaiCharacterDao().getCharacterCount() == 0) {
                val jsonString = context.assets.open("database/initial_characters.json")
                    .bufferedReader()
                    .use { it.readText() }

                val charactersWrapper = json.decodeFromString<CharactersWrapper>(jsonString)
                database.thaiCharacterDao().insertAll(charactersWrapper.thai_characters.map { it.toEntity() })
            }
        } catch (e: Exception) {
            Log.e("DatabaseInitializer", "Error initializing database", e)
            throw e
        }
    }
}