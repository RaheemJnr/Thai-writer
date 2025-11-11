package com.rjnr.thaiwrter.data.models

import com.rjnr.thaiwrter.data.local.entities.ThaiCharacterEntity
import com.rjnr.thaiwrter.data.local.entities.UserProgressEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val strokesJson = Json { ignoreUnknownKeys = true }

fun ThaiCharacterEntity.toDomain(): ThaiCharacter {
    val strokes =
            runCatching {
                if (strokeData.isBlank()) emptyList()
                else strokesJson.decodeFromString<List<String>>(strokeData)
            }
                    .getOrElse { emptyList() }

    return ThaiCharacter(
            id = id,
            character = character,
            pronunciation = pronunciation,
            strokes = strokes,
            difficulty = difficulty,
            category = category
    )
}

fun ThaiCharacter.toEntity(): ThaiCharacterEntity {
    val strokesPayload = runCatching { strokesJson.encodeToString(strokes) }.getOrElse { "[]" }
    return ThaiCharacterEntity(
            id = id,
            character = character,
            pronunciation = pronunciation,
            strokeData = strokesPayload,
            difficulty = difficulty,
            category = category
    )
}

fun UserProgressEntity.toDomain(): UserProgress {
    return UserProgress(
            characterId = characterId,
            lastReviewed = lastReviewed,
            nextReviewDate = nextReviewDate,
            correctCount = correctCount,
            incorrectCount = incorrectCount,
            streak = streak,
            easeFactor = easeFactor
    )
}

fun UserProgress.toEntity(): UserProgressEntity {
    return UserProgressEntity(
            characterId = characterId,
            lastReviewed = lastReviewed,
            nextReviewDate = nextReviewDate,
            correctCount = correctCount,
            incorrectCount = incorrectCount,
            streak = streak,
            easeFactor = easeFactor
    )
}
