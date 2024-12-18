package com.rjnr.thaiwrter.data.models

import kotlinx.serialization.Serializable

@Serializable
data class CharactersWrapper(
    val thai_characters: List<ThaiCharacter>
)