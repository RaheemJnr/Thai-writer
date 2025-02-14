package com.rjnr.thaiwrter.ui.drawing

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import kotlin.math.pow
import kotlin.math.sqrt

//@Composable
//fun DrawingCanvas(
//    modifier: Modifier = Modifier,
//    currentCharacter: ThaiCharacter?,
//    currentStrokeIndex: Int,
//    paths: List<PathWithColor>,
//    onStrokeFinished: (List<Point>, Int, Int) -> Unit
//) {
//    var currentPath by remember { mutableStateOf(Path()) }
//    val currentPoints = remember { mutableStateListOf<Point>() }
//
//    //
//    LaunchedEffect(paths) {
//        if (paths.isEmpty()) {
//            currentPath = Path()
//            currentPoints.clear()
//        }
//    }
//    Canvas(
//        modifier = modifier
//            .background(Color.White)
//            .pointerInput(Unit) {
//                detectDragGestures(
//                    onDragStart = { offset ->
//                        if (currentCharacter == null) return@detectDragGestures
//
//                        // Check if starting near the correct point
//                        val targetStart = currentCharacter.strokeData.strokes[currentStrokeIndex]
//                            .points.first()
//                            .let { Point(it.x * size.width, it.y * size.height) }
//
//                        if (distanceBetween(offset, targetStart) > 50f) {
//                            // Invalid start position
//                            return@detectDragGestures
//                        }
//                        currentPath = Path().apply {
//                            moveTo(offset.x, offset.y)
//                        }
//                        currentPoints.clear()
//                        currentPoints.add(
//                            Point(
//                                offset.x,
//                                offset.y
//                            )
//                        )  // Keep as float
//                    },
//                    onDragEnd = {
//                        onStrokeFinished(currentPoints.toList(), size.width, size.height)
//                    },
//                    onDrag = { change, _ ->
//                        change.consume()
//                        val newPoint = change.position
//                        currentPath.lineTo(newPoint.x, newPoint.y)
//                        currentPoints.add(Point(newPoint.x, newPoint.y))  // Keep as float
//                    }
//                )
//            }
//    ) {
//        // Draw background
//        drawRect(Color.White, Offset.Zero, size)
//
//        // 2. Draw grid lines
//        val center = Offset(size.width / 2, size.height / 2)
//        drawLine(
//            color = Color.LightGray,
//            start = Offset(0f, center.y),
//            end = Offset(size.width, center.y),
//            strokeWidth = 2f
//        )
//        drawLine(
//            color = Color.LightGray,
//            start = Offset(center.x, 0f),
//            end = Offset(center.x, size.height),
//            strokeWidth = 2f
//        )
//        drawLine(
//            color = Color.LightGray,
//            start = Offset(0f, 0f),
//            end = Offset(size.width, size.height),
//            strokeWidth = 2f
//        )
//        drawLine(
//            color = Color.LightGray,
//            start = Offset(size.width, 0f),
//            end = Offset(0f, size.height),
//            strokeWidth = 2f
//        )
//
//        // Draw guide character
//        currentCharacter?.let { char ->
//            val strokeData = char.strokeData
//
//
//            // Draw all strokes in light gray
////            strokeData.strokes.forEachIndexed { index, stroke ->
////                val strokeColor = when {
////                    index < currentStrokeIndex -> Color.LightGray.copy(alpha = 0.2f) // Past strokes
////                    index == currentStrokeIndex -> Color.Gray // Current stroke - make it darker
////                    else -> Color.LightGray.copy(alpha = 0.3f) // Future strokes
////                }
////                // Draw stroke path
////                drawLine(
////                    color = strokeColor,
////                    start = Offset(stroke.x1 * size.width, stroke.y1 * size.height),
////                    end = Offset(stroke.x2 * size.width, stroke.y2 * size.height),
////                    strokeWidth = 8f,
////                    pathEffect = if (index == currentStrokeIndex) {
////                        PathEffect.dashPathEffect(floatArrayOf(20f, 10f))
////                    } else {
////                        null
////                    }
////                )
////
////                if (index == currentStrokeIndex) {
////                    // Draw start point (green circle)
////                    drawCircle(
////                        color = Color.Green,
////                        radius = 15f,
////                        center = Offset(stroke.x1 * size.width, stroke.y1 * size.height)
////                    )
////
////                    // Draw end point (red circle)
////                    drawCircle(
////                        color = Color.Red,
////                        radius = 15f,
////                        center = Offset(stroke.x2 * size.width, stroke.y2 * size.height)
////                    )
////
////                    // Draw direction arrow
////                    val arrowPath = Path().apply {
////                        val startX = stroke.x1 * size.width
////                        val startY = stroke.y1 * size.height
////                        val endX = stroke.x2 * size.width
////                        val endY = stroke.y2 * size.height
////
////                        // Calculate middle point
////                        val midX = (startX + endX) / 2
////                        val midY = (startY + endY) / 2
////
////                        // Draw arrow at middle point
////                        val angle = atan2(endY - startY, endX - startX)
////                        val arrowSize = 30f
////
////                        moveTo(
////                            midX - arrowSize * cos(angle - PI / 6).toFloat(),
////                            midY - arrowSize * sin(angle - PI / 6).toFloat()
////                        )
////                        lineTo(midX, midY)
////                        lineTo(
////                            midX - arrowSize * cos(angle + PI / 6).toFloat(),
////                            midY - arrowSize * sin(angle + PI / 6).toFloat()
////                        )
////                    }
////
////                    drawPath(
////                        path = arrowPath,
////                        color = Color.Blue.copy(alpha = 0.6f),
////                        style = Stroke(width = 5f)
////                    )
////
////                    // Draw stroke number
////                    drawContext.canvas.nativeCanvas.apply {
////                        drawText(
////                            (index + 1).toString(),
////                            stroke.x1 * size.width - 25f,
////                            stroke.y1 * size.height - 25f,
////                            android.graphics.Paint().apply {
////                                color = android.graphics.Color.BLUE
////                                textSize = 40f
////                                textAlign = android.graphics.Paint.Align.CENTER
////                            }
////                        )
////                    }
////                }
////            }
//            // Inside Canvas block where you draw the guide character
//            strokeData.strokes.forEachIndexed { index, stroke ->
//                val strokeColor = when {
//                    index < currentStrokeIndex -> Color.LightGray.copy(alpha = 0.2f)
//                    index == currentStrokeIndex -> Color.Gray
//                    else -> Color.LightGray.copy(alpha = 0.3f)
//                }
//
//                // Draw entire stroke path
//                val guidePath = Path().apply {
//                    stroke.points.forEachIndexed { i, point ->
//                        val x = point.x * size.width
//                        val y = point.y * size.height
//                        if (i == 0) moveTo(x, y) else lineTo(x, y)
//                    }
//                }
//
//                drawPath(
//                    path = guidePath,
//                    color = strokeColor,
//                    style = Stroke(width = 8f)
//                )
//
//                if (index == currentStrokeIndex) {
//                    // Draw start/end markers
//                    val start = stroke.points.first()
//                    val end = stroke.points.last()
//
//                    drawCircle(
//                        color = Color.Green,
//                        center = Offset(start.x * size.width, start.y * size.height),
//                        radius = 15f
//                    )
//
//                    drawCircle(
//                        color = Color.Red,
//                        center = Offset(end.x * size.width, end.y * size.height),
//                        radius = 15f
//                    )
//                }
//            }
//        }
//
//        // Draw current path
//        drawPath(
//            path = currentPath,
//            color = Color.Black,
//            style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
//        )
//
//        // Draw completed paths
//        paths.forEach { pathWithColor ->
//            drawPath(
//                path = pathWithColor.path,
//                color = pathWithColor.color,
//                style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
//            )
//        }
//    }
//}

@Composable
fun DrawingCanvas(
    modifier: Modifier = Modifier,
    onDrawingComplete: (List<Point>, Int, Int) -> Unit
) {
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var currentPath by remember { mutableStateOf(Path()) }
    val currentPoints = remember { mutableStateListOf<Point>() }
    val paths = remember { mutableStateListOf<Path>() }

    LaunchedEffect(Unit) {
        currentPath = Path()
        currentPoints.clear()
        paths.clear()
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

        // Draw completed paths
        paths.forEach { path ->
            drawPath(
                path = path,
                color = Color.Black,
                style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }

        // Draw current path
        drawPath(
            path = currentPath,
            color = Color.Black,
            style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}
fun distanceBetween(offset: Offset, point: Point): Float {
    return sqrt((offset.x - point.x).pow(2) + (offset.y - point.y).pow(2))
}

data class PathWithColor(
    val path: Path,
    val color: Color
)

