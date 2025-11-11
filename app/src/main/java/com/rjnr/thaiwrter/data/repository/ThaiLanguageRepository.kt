package com.rjnr.thaiwrter.data.repository

import com.rjnr.thaiwrter.data.local.ThaiCharacterDao
import com.rjnr.thaiwrter.data.local.UserProgressDao
import com.rjnr.thaiwrter.data.models.THAI_CHARACTERS
import com.rjnr.thaiwrter.data.models.ThaiCharacter
import com.rjnr.thaiwrter.data.models.UserProgress
import com.rjnr.thaiwrter.data.models.toDomain
import com.rjnr.thaiwrter.data.models.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ThaiLanguageRepository(
        private val thaiCharacterDao: ThaiCharacterDao,
        private val userProgressDao: UserProgressDao
) {
    fun getDueReviews(): Flow<List<UserProgress>> =
            userProgressDao.getDueReviews(System.currentTimeMillis()).map { entities ->
                entities.map { it.toDomain() }
            }

    fun getCharactersByDifficulty(maxDifficulty: Int): Flow<List<ThaiCharacter>> =
            thaiCharacterDao.getCharactersByDifficulty(maxDifficulty).map { entities ->
                entities.map { it.toDomain().withFallback() }
            }

    fun getAllCharacters(): Flow<List<ThaiCharacter>> =
            thaiCharacterDao.getAllCharacters().map { entities ->
                entities.map { it.toDomain().withFallback() }
            }

    suspend fun updateUserProgress(progress: UserProgress) {
        userProgressDao.updateProgress(progress.toEntity())
    }

    fun getProgressForCharacter(characterId: Int): Flow<UserProgress?> =
            userProgressDao.getProgressForCharacter(characterId).map { entity ->
                entity?.toDomain()
            }

    private fun ThaiCharacter.withFallback(): ThaiCharacter {
        val fallback = THAI_CHARACTERS.find { it.id == id } ?: return this
        return copy(
                character = if (character.isBlank()) fallback.character else character,
                pronunciation =
                        if (pronunciation.isBlank()) fallback.pronunciation else pronunciation,
                strokes = if (strokes.isEmpty()) fallback.strokes else strokes,
                difficulty = if (difficulty == 0) fallback.difficulty else difficulty,
                category = if (category.isBlank()) fallback.category else category
        )
    }
}
