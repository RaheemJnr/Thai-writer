package com.rjnr.thaiwrter.ui.drawing

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import com.rjnr.thaiwrter.data.models.Point
import com.rjnr.thaiwrter.data.models.ThaiCharacter
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt


//@Composable
//fun DrawingCanvas(
//    modifier: Modifier = Modifier,
//    shouldClear: Boolean = false,
//    onDrawingComplete: (List<Point>, Int, Int) -> Unit
//) {
//    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
//    var currentPath by remember { mutableStateOf(Path()) }
//    val currentPoints = remember { mutableStateListOf<Point>() }
//    val paths = remember { mutableStateListOf<Path>() }
//
//    // React to shouldClear changes
//    LaunchedEffect(shouldClear) {
//        if (shouldClear) {
//            currentPath = Path()
//            currentPoints.clear()
//            paths.clear()
//        }
//    }
//
//    Canvas(
//        modifier = modifier
//            .background(Color.White)
//            .onSizeChanged { size ->
//                canvasSize = size
//            }
//            .pointerInput(Unit) {
//                detectDragGestures(
//                    onDragStart = { offset ->
//                        currentPath = Path().apply {
//                            moveTo(offset.x, offset.y)
//                        }
//                        currentPoints.clear()
//                        currentPoints.add(Point(offset.x, offset.y))
//                    },
//                    onDragEnd = {
//                        paths.add(currentPath)
//                        onDrawingComplete(currentPoints.toList(), canvasSize.width, canvasSize.height)
//                    },
//                    onDrag = { change, _ ->
//                        change.consume()
//                        val newPoint = change.position
//                        currentPath.lineTo(newPoint.x, newPoint.y)
//                        currentPoints.add(Point(newPoint.x, newPoint.y))
//                    }
//                )
//            }
//    ) {
//        // Draw background
//        drawRect(Color.White, Offset.Zero, size)
//
//        // Draw completed paths
//        paths.forEach { path ->
//            drawPath(
//                path = path,
//                color = Color.Black,
//                style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
//            )
//        }
//
//        // Draw current path
//        drawPath(
//            path = currentPath,
//            color = Color.Black,
//            style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
//        )
//    }
//}

@Composable
fun DrawingCanvas(
    modifier: Modifier = Modifier,
    shouldClear: Boolean = false,
    pathColor: Color = Color.Black,
    onDrawingComplete: (List<Point>, Int, Int) -> Unit
) {
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var currentPath by remember { mutableStateOf(Path()) }
    val currentPoints = remember { mutableStateListOf<Point>() }
    val paths = remember { mutableStateListOf<Path>() }
    val pointSpeeds = remember { mutableStateListOf<Float>() }

    // For tracking drawing speed
    var lastPoint by remember { mutableStateOf<Offset?>(null) }
    var lastTime by remember { mutableLongStateOf(0L) }

    // React to shouldClear changes
    LaunchedEffect(shouldClear) {
        if (shouldClear) {
            currentPath = Path()
            currentPoints.clear()
            pointSpeeds.clear()
            paths.clear()
            lastPoint = null
        }
    }

    Canvas(
        modifier = modifier
            .background(Color.White)
            .onSizeChanged { size ->
                canvasSize = size
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        currentPath = Path().apply {
                            moveTo(offset.x, offset.y)
                        }
                        currentPoints.clear()
                        pointSpeeds.clear()
                        currentPoints.add(Point(offset.x, offset.y))
                        pointSpeeds.add(1f) // Initial point
                        lastPoint = offset
                        lastTime = System.currentTimeMillis()
                    },
                    onDragEnd = {
                        paths.add(currentPath)
                        onDrawingComplete(
                            currentPoints.toList(),
                            canvasSize.width,
                            canvasSize.height
                        )
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        val newPoint = change.position
                        currentPath.lineTo(newPoint.x, newPoint.y)

                        // Calculate drawing speed
                        val currentTime = System.currentTimeMillis()
                        val speed = lastPoint?.let { lastPt ->
                            val distance = sqrt(
                                (newPoint.x - lastPt.x).pow(2) +
                                        (newPoint.y - lastPt.y).pow(2)
                            )
                            val timeDiff = max(currentTime - lastTime, 1L)
                            distance / timeDiff
                        } ?: 0f

                        currentPoints.add(Point(newPoint.x, newPoint.y))
                        pointSpeeds.add(speed)
                        lastPoint = newPoint
                        lastTime = currentTime
                    }
                )
            }
    ) {
        // Draw background
        drawRect(Color.White, Offset.Zero, size)

        // Draw completed paths
        paths.forEach { path ->
            drawPath(
                path = path,
                color = pathColor,
                style = Stroke(width = 10f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }

        // Draw current path with variable width
        if (currentPoints.size > 1) {
            for (i in 1 until currentPoints.size) {
                val p1 = currentPoints[i - 1]
                val p2 = currentPoints[i]
                val speed = pointSpeeds[i]

                // Width varies from 4dp to 12dp based on speed
                val width = 12f - (speed * 30f).coerceIn(0f, 8f)

                drawLine(
                    color = pathColor,
                    start = Offset(p1.x, p1.y),
                    end = Offset(p2.x, p2.y),
                    strokeWidth = width,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}




