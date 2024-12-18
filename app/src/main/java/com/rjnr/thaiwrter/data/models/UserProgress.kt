package com.rjnr.thaiwrter.data.models

import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
data class UserProgress(
    @PrimaryKey val characterId: Int,
    val lastReviewed: Long,
    val nextReviewDate: Long,
    val correctCount: Int,
    val incorrectCount: Int,
    val streak: Int,
    val easeFactor: Float    // For spaced repetition algorithm
)