import android.graphics.Typeface
import android.text.TextPaint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

//@Composable
//fun AnimatedGuideOverlay(
//    character: String,
//    isVisible: Boolean,
//    modifier: Modifier = Modifier
//) {
//    val strokePaths = remember(character) {
//        // This would come from your character database
//        // For now, we'll use a placeholder path
//        val paths = mutableListOf<Path>()
//        when (character) {
//            "ก" -> {
//                // Simplified path for 'ก'
//                paths.add(Path().apply {
//                    moveTo(0.3f, 0.2f)
//                    lineTo(0.3f, 0.8f)
//                })
//                paths.add(Path().apply {
//                    moveTo(0.3f, 0.4f)
//                    lineTo(0.7f, 0.4f)
//                    lineTo(0.7f, 0.8f)
//                    lineTo(0.4f, 0.8f)
//                    lineTo(0.4f, 0.6f)
//                    lineTo(0.6f, 0.6f)
//                })
//            }
//            // Add more characters as needed
//            else -> {
//                // Generic placeholder
//                paths.add(Path().apply {
//                    moveTo(0.3f, 0.2f)
//                    lineTo(0.3f, 0.8f)
//                })
//            }
//        }
//        paths
//    }
//
//    val animProgress = remember { Animatable(0f) }
//
//    LaunchedEffect(isVisible, character) {
//        if (isVisible) {
//            animProgress.snapTo(0f)
//            animProgress.animateTo(
//                targetValue = 1f,
//                animationSpec = tween(
//                    durationMillis = 2000,
//                    easing = LinearEasing
//                )
//            )
//        }
//    }
//
//    Canvas(modifier = modifier) {
//        strokePaths.forEach { path ->
//            val scaledPath = Path().apply {
//                addPath(path)
//                transform(Matrix().apply {
//                    scale(size.width,size.height)
//                })
//            }
//
//            // Draw guide path with low opacity
//            drawPath(
//                path = scaledPath,
//                color = Color.LightGray.copy(alpha = 0.3f),
//                style = Stroke(width = 10f, cap = StrokeCap.Round, join = StrokeJoin.Round)
//            )
//
//            // Draw animated progress of the path
//            val pathMeasure = PathMeasure()
//            pathMeasure.setPath(scaledPath, false)
//            val animatedPath = Path()
//            pathMeasure.getSegment(
//                0f,
//                pathMeasure.length * animProgress.value,
//                animatedPath,
//                true
//            )
//
//            drawPath(
//                path = animatedPath,
//                color = Color.Blue,
//                style = Stroke(width = 10f, cap = StrokeCap.Round, join = StrokeJoin.Round)
//            )
//        }
//    }
//}
@Composable
fun CharacterGuideOverlay(
    character: String,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    // Define the stroke paths for Thai characters based on your character map
    val characterStrokes = remember(character) {
        when (character) {
            "ก" -> { // ko kai
                listOf(
                    // Vertical line
                    listOf(Offset(0.3f, 0.2f), Offset(0.3f, 0.8f)),
                    // Right curved part
                    listOf(
                        Offset(0.3f, 0.4f), Offset(0.5f, 0.4f),
                        Offset(0.7f, 0.4f), Offset(0.7f, 0.6f),
                        Offset(0.7f, 0.8f), Offset(0.5f, 0.8f),
                        Offset(0.3f, 0.8f)
                    )
                )
            }
            "อ" -> { // o ang (as shown in your screenshot)
                listOf(
                    // Vertical line (left)
                    listOf(Offset(0.3f, 0.2f), Offset(0.3f, 0.8f)),
                    // Right curved part
                    listOf(
                        Offset(0.45f, 0.3f), Offset(0.6f, 0.3f),
                        Offset(0.75f, 0.4f), Offset(0.75f, 0.6f),
                        Offset(0.7f, 0.75f), Offset(0.5f, 0.8f),
                        Offset(0.35f, 0.7f), Offset(0.35f, 0.6f),
                        Offset(0.45f, 0.5f), Offset(0.55f, 0.5f)
                    )
                )
            }
            // Add other characters from CHARACTER_MAP as needed
            else -> listOf(emptyList())
        }
    }

    // Animation progress
    val animationProgress = animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(2000)
    )

    Canvas(modifier = modifier) {
        if (isVisible) {
            characterStrokes.forEach { stroke ->
                if (stroke.isNotEmpty()) {
                    // Calculate how much of the stroke to show based on animation progress
                    val totalLength = stroke.zipWithNext().sumOf { (a, b) ->
                        distance(a, b, size.width, size.height).toDouble()
                    }.toFloat()
                    var drawnLength = 0f
                    val targetLength = totalLength * animationProgress.value

                    // Draw the strokes as a blue guide
                    stroke.zipWithNext().forEach { (start, end) ->
                        val segmentLength = distance(start, end, size.width, size.height)

                        if (drawnLength < targetLength) {
                            val segmentDrawn = minOf(segmentLength, targetLength - drawnLength)
                            val ratio = if (segmentLength > 0) segmentDrawn / segmentLength else 0f

                            // Scale coordinates to actual canvas size
                            val scaledStart = Offset(start.x * size.width, start.y * size.height)
                            val scaledEnd = Offset(end.x * size.width, end.y * size.height)

                            // Calculate endpoint based on progress
                            val endPoint = if (ratio < 1f) {
                                Offset(
                                    scaledStart.x + (scaledEnd.x - scaledStart.x) * ratio,
                                    scaledStart.y + (scaledEnd.y - scaledStart.y) * ratio
                                )
                            } else {
                                scaledEnd
                            }

                            // Draw the segment with blue color and proper thickness
                            drawLine(
                                color = Color.Blue,
                                start = scaledStart,
                                end = endPoint,
                                strokeWidth = 16f,
                                cap = StrokeCap.Round
                            )
                        }

                        drawnLength += segmentLength
                    }
                }
            }
        }
    }
}

private fun distance(a: Offset, b: Offset, width: Float, height: Float): Float {
    val scaledA = Offset(a.x * width, a.y * height)
    val scaledB = Offset(b.x * width, b.y * height)
    return sqrt((scaledB.x - scaledA.x).pow(2) + (scaledB.y - scaledA.y).pow(2))
}