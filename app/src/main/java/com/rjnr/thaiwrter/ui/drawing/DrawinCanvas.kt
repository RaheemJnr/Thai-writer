package com.rjnr.thaiwrter.ui.drawing

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import com.rjnr.thaiwrter.data.models.ThaiCharacter
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

//@Composable
//fun DrawingCanvas(
//    modifier: Modifier = Modifier,
//    currentCharacter: ThaiCharacter?,
//    paths: List<PathWithColor>,
//    onStrokeFinished: (List<Point>) -> Unit
//) {
//    var currentPath by remember { mutableStateOf(Path()) }
//    var currentPoints by remember { mutableStateOf(mutableListOf<Point>()) }
//
//    Canvas(
//        modifier = modifier
//            .background(Color.White)
//            .pointerInput(Unit) {
//                detectDragGestures(
//                    onDragStart = { offset ->
//                        currentPath = Path().apply {
//                            moveTo(offset.x, offset.y)
//                        }
//                        currentPoints.clear()
//                        currentPoints.add(Point(offset.x.toInt(), offset.y.toInt()))
//                        Log.d("DrawingCanvas", "Started drawing at: $offset")
//
//                    },
//                    onDragEnd = {
//                        Log.d("DrawingCanvas", "Finished drawing with ${currentPoints.size} points")
//                        onStrokeFinished(currentPoints.toList())
//                    },
//                    onDrag = { change, _ ->
//                        change.consume()
//                        val newPoint = change.position
//                        currentPath.lineTo(newPoint.x, newPoint.y)
//                        currentPoints.add(Point(newPoint.x.toInt(), newPoint.y.toInt()))
//                    }
//                )
//            }
//    ) {
//        Log.d("DrawingCanvas", "Canvas size: ${size.width} x ${size.height}")
//
//        // Draw background
//        drawRect(Color.White, Offset.Zero, size)
//        // Draw guide strokes
////        currentCharacter?.let {
////            val strokeData = it.strokeData
////            strokeData.strokes.forEach { stroke ->
////                drawLine(
////                    color = Color.LightGray,
////                    start = Offset(stroke.x1 * size.width, stroke.y1 * size.height),
////                    end = Offset(stroke.x2 * size.width, stroke.y2 * size.height),
////                    strokeWidth = 5f,
////                    alpha = 0.3f
////                )
////            }
////        }
//        currentCharacter?.let { char ->
//            drawContext.canvas.nativeCanvas.apply {
//                drawText(
//                    char.character,
//                    size.width / 2f,
//                    size.height / 2f,
//                    android.graphics.Paint().apply {
//                        color = android.graphics.Color.LTGRAY
//                        alpha = 128
//                        textSize = 200f
//                        textAlign = android.graphics.Paint.Align.CENTER
//                    }
//                )
//            }
//        }
//
//        // Draw current path
//        drawPath(
//            path = currentPath,
//            color = Color.Black,
//            style = Stroke(
//                width = 8f,
//                cap = StrokeCap.Round,
//                join = StrokeJoin.Round
//            )
//        )
//
//        // Draw completed paths
//        paths.forEach { pathWithColor ->
//            drawPath(
//                path = pathWithColor.path,
//                color = pathWithColor.color,
//                style = Stroke(width = 8f,
//                    cap = StrokeCap.Round,
//                    join = StrokeJoin.Round)
//            )
//        }
//    }
//}
@Composable
fun DrawingCanvas(
    modifier: Modifier = Modifier,
    currentCharacter: ThaiCharacter?,
    currentStrokeIndex: Int,
    paths: List<PathWithColor>,
    onStrokeFinished: (List<Point>, Int, Int) -> Unit
) {
    var currentPath by remember { mutableStateOf(Path()) }
    val currentPoints = remember { mutableStateListOf<Point>() }

    Canvas(
        modifier = modifier
            .background(Color.White)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        currentPath = Path().apply {
                            moveTo(offset.x, offset.y)
                        }
                        currentPoints.clear()
                        currentPoints.add(
                            Point(
                                offset.x,
                                offset.y
                            )
                        )  // Keep as float
                    },
                    onDragEnd = {
                        onStrokeFinished(currentPoints.toList(), size.width, size.height)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        val newPoint = change.position
                        currentPath.lineTo(newPoint.x, newPoint.y)
                        currentPoints.add(Point(newPoint.x, newPoint.y))  // Keep as float
                    }
                )
            }
    ) {
        // Draw background
        drawRect(Color.White, Offset.Zero, size)

        // Draw guide character
        currentCharacter?.let { char ->
            val strokeData = char.strokeData


            // Draw all strokes in light gray
            strokeData.strokes.forEachIndexed { index, stroke ->
                val strokeColor = when {
                    index < currentStrokeIndex -> Color.LightGray.copy(alpha = 0.2f) // Past strokes
                    index == currentStrokeIndex -> Color.Gray // Current stroke - make it darker
                    else -> Color.LightGray.copy(alpha = 0.3f) // Future strokes
                }
                // Draw stroke path
                drawLine(
                    color = strokeColor,
                    start = Offset(stroke.x1 * size.width, stroke.y1 * size.height),
                    end = Offset(stroke.x2 * size.width, stroke.y2 * size.height),
                    strokeWidth = 8f,
                    pathEffect = if (index == currentStrokeIndex) {
                        PathEffect.dashPathEffect(floatArrayOf(20f, 10f))
                    } else {
                        null
                    }
                )

                if (index == currentStrokeIndex) {
                    // Draw start point (green circle)
                    drawCircle(
                        color = Color.Green,
                        radius = 15f,
                        center = Offset(stroke.x1 * size.width, stroke.y1 * size.height)
                    )

                    // Draw end point (red circle)
                    drawCircle(
                        color = Color.Red,
                        radius = 15f,
                        center = Offset(stroke.x2 * size.width, stroke.y2 * size.height)
                    )

                    // Draw direction arrow
                    val arrowPath = Path().apply {
                        val startX = stroke.x1 * size.width
                        val startY = stroke.y1 * size.height
                        val endX = stroke.x2 * size.width
                        val endY = stroke.y2 * size.height

                        // Calculate middle point
                        val midX = (startX + endX) / 2
                        val midY = (startY + endY) / 2

                        // Draw arrow at middle point
                        val angle = atan2(endY - startY, endX - startX)
                        val arrowSize = 30f

                        moveTo(
                            midX - arrowSize * cos(angle - PI / 6).toFloat(),
                            midY - arrowSize * sin(angle - PI / 6).toFloat()
                        )
                        lineTo(midX, midY)
                        lineTo(
                            midX - arrowSize * cos(angle + PI / 6).toFloat(),
                            midY - arrowSize * sin(angle + PI / 6).toFloat()
                        )
                    }

                    drawPath(
                        path = arrowPath,
                        color = Color.Blue.copy(alpha = 0.6f),
                        style = Stroke(width = 5f)
                    )

                    // Draw stroke number
                    drawContext.canvas.nativeCanvas.apply {
                        drawText(
                            (index + 1).toString(),
                            stroke.x1 * size.width - 25f,
                            stroke.y1 * size.height - 25f,
                            android.graphics.Paint().apply {
                                color = android.graphics.Color.BLUE
                                textSize = 40f
                                textAlign = android.graphics.Paint.Align.CENTER
                            }
                        )
                    }
                }
            }
        }

        // Draw current path
        drawPath(
            path = currentPath,
            color = Color.Black,
            style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Draw completed paths
        paths.forEach { pathWithColor ->
            drawPath(
                path = pathWithColor.path,
                color = pathWithColor.color,
                style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
    }
}

data class PathWithColor(
    val path: Path,
    val color: Color
)

data class Point(val x: Float, val y: Float)