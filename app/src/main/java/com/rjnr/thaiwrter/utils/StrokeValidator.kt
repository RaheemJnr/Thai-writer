package com.rjnr.thaiwrter.utils

import com.rjnr.thaiwrter.data.models.Stroke
import com.rjnr.thaiwrter.ui.drawing.Point
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

class StrokeValidator {
    companion object {
        private const val TOLERANCE = 0.15f // Tolerance for stroke matching

        fun validateStroke(
            drawnPoints: List<Point>,
            targetStroke: Stroke,
            canvasWidth: Float,
            canvasHeight: Float
        ): Boolean {
            if (drawnPoints.size < 2) return false

            // Convert target stroke to canvas coordinates
            val startX = targetStroke.x1 * canvasWidth
            val startY = targetStroke.y1 * canvasHeight
            val endX = targetStroke.x2 * canvasWidth
            val endY = targetStroke.y2 * canvasHeight

            // Check start and end points
            val firstPoint = drawnPoints.first()
            val lastPoint = drawnPoints.last()

            // Calculate distances from drawn points to target line
            val startDistance = calculateDistance(firstPoint, Point(startX, startY))
            val endDistance = calculateDistance(lastPoint, Point(endX, endY))

            // Check if stroke follows the general direction
            val targetAngle = calculateAngle(startX, startY, endX, endY)
            val drawnAngle = calculateAngle(
                firstPoint.x,
                firstPoint.y,
                lastPoint.x,
                lastPoint.y
            )

            val angleDifference = abs(targetAngle - drawnAngle)

            // Check if drawn stroke follows the target path
            val maxDeviation = canvasWidth * TOLERANCE
            val isPathValid = drawnPoints.all { point ->
                distanceToLine(
                    point,
                    Point(startX, startY),
                    Point(endX, endY)
                ) < maxDeviation
            }

            return startDistance < maxDeviation &&
                    endDistance < maxDeviation &&
                    angleDifference < 30 &&
                    isPathValid
        }

        private fun calculateDistance(p1: Point, p2: Point): Float {
            val dx = p2.x - p1.x
            val dy = p2.y - p1.y
            return sqrt((dx * dx + dy * dy).toDouble()).toFloat()
        }

        private fun calculateAngle(x1: Float, y1: Float, x2: Float, y2: Float): Float {
            return atan2(y2 - y1, x2 - x1) * (180 / PI.toFloat())
        }

        private fun distanceToLine(point: Point, lineStart: Point, lineEnd: Point): Float {
            val numerator = abs(
                (lineEnd.y - lineStart.y) * point.x -
                        (lineEnd.x - lineStart.x) * point.y +
                        lineEnd.x * lineStart.y -
                        lineEnd.y * lineStart.x
            )

            val denominator = sqrt(
                ((lineEnd.y - lineStart.y) * (lineEnd.y - lineStart.y) +
                        (lineEnd.x - lineStart.x) * (lineEnd.x - lineStart.x)).toDouble()
            )

            return (numerator / denominator).toFloat()
        }
    }
}

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