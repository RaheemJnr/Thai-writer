package com.rjnr.thaiwrter.ui.drawing

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin

/**
 * Configuration object for drawing performance and appearance settings
 */
object DrawingConfig {
    // Performance settings
    const val MIN_POINT_DISTANCE = 2f // Minimum distance between points in dp
    const val ADAPTIVE_DISTANCE_FACTOR = 0.3f // Factor for adaptive distance based on speed
    const val MAX_ADAPTIVE_DISTANCE = 10f // Maximum adaptive distance in dp
    const val PATH_SIMPLIFICATION_EPSILON = 2f // Epsilon for Douglas-Peucker algorithm
    
    // Smoothing settings
    const val USE_BEZIER_SMOOTHING = true // Enable/disable Bezier curve smoothing
    const val SMOOTHING_FACTOR = 0.5f // How much to smooth the path (0-1)
    
    // Drawing appearance
    const val DEFAULT_STROKE_WIDTH_RATIO = 0.36f // Stroke width as ratio of canvas size - increased for bolder lines
    const val MIN_STROKE_WIDTH = 25f // Minimum stroke width in dp - increased for bolder lines
    const val MAX_STROKE_WIDTH = 80f // Maximum stroke width in dp - increased for bolder lines
    
    // Canvas settings
    const val ENABLE_HARDWARE_ACCELERATION = true
    const val USE_ANTIALIASING = true
    const val CANVAS_BACKGROUND_COLOR = 0x00000000 // Transparent
    
    // Touch responsiveness
    const val TOUCH_SLOP_MULTIPLIER = 0.5f // Reduce touch slop for more responsive drawing
    const val PREDICTION_ENABLED = true // Enable touch prediction for smoother lines
    
    // Frame rate optimization
    const val TARGET_FRAME_RATE = 120 // Target frame rate for smooth drawing
    const val ENABLE_FRAME_PACING = true // Enable frame pacing for consistent performance
    
    // Memory optimization
    const val MAX_PATH_POINTS = 1000 // Maximum points in a single path
    const val ENABLE_PATH_RECYCLING = true // Reuse path objects when possible
    
    // Debug settings
    const val SHOW_PERFORMANCE_OVERLAY = false // Show FPS and performance metrics
    const val SHOW_TOUCH_POINTS = false // Show touch points for debugging
    
    // Stroke style
    val strokeCap = StrokeCap.Round
    val strokeJoin = StrokeJoin.Round
    const val strokeMiter = 10f
    
    // Colors
    val defaultStrokeColor = Color.Black
    val guideStrokeColor = Color(0xFF5B4CE0) // Purple
    val correctStrokeColor = Color.Green
    
    /**
     * Get optimized stroke width based on canvas size
     */
    fun getStrokeWidth(canvasSize: Float): Float {
        val width = canvasSize * DEFAULT_STROKE_WIDTH_RATIO
        return width.coerceIn(MIN_STROKE_WIDTH, MAX_STROKE_WIDTH)
    }
    
    /**
     * Calculate adaptive minimum distance based on drawing speed
     */
    fun calculateAdaptiveDistance(baseDistance: Float, averageSpeed: Float): Float {
        val adaptiveDistance = baseDistance + (averageSpeed * ADAPTIVE_DISTANCE_FACTOR)
        return adaptiveDistance.coerceAtMost(MAX_ADAPTIVE_DISTANCE)
    }
} 