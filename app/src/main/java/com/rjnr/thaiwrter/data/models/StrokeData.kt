package com.rjnr.thaiwrter.data.models

import kotlinx.serialization.Serializable

@Serializable
data class StrokeData(
    val strokes: List<Stroke>
)

@Serializable
data class Stroke(
    val x1: Float,
    val y1: Float,
    val x2: Float,
    val y2: Float
)
