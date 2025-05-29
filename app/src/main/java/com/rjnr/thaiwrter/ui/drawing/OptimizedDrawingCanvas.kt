package com.rjnr.thaiwrter.ui.drawing

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.flow.SharedFlow
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Optimized drawing canvas with improved performance features:
 * - Path smoothing using Bezier curves
 * - Point reduction to minimize path complexity
 * - Hardware acceleration hints
 * - Efficient recomposition strategy
 */
@Composable
fun OptimizedDrawingCanvas(
    modifier: Modifier = Modifier,
    clearSignal: SharedFlow<Unit>,
    onStrokeFinished: (Path) -> Unit,
    onDragStartAction: () -> Unit,
    enabled: Boolean = true,
    strokeColor: Color = DrawingConfig.defaultStrokeColor,
    strokeWidthRatio: Float = DrawingConfig.DEFAULT_STROKE_WIDTH_RATIO
) {
    val density = LocalDensity.current
    
    // Drawing state
    var drawingState by remember { mutableStateOf(DrawingState()) }
    
    // Clear canvas when signal is received
    LaunchedEffect(Unit) {
        clearSignal.collect {
            drawingState = DrawingState()
        }
    }
    
    Canvas(
        modifier = modifier
            .graphicsLayer {
                // Enable hardware acceleration
                if (DrawingConfig.ENABLE_HARDWARE_ACCELERATION) {
                    compositingStrategy = CompositingStrategy.Offscreen
                }
            }
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                
                awaitEachGesture {
                    val down = awaitFirstDown()
                    onDragStartAction()
                    
                    // Start new path
                    drawingState = drawingState.copy(
                        currentPath = Path().apply { moveTo(down.position.x, down.position.y) },
                        lastPoint = down.position,
                        points = mutableListOf(down.position),
                        isDrawing = true
                    )
                    
                    // Handle drag
                    drag(down.id) { change ->
                        if (change.positionChange() != Offset.Zero) {
                            change.consume()
                            
                            val newPoint = change.position
                            val lastPoint = drawingState.lastPoint
                            
                            // Calculate distance from last point
                            val distance = (newPoint - lastPoint).getDistance()
                            
                            // Only add point if it's far enough (adaptive based on speed)
                            val minDistance = calculateMinDistance(drawingState.points, density.density)
                            
                            if (distance >= minDistance && drawingState.points.size < DrawingConfig.MAX_PATH_POINTS) {
                                // Add smooth curve instead of straight line
                                if (DrawingConfig.USE_BEZIER_SMOOTHING) {
                                    addSmoothPoint(drawingState, newPoint)
                                } else {
                                    drawingState.currentPath.lineTo(newPoint.x, newPoint.y)
                                }
                                
                                drawingState = drawingState.copy(
                                    lastPoint = newPoint,
                                    points = drawingState.points.apply { add(newPoint) }
                                )
                            }
                        }
                    }
                    
                    // Finish stroke
                    drawingState = drawingState.copy(isDrawing = false)
                    
                    // Optimize path before callback
                    val optimizedPath = if (drawingState.points.size > 10) {
                        optimizePath(drawingState.currentPath, drawingState.points)
                    } else {
                        drawingState.currentPath
                    }
                    onStrokeFinished(optimizedPath)
                }
            }
    ) {
        val strokeWidth = DrawingConfig.getStrokeWidth(min(size.width, size.height))
        
        // Draw with optimized stroke settings
        drawOptimizedPath(
            path = drawingState.currentPath,
            color = strokeColor,
            strokeWidth = strokeWidth
        )
    }
}

private data class DrawingState(
    val currentPath: Path = Path(),
    val lastPoint: Offset = Offset.Zero,
    val points: MutableList<Offset> = mutableListOf(),
    val isDrawing: Boolean = false
)

private fun Offset.getDistance(): Float {
    return sqrt(x * x + y * y)
}

private fun calculateMinDistance(points: List<Offset>, density: Float): Float {
    // Adaptive minimum distance based on drawing speed
    val baseMinDistance = DrawingConfig.MIN_POINT_DISTANCE * density
    
    if (points.size < 3) return baseMinDistance
    
    // Calculate recent drawing speed
    val recentPoints = points.takeLast(5)
    val distances = recentPoints.zipWithNext { a, b -> (b - a).getDistance() }
    val avgDistance = distances.average().toFloat()
    
    // Use config for adaptive distance calculation
    return DrawingConfig.calculateAdaptiveDistance(baseMinDistance, avgDistance)
}

