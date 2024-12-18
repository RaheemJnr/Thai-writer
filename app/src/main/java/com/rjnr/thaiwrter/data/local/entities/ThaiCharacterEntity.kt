package com.rjnr.thaiwrter.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "thai_characters")
data class ThaiCharacterEntity(
    @PrimaryKey val id: Int,
    val character: String,
    val pronunciation: String,
    val strokeData: String,
    val difficulty: Int,
    val category: String
)