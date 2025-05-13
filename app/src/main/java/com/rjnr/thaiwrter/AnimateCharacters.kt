import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlinx.coroutines.delay
import kotlin.math.min

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


//@Composable
//fun MorphOverlay(
//    userPath: Path,          // what the user drew (canvas coords)
//    perfectSvg: String,      // original SVG path data
//    durationMs: Int = 600,
//    modifier: Modifier = Modifier,
//    onFinished: () -> Unit
//) {
//
//    // parse & scale the perfect path to the same canvas space
//    val targetPath = remember(perfectSvg) {
//        androidx.compose.ui.graphics.vector.PathParser()
//            .parsePathString(perfectSvg)
//            .toPath()
//            .asAndroidPath()
//    }
//    // progress 0 → 1
//    val progress = remember { Animatable(0f) }
//    LaunchedEffect(Unit) {
//        progress.animateTo(
//            1f,
//            animationSpec = tween(durationMs, easing = FastOutSlowInEasing)
//        )
//        onFinished()
//    }
//
//    /* -- scale targetPath to canvas each draw -- */
//    Canvas(modifier) {
//        val strokePx = 0.06f * min(size.width, size.height)
//
//        // Fit exactly the way StrokeGuide does
//        val r = android.graphics.RectF().also { targetPath.computeBounds(it, true) }
//        val scale = min(size.width / r.width(), size.height / r.height()) * 0.9f
//        val dx = (size.width - r.width() * scale) / 2f - r.left * scale
//        val dy = (size.height - r.height() * scale) / 2f - r.top * scale
//        val m = android.graphics.Matrix().apply {
//            postScale(scale, scale)
//            postTranslate(dx, dy)
//        }
//        val perfectCanvas = android.graphics.Path().apply {
//            set(targetPath); transform(m)
//        }
//        // 0 = user visible, 1 = perfect visible
//        drawPath(
//            path = userPath,
//            color = Color.Black,
//            style = Stroke(width = strokePx, cap = StrokeCap.Round),
//            alpha = 1f - progress.value
//        )
//        drawPath(
//            path = perfectCanvas.asComposePath(), color = Color.Green,
//            style = Stroke(width = strokePx, cap = StrokeCap.Round),
//            alpha = progress.value
//        )
//    }
//}


@Composable
fun MorphOverlay(
    userPath: Path,
    perfectSvgData: String?, // Changed from perfectSvg to perfectSvgData
    durationMs: Int = 800,   // Slightly longer for better visual
    modifier: Modifier = Modifier,
    marginToApply: Float = 0.25f, // Consistent margin
    onFinished: () -> Unit
) {
    if (perfectSvgData.isNullOrEmpty()) {
        LaunchedEffect(Unit) { onFinished() } // Call onFinished immediately if no perfect path
        return
    }

    val targetPath = remember(perfectSvgData) {
        androidx.compose.ui.graphics.vector.PathParser()
            .parsePathString(perfectSvgData)
            .toPath()
            .asAndroidPath()
    }
    // ... rest of your MorphOverlay logic is good ...
    // Make sure the scaling logic (m matrix) inside Canvas is robust

    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(
            1f,
            animationSpec = tween(durationMs, easing = FastOutSlowInEasing)
        )
        delay(200) // Small delay to let user see the green before it disappears or advances
        onFinished()
    }
    val r = remember(targetPath) {
        android.graphics.RectF().also { targetPath.computeBounds(it, true) }
    }


    Canvas(modifier) {
        val strokePx = 0.07f * min(size.width, size.height)


        // Consistent Scaling with StrokeGuide
        val r = android.graphics.RectF().also { targetPath.computeBounds(it, true) }
        val padX = size.width * marginToApply
        val padY = size.height * marginToApply
        val availW = size.width - padX * 2
        val availH = size.height - padY * 2

        if (r.width() <= 0 || r.height() <= 0 || availW <= 0 || availH <= 0) {
            // Handle invalid bounds or available space, perhaps by calling onFinished early
            onFinished()
            return@Canvas
        }


        val scaleFactor = 0.9f // Consistent additional factor
        val finalScale = min(availW / r.width(), availH / r.height()) * scaleFactor


        val scaledWidth = r.width() * finalScale
        val scaledHeight = r.height() * finalScale
        val finalDx = padX + (availW - scaledWidth) / 2f - (r.left * finalScale)
        val finalDy = padY + (availH - scaledHeight) / 2f - (r.top * finalScale)

        val m =
            android.graphics.Matrix().apply {
                postScale(finalScale, finalScale)
                postTranslate(finalDx, finalDy)
            }

        val perfectCanvasPath =
            android.graphics.Path().apply {
                set(targetPath); transform(m)
            }


        // User path (fading out)
        drawPath(
            path = userPath,
            color = Color.Black,
            style = Stroke(width = strokePx, cap = StrokeCap.Round, join = StrokeJoin.Round),
            alpha = 1f - progress.value
        )
        // Perfect path (fading in, green)
        drawPath(
            path = perfectCanvasPath.asComposePath(),
            color = Color.Green, // Or your MaterialTheme.colorScheme.tertiary
            style = Stroke(width = strokePx, cap = StrokeCap.Round, join = StrokeJoin.Round),
            alpha = progress.value
        )
    }
}