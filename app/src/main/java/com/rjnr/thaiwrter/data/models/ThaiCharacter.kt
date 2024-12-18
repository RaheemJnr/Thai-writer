package com.rjnr.thaiwrter.data.models

import kotlinx.serialization.Serializable


@Serializable
data class ThaiCharacter(
    val id: Int,
    val character: String,
    val pronunciation: String,
    val strokeData: StrokeData,  // JSON string of stroke coordinates
    val difficulty: Int,     // 1-5 scale
    val category: String     // consonant, vowel, tone mark, etc.
)