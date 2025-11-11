package com.rjnr.thaiwrter.utils

import android.content.Context
import android.util.Log
import com.rjnr.thaiwrter.data.local.AppDatabase
import com.rjnr.thaiwrter.data.models.THAI_CHARACTERS
import com.rjnr.thaiwrter.data.models.toEntity

class DatabaseInitializer(private val context: Context, private val database: AppDatabase) {
    suspend fun initializeDatabase() {
        try {
            // Check if we need to initialize
            if (database.thaiCharacterDao().getCharacterCount() == 0) {
                database.thaiCharacterDao().insertAll(THAI_CHARACTERS.map { it.toEntity() })
            }
        } catch (e: Exception) {
            Log.e("DatabaseInitializer", "Error initializing database", e)
            throw e
        }
    }
}
