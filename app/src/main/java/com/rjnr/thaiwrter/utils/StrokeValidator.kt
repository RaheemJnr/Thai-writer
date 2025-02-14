package com.rjnr.thaiwrter.utils

import com.rjnr.thaiwrter.data.models.Point
import com.rjnr.thaiwrter.data.models.Stroke
import kotlin.math.pow
import kotlin.math.sqrt

//object StrokeValidator {
    const val RESAMPLE_POINTS = 64
    const val DTW_THRESHOLD = 0.25f // Adjust based on testing
//
//    fun validateStroke(
//        drawnPoints: List<Point>,
//        targetStroke: Stroke,
//        canvasWidth: Float,
//        canvasHeight: Float
//    ): Boolean {
//        // 1. Normalize and resample both strokes
//        val drawnNormalized = normalizePoints(drawnPoints, canvasWidth, canvasHeight)
//        val drawnResampled = resamplePoints(drawnNormalized, RESAMPLE_POINTS)
//
//        val targetResampled = resamplePoints(targetStroke.points, RESAMPLE_POINTS)
//
//        // 2. Calculate DTW distance
//        val distance = calculateDTW(drawnResampled, targetResampled)
//
//        // 3. Check direction similarity
//        val dirSimilarity = directionSimilarity(
//            drawnResampled.first(),
//            drawnResampled.last(),
//            targetResampled.first(),
//            targetResampled.last()
//        )
//
//        return distance < DTW_THRESHOLD && dirSimilarity > 0.7f
//    }
//
    private fun normalizePoints(
        points: List<Point>,
        width: Float,
        height: Float
    ): List<Point> {
        return points.map {
            Point(it.x / width, it.y / height)
        }
    }
//
    private fun resamplePoints(points: List<Point>, targetCount: Int): List<Point> {
        if (points.size < 2) return points
        val interval = pathLength(points) / (targetCount - 1)
        var currentDistance = 0f
        val newPoints = mutableListOf(points.first())
        var prevPoint = points.first()

        for (i in 1 until points.size) {
            val dist = distanceBetween(prevPoint, points[i])
            if (currentDistance + dist >= interval) {
                val ratio = (interval - currentDistance) / dist
                val newX = prevPoint.x + ratio * (points[i].x - prevPoint.x)
                val newY = prevPoint.y + ratio * (points[i].y - prevPoint.y)
                newPoints.add(Point(newX, newY))
                currentDistance = 0f
                prevPoint = Point(newX, newY)
            } else {
                currentDistance += dist
            }
        }

        while (newPoints.size < targetCount) {
            newPoints.add(points.last())
        }
        return newPoints
    }
//
    fun calculateDTW(a: List<Point>, b: List<Point>): Float {
        val matrix = Array(a.size) { FloatArray(b.size) { Float.MAX_VALUE } }
        matrix[0][0] = distanceBetween(a[0], b[0])

        for (i in 1 until a.size) {
            for (j in 1 until b.size) {
                val cost = distanceBetween(a[i], b[j])
                matrix[i][j] = cost + minOf(
                    matrix[i-1][j],
                    matrix[i][j-1],
                    matrix[i-1][j-1]
                )
            }
        }
        return matrix.last().last() / a.size
    }
//
    fun directionSimilarity(
        startA: Point, endA: Point,
        startB: Point, endB: Point
    ): Float {
        val vecA = Point(endA.x - startA.x, endA.y - startA.y)
        val vecB = Point(endB.x - startB.x, endB.y - startB.y)
        val dot = vecA.x * vecB.x + vecA.y * vecB.y
        val magA = sqrt(vecA.x.pow(2) + vecA.y.pow(2))
        val magB = sqrt(vecB.x.pow(2) + vecB.y.pow(2))
        return (dot / (magA * magB + 1e-8f)).coerceIn(-1f, 1f)
    }

    fun pathLength(points: List<Point>): Float {
        return points.zipWithNext().sumOf {
            distanceBetween(it.first, it.second).toDouble()
        }.toFloat()
    }

     fun distanceBetween(a: Point, b: Point): Float {
        return sqrt((a.x - b.x).pow(2) + (a.y - b.y).pow(2))
    }
//}