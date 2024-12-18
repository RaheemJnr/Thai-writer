import android.graphics.Typeface
import android.text.TextPaint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.min

@Composable
fun AnimatedLetters() {
    val letters = listOf('A', 'B', 'C', 'D', 'E')
    var currentLetterIndex by remember { mutableStateOf(0) }

    // Declare 'letter' before using it
    val letter = letters[currentLetterIndex]

    // Initialize animation progress
    val animationProgress = remember { Animatable(0f) }

    // Start the animation when 'letter' changes
    LaunchedEffect(letter) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
        )
        // Update 'currentLetterIndex' after animation completes
        if (currentLetterIndex < letters.size - 1) {
            currentLetterIndex++
        } else {
            currentLetterIndex = 0
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val minDimension = min(canvasWidth, canvasHeight)
        val letterSize = minDimension * 0.8f

        // Get the path for the current letter
        val androidPath = getLetterPath(letter, letterSize)

        // Compute bounds of the path
        val bounds = android.graphics.RectF()
        androidPath.computeBounds(bounds, true)

        // Center the path within the canvas
        val offsetX = (canvasWidth - bounds.width()) / 2 - bounds.left
        val offsetY = (canvasHeight - bounds.height()) / 2 - bounds.top
        val matrix = android.graphics.Matrix()
        matrix.setTranslate(offsetX, offsetY)
        androidPath.transform(matrix)

        // Convert to Compose Path
        val letterPath = android.graphics.Path(androidPath)

        // Define dashed stroke for tracing style
        val dashedStroke = Stroke(
            width = 5f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), phase = 0f)
        )

        // Draw the letter in tracing style (dashed lines)
        drawPath(
            path = letterPath.asComposePath(),
            color = Color.Gray,
            style = dashedStroke
        )

        // Animate the circle along the path
        val pathMeasure = android.graphics.PathMeasure(androidPath, false)
        val totalLength = pathMeasure.length
        val position = floatArrayOf(0f, 0f)

        pathMeasure.getPosTan(totalLength * animationProgress.value, position, null)
        if (animationProgress.value < 1f) {
            // Draw the moving circle
            drawCircle(
                color = Color.Red,
                radius = 10f,
                center = Offset(position[0], position[1])
            )
        } else {
            // Optionally, redraw the letter in solid black when animation is complete
            drawPath(
                path = letterPath.asComposePath(),
                color = Color.Black,
                style = Stroke(width = 5f)
            )
        }
    }
}

fun getLetterPath(letter: Char, size: Float): android.graphics.Path {
    val textPaint = TextPaint().apply {
        textSize = size
        typeface = Typeface.DEFAULT_BOLD
    }
    val path = android.graphics.Path()
    textPaint.getTextPath(letter.toString(), 0, 1, 0f, 0f, path)
    return path
}