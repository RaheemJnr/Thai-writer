package com.rjnr.thaiwrter.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rjnr.thaiwrter.data.local.entities.ThaiCharacterEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface ThaiCharacterDao {
    @Query("SELECT * FROM thai_characters")
    fun getAllCharacters(): Flow<List<ThaiCharacterEntity>>

    @Query("SELECT * FROM thai_characters WHERE difficulty <= :maxDifficulty")
    fun getCharactersByDifficulty(maxDifficulty: Int): Flow<List<ThaiCharacterEntity>>

    @Query("SELECT * FROM thai_characters WHERE category = :category")
    fun getCharactersByCategory(category: String): Flow<List<ThaiCharacterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(characters: List<ThaiCharacterEntity>)

    @Query("SELECT COUNT(*) FROM thai_characters")
    suspend fun getCharacterCount(): Int
}