package com.rjnr.thaiwrter.ui.drawing

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.flow.SharedFlow
import kotlin.math.min


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
//                        onDrawingComplete(
//                            currentPoints.toList(),
//                            canvasSize.width,
//                            canvasSize.height
//                        )
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
//        // Draw completed paths with MUCH thicker stroke (16-20dp)
//        paths.forEach { path ->
//            drawPath(
//                path = path,
//                color = Color.Black,
//                style = Stroke(width = 16f, cap = StrokeCap.Round, join = StrokeJoin.Round)
//            )
//        }
//
//        // Draw current path with same thick stroke
//        drawPath(
//            path = currentPath,
//            color = Color.Black,
//            style = Stroke(width = 16f, cap = StrokeCap.Round, join = StrokeJoin.Round)
//        )
//    }
//}

/* ---------- DrawingCanvas: minimal edits ---------- */
//@Composable
//fun DrawingCanvas(
//    modifier: Modifier = Modifier,
//    shouldClear: Boolean = false,
//    onStrokeFinished: (Path) -> Unit
//) {
//    var currentPath by remember { mutableStateOf(Path()) }
//    val finishedPaths = remember { mutableStateListOf<Path>() }
//
//
//    LaunchedEffect(shouldClear) {
//        if (shouldClear) {
//            currentPath = Path()
//            finishedPaths.clear()
//        }
//    }
//
//    Canvas(
//        modifier = modifier
//            .pointerInput(Unit) {
//                detectDragGestures(
//                    onDragStart = { off ->
//                        currentPath = Path().apply { moveTo(off.x, off.y) }
//                    },
//                    onDragEnd = {
//                        finishedPaths += currentPath
//                        onStrokeFinished(currentPath)
//                    },
//                    onDrag = { change, _ ->
//                        change.consume()
//                        currentPath.lineTo(change.position.x, change.position.y)
//                    }
//                )
//            }
//    ) {
//        val strokePx = 0.06f * min(size.width, size.height)  // ≈6 % of box
//        val style = Stroke(width = strokePx, cap = StrokeCap.Round, join = StrokeJoin.Round)
//        finishedPaths.forEach { drawPath(path = it, color = Color.Black, style = style) }
//        drawPath(path = currentPath, color = Color.Black, style = style)
//    }
//}


@Composable
fun DrawingCanvas(
    modifier: Modifier = Modifier,
    clearSignal: SharedFlow<Unit>,
    onStrokeFinished: (Path) -> Unit,
    onDragStartAction: () -> Unit, // New callback for when dragging starts
    enabled: Boolean = true // To disable drawing during morphing etc.
) {
    var currentPath by remember { mutableStateOf(Path()) }
    // For single stroke characters, we only care about the last completed stroke for morphing.
    // If you need multi-stroke, this logic would need to change.
    // var finishedPaths = remember { mutableStateListOf<Path>() } // Not needed if we only handle one stroke at a time for morphing

    LaunchedEffect(Unit) { // Changed key to Unit, listen for signal
        clearSignal.collect {
            currentPath = Path()
            // finishedPaths.clear() // If you were using it
        }
    }

    Canvas(
        modifier = modifier
            .pointerInput(enabled) { // Re-key pointerInput if 'enabled' changes
                if (enabled) {
                    detectDragGestures(
                        onDragStart = { off ->
                            onDragStartAction() // New callback
                            currentPath = Path().apply { moveTo(off.x, off.y) }
                        },
                        onDragEnd = {
                            // finishedPaths += currentPath // If collecting all strokes
                            onStrokeFinished(currentPath)
                            // currentPath = Path() // Clear current path for next potential stroke if needed
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            val newPoint = change.position
                            // Make sure currentPath is not reset mid-drag if onDragEnd clears it
                           // currentPath.lineTo(change.position.x, change.position.y)
                            currentPath = currentPath.apply { lineTo(newPoint.x, newPoint.y) }
                        }
                    )
                }
            }
    ) {
        val strokePx = 0.07f * min(size.width, size.height)
        val style = Stroke(width = strokePx, cap = StrokeCap.Round, join = StrokeJoin.Round)
        // finishedPaths.forEach { drawPath(path = it, color = Color.Black, style = style) }
        drawPath(path = currentPath, color = Color.Black, style = style)
    }
}