private fun addSmoothPoint(state: DrawingState, newPoint: Offset) {
    val path = state.currentPath
    val lastPoint = state.lastPoint
    
    if (state.points.size < 2) {
        // Not enough points for smoothing
        path.lineTo(newPoint.x, newPoint.y)
    } else {
        // Use quadratic Bezier curve for smoothing
        val controlPoint = lastPoint
        val endPoint = Offset(
            (lastPoint.x + newPoint.x) / 2,
            (lastPoint.y + newPoint.y) / 2
        )
        
        path.quadraticTo(
            controlPoint.x, controlPoint.y,
            endPoint.x, endPoint.y
        )
    }
}

private fun DrawScope.drawOptimizedPath(
    path: Path,
    color: Color,
    strokeWidth: Float
) {
    drawPath(
        path = path,
        color = color,
        style = Stroke(
            width = strokeWidth,
            cap = DrawingConfig.strokeCap,
            join = DrawingConfig.strokeJoin,
            miter = DrawingConfig.strokeMiter,
            pathEffect = null // Avoid path effects for better performance
        ),
        alpha = 1f,
        colorFilter = null,
        blendMode = DrawScope.DefaultBlendMode
    )
}

private fun optimizePath(path: Path, points: List<Offset>): Path {
    // Simplify points using Douglas-Peucker algorithm
    val simplifiedPoints = simplifyPathDouglasPeucker(points, DrawingConfig.PATH_SIMPLIFICATION_EPSILON)
    
    if (simplifiedPoints.size < 2) return path
    
    // Create new optimized path
    return Path().apply {
        moveTo(simplifiedPoints.first().x, simplifiedPoints.first().y)
        
        if (DrawingConfig.USE_BEZIER_SMOOTHING && simplifiedPoints.size > 2) {
            // Use Catmull-Rom spline for smooth curves through all points
            for (i in 1 until simplifiedPoints.size) {
                val p0 = simplifiedPoints.getOrElse(i - 2) { simplifiedPoints[i - 1] }
                val p1 = simplifiedPoints[i - 1]
                val p2 = simplifiedPoints[i]
                val p3 = simplifiedPoints.getOrElse(i + 1) { simplifiedPoints[i] }
                
                // Calculate control points for cubic Bezier
                val cp1x = p1.x + (p2.x - p0.x) / 6f * DrawingConfig.SMOOTHING_FACTOR
                val cp1y = p1.y + (p2.y - p0.y) / 6f * DrawingConfig.SMOOTHING_FACTOR
                val cp2x = p2.x - (p3.x - p1.x) / 6f * DrawingConfig.SMOOTHING_FACTOR
                val cp2y = p2.y - (p3.y - p1.y) / 6f * DrawingConfig.SMOOTHING_FACTOR
                
                cubicTo(cp1x, cp1y, cp2x, cp2y, p2.x, p2.y)
            }
        } else {
            // Simple line connections
            simplifiedPoints.drop(1).forEach { point ->
                lineTo(point.x, point.y)
            }
        }
    }
}

/**
 * Extension function to implement Douglas-Peucker algorithm for path simplification
 */
fun simplifyPathDouglasPeucker(points: List<Offset>, epsilon: Float): List<Offset> {
    if (points.size < 3) return points
    
    // Find the point with the maximum distance
    var dmax = 0f
    var index = 0
    
    for (i in 1 until points.size - 1) {
        val d = perpendicularDistance(points[i], points.first(), points.last())
        if (d > dmax) {
            index = i
            dmax = d
        }
    }
    
    // If max distance is greater than epsilon, recursively simplify
    return if (dmax > epsilon) {
        // Recursive call
        val recResults1 = simplifyPathDouglasPeucker(points.subList(0, index + 1), epsilon)
        val recResults2 = simplifyPathDouglasPeucker(points.subList(index, points.size), epsilon)
        
        // Build the result list
        recResults1.dropLast(1) + recResults2
    } else {
        listOf(points.first(), points.last())
    }
}

private fun perpendicularDistance(point: Offset, lineStart: Offset, lineEnd: Offset): Float {
    val dx = lineEnd.x - lineStart.x
    val dy = lineEnd.y - lineStart.y
    
    if (dx == 0f && dy == 0f) {
        return (point - lineStart).getDistance()
    }
    
    val normalLength = sqrt(dx * dx + dy * dy)
    return abs((point.x - lineStart.x) * dy - (point.y - lineStart.y) * dx) / normalLength
} 