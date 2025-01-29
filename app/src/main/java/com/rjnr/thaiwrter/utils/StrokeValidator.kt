package com.rjnr.thaiwrter.utils

import com.rjnr.thaiwrter.data.models.Stroke
import com.rjnr.thaiwrter.ui.drawing.Point
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt
import kotlin.math.*

object StrokeValidator {
    private const val RESAMPLE_POINTS = 64
    private const val DTW_THRESHOLD = 0.25f // Adjust based on testing

    fun validateStroke(
        drawnPoints: List<Point>,
        targetStroke: Stroke,
        canvasWidth: Float,
        canvasHeight: Float
    ): Boolean {
        // 1. Normalize and resample both strokes
        val drawnNormalized = normalizePoints(drawnPoints, canvasWidth, canvasHeight)
        val drawnResampled = resamplePoints(drawnNormalized, RESAMPLE_POINTS)

        val targetResampled = resamplePoints(targetStroke.points, RESAMPLE_POINTS)

        // 2. Calculate DTW distance
        val distance = calculateDTW(drawnResampled, targetResampled)

        // 3. Check direction similarity
        val dirSimilarity = directionSimilarity(
            drawnResampled.first(),
            drawnResampled.last(),
            targetResampled.first(),
            targetResampled.last()
        )

        return distance < DTW_THRESHOLD && dirSimilarity > 0.7f
    }

    private fun normalizePoints(
        points: List<Point>,
        width: Float,
        height: Float
    ): List<Point> {
        return points.map {
            Point(it.x / width, it.y / height)
        }
    }

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

    private fun calculateDTW(a: List<Point>, b: List<Point>): Float {
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

    private fun directionSimilarity(
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

    private fun pathLength(points: List<Point>): Float {
        return points.zipWithNext().sumOf {
            distanceBetween(it.first, it.second).toDouble()
        }.toFloat()
    }

    private fun distanceBetween(a: Point, b: Point): Float {
        return sqrt((a.x - b.x).pow(2) + (a.y - b.y).pow(2))
    }
}
//class StrokeValidator {
//    companion object {
//        private const val TOLERANCE = 0.15f // Tolerance for stroke matching
//
//        fun validateStroke(
//            drawnPoints: List<Point>,
//            targetStroke: Stroke,
//            canvasWidth: Float,
//            canvasHeight: Float
//        ): Boolean {
//            if (drawnPoints.size < 2) return false
//
//            // Convert target stroke to canvas coordinates
//            val startX = targetStroke.x1 * canvasWidth
//            val startY = targetStroke.y1 * canvasHeight
//            val endX = targetStroke.x2 * canvasWidth
//            val endY = targetStroke.y2 * canvasHeight
//
//            // Check start and end points
//            val firstPoint = drawnPoints.first()
//            val lastPoint = drawnPoints.last()
//
//            // Calculate distances from drawn points to target line
//            val startDistance = calculateDistance(firstPoint, Point(startX, startY))
//            val endDistance = calculateDistance(lastPoint, Point(endX, endY))
//
//            // Check if stroke follows the general direction
//            val targetAngle = calculateAngle(startX, startY, endX, endY)
//            val drawnAngle = calculateAngle(
//                firstPoint.x,
//                firstPoint.y,
//                lastPoint.x,
//                lastPoint.y
//            )
//
//            val angleDifference = abs(targetAngle - drawnAngle)
//
//            // Check if drawn stroke follows the target path
//            val maxDeviation = canvasWidth * TOLERANCE
//            val isPathValid = drawnPoints.all { point ->
//                distanceToLine(
//                    point,
//                    Point(startX, startY),
//                    Point(endX, endY)
//                ) < maxDeviation
//            }
//
//            return startDistance < maxDeviation &&
//                    endDistance < maxDeviation &&
//                    angleDifference < 30 &&
//                    isPathValid
//        }
//
//        private fun calculateDistance(p1: Point, p2: Point): Float {
//            val dx = p2.x - p1.x
//            val dy = p2.y - p1.y
//            return sqrt((dx * dx + dy * dy).toDouble()).toFloat()
//        }
//
//        private fun calculateAngle(x1: Float, y1: Float, x2: Float, y2: Float): Float {
//            return atan2(y2 - y1, x2 - x1) * (180 / PI.toFloat())
//        }
//
//        private fun distanceToLine(point: Point, lineStart: Point, lineEnd: Point): Float {
//            val numerator = abs(
//                (lineEnd.y - lineStart.y) * point.x -
//                        (lineEnd.x - lineStart.x) * point.y +
//                        lineEnd.x * lineStart.y -
//                        lineEnd.y * lineStart.x
//            )
//
//            val denominator = sqrt(
//                ((lineEnd.y - lineStart.y) * (lineEnd.y - lineStart.y) +
//                        (lineEnd.x - lineStart.x) * (lineEnd.x - lineStart.x)).toDouble()
//            )
//
//            return (numerator / denominator).toFloat()
//        }
//    }
//}

//object StrokeValidator {
//    private const val TOLERANCE = 0.15f
//
//    fun validateStroke(
//        drawnPoints: List<Point>,
//        targetStroke: Stroke
//    ): Boolean {
//        if (drawnPoints.size < 2) return false
//
//        // Get first and last points
//        val firstPoint = drawnPoints.first()
//        val lastPoint = drawnPoints.last()
//
//        // Calculate distances from drawn points to target stroke endpoints
//        val startDistance = calculateDistance(
//            firstPoint,
//            Point(targetStroke.x1, targetStroke.y1)
//        )
//        val endDistance = calculateDistance(
//            lastPoint,
//            Point(targetStroke.x2, targetStroke.y2)
//        )
//
//        // Calculate angles
//        val targetAngle = calculateAngle(
//            targetStroke.x1,
//            targetStroke.y1,
//            targetStroke.x2,
//            targetStroke.y2
//        )
//        val drawnAngle = calculateAngle(
//            firstPoint.x,
//            firstPoint.y,
//            lastPoint.x,
//            lastPoint.y
//        )
//
//        val angleDifference = abs(targetAngle - drawnAngle)
//
//        // Check if drawn stroke follows the target path
//        val maxDeviation = TOLERANCE
//        val isPathValid = drawnPoints.all { point ->
//            distanceToLine(
//                point,
//                Point(targetStroke.x1, targetStroke.y1),
//                Point(targetStroke.x2, targetStroke.y2)
//            ) < maxDeviation
//        }
//
//        return startDistance < maxDeviation &&
//                endDistance < maxDeviation &&
//                angleDifference < 30 &&
//                isPathValid
//    }
//
//    private fun calculateDistance(p1: Point, p2: Point): Float {
//        val dx = p2.x - p1.x
//        val dy = p2.y - p1.y
//        return sqrt(dx * dx + dy * dy)
//    }
//
//    private fun calculateAngle(x1: Float, y1: Float, x2: Float, y2: Float): Float {
//        return atan2(y2 - y1, x2 - x1) * (180 / PI.toFloat())
//    }
//
//    private fun distanceToLine(point: Point, lineStart: Point, lineEnd: Point): Float {
//        val numerator = abs(
//            (lineEnd.y - lineStart.y) * point.x -
//                    (lineEnd.x - lineStart.x) * point.y +
//                    lineEnd.x * lineStart.y -
//                    lineEnd.y * lineStart.x
//        )
//
//        val denominator = sqrt(
//            (lineEnd.y - lineStart.y) * (lineEnd.y - lineStart.y) +
//                    (lineEnd.x - lineStart.x) * (lineEnd.x - lineStart.x)
//        )
//
//        return numerator / denominator
//    }
//}