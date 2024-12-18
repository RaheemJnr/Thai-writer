package com.rjnr.thaiwrter.data.repository

import com.rjnr.thaiwrter.data.local.ThaiCharacterDao
import com.rjnr.thaiwrter.data.local.UserProgressDao
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
        userProgressDao.getDueReviews(System.currentTimeMillis())
            .map { entities -> entities.map { it.toDomain() } }

    fun getCharactersByDifficulty(maxDifficulty: Int): Flow<List<ThaiCharacter>> =
        thaiCharacterDao.getCharactersByDifficulty(maxDifficulty)
            .map { entities -> entities.map { it.toDomain() } }

    suspend fun updateUserProgress(progress: UserProgress) {
        userProgressDao.updateProgress(progress.toEntity())
    }

    fun getProgressForCharacter(characterId: Int): Flow<UserProgress?> =
        userProgressDao.getProgressForCharacter(characterId)
            .map { entity -> entity?.toDomain() }
}