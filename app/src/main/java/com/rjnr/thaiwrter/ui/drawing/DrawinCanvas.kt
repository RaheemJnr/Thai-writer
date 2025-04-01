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
    onDrawingComplete: (List<Point>, Int, Int) -> Unit
) {
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var currentPath by remember { mutableStateOf(Path()) }
    val currentPoints = remember { mutableStateListOf<Point>() }
    val paths = remember { mutableStateListOf<Path>() }

    // React to shouldClear changes
    LaunchedEffect(shouldClear) {
        if (shouldClear) {
            currentPath = Path()
            currentPoints.clear()
            paths.clear()
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
                        currentPoints.add(Point(offset.x, offset.y))
                    },
                    onDragEnd = {
                        paths.add(currentPath)
                        onDrawingComplete(currentPoints.toList(), canvasSize.width, canvasSize.height)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        val newPoint = change.position
                        currentPath.lineTo(newPoint.x, newPoint.y)
                        currentPoints.add(Point(newPoint.x, newPoint.y))
                    }
                )
            }
    ) {
        // Draw background
        drawRect(Color.White, Offset.Zero, size)

        // Draw completed paths with MUCH thicker stroke (16-20dp)
        paths.forEach { path ->
            drawPath(
                path = path,
                color = Color.Black,
                style = Stroke(width = 16f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }

        // Draw current path with same thick stroke
        drawPath(
            path = currentPath,
            color = Color.Black,
            style = Stroke(width = 16f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}




