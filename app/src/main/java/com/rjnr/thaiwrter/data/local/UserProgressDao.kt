package com.rjnr.thaiwrter.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rjnr.thaiwrter.data.local.entities.UserProgressEntity
import com.rjnr.thaiwrter.data.models.UserProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {
    @Query("SELECT * FROM user_progress WHERE nextReviewDate <= :currentTime")
    fun getDueReviews(currentTime: Long): Flow<List<UserProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateProgress(progress: UserProgressEntity)

    @Query("SELECT * FROM user_progress WHERE characterId = :characterId")
    fun getProgressForCharacter(characterId: Int): Flow<UserProgressEntity?>
}