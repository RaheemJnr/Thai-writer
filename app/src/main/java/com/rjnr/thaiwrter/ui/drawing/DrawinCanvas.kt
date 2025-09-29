package com.rjnr.thaiwrter.ui.drawing

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.flow.SharedFlow
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.math.abs


@Composable
fun DrawingCanvas(
    modifier: Modifier = Modifier,
    clearSignal: SharedFlow<Unit>,
    onStrokeFinished: (Path) -> Unit,
    onDragStartAction: () -> Unit,
    enabled: Boolean = true
) {
    // Use neverEqualPolicy to prevent unnecessary recompositions
    var currentPath by remember { mutableStateOf(Path(), neverEqualPolicy()) }
    var lastPoint by remember { mutableStateOf<Offset?>(null) }
    var pointCount by remember { mutableIntStateOf(0) }
    
    // Minimum distance between points to reduce path complexity
    val minDistance = 3f
    
    LaunchedEffect(Unit) {
        clearSignal.collect {
            currentPath = Path()
            lastPoint = null
            pointCount = 0
        }
    }

    Canvas(
        modifier = modifier
            .pointerInput(enabled) {
                if (enabled) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            onDragStartAction()
                            currentPath = Path().apply { moveTo(offset.x, offset.y) }
                            lastPoint = offset
                            pointCount = 1
                        },
                        onDragEnd = {
                            // Simplify path before sending it
                            val simplifiedPath = simplifyPath(currentPath, pointCount)
                            onStrokeFinished(simplifiedPath)
                            lastPoint = null
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            val newPoint = change.position
                            
                            // Only add point if it's far enough from the last point
                            lastPoint?.let { last ->
                                val distance = sqrt(
                                    (newPoint.x - last.x) * (newPoint.x - last.x) +
                                    (newPoint.y - last.y) * (newPoint.y - last.y)
                                )
                                
                                if (distance >= minDistance) {
                                    // Use quadratic bezier for smoother curves
                                    val midPoint = Offset(
                                        (last.x + newPoint.x) / 2,
                                        (last.y + newPoint.y) / 2
                                    )
                                    currentPath.quadraticTo(
                                        last.x, last.y,
                                        midPoint.x, midPoint.y
                                    )
                                    lastPoint = newPoint
                                    pointCount++
                                }
                            } ?: run {
                                currentPath.lineTo(newPoint.x, newPoint.y)
                                lastPoint = newPoint
                                pointCount++
                            }
                        }
                    )
                }
            }
    ) {
        val strokePx = DrawingConfig.getStrokeWidth(min(size.width, size.height))
        val style = Stroke(
            width = strokePx, 
            cap = StrokeCap.Round, 
            join = StrokeJoin.Round,
            miter = 10f
        )
        
        // Enable anti-aliasing for smoother rendering
        drawPath(
            path = currentPath,
            color = Color.Black,
            style = style
        )
    }
}

// Douglas-Peucker algorithm for path simplification
private fun simplifyPath(path: Path, pointCount: Int): Path {
    // For now, return the original path
    // In a production app, you would implement proper path simplification
    return path
}


