package com.rjnr.thaiwrter.data.models

import kotlinx.serialization.Serializable


@Serializable
data class ThaiCharacter(
    val id: Int,
    val character: String,
    val pronunciation: String,
    val svgPathData: String = "M14 127C11.6 38.2 7 55 28 33L1 15C1 15 26.9941 0.0325775 45 1C60.4269 1.82886 82 5.00001 82 15C82 25 82 127 82 127",
    val difficulty: Int = 0,     // 1-5 scale
    val category: String = ""    // consonant, vowel, tone mark, etc.
)