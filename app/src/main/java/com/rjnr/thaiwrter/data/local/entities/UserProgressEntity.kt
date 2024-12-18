package com.rjnr.thaiwrter.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val characterId: Int,
    val lastReviewed: Long,
    val nextReviewDate: Long,
    val correctCount: Int,
    val incorrectCount: Int,
    val streak: Int,
    val easeFactor: Float
)