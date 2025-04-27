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
object ThaiCharacterStrokePaths {

    /**
     * Get the stroke paths for a specific Thai character
     * @param character The Thai character to get strokes for
     * @return List of strokes, where each stroke is a list of points
     */
    fun getStrokePathsForCharacter(character: String): List<List<Offset>> {
        return when (character) {
            // Consonants - First row
            "ก" -> { // ko kai (0)
                listOf(
                    listOf(Offset(0.3f, 0.2f), Offset(0.3f, 0.8f)),
                    listOf(Offset(0.3f, 0.4f), Offset(0.7f, 0.4f), Offset(0.7f, 0.8f),
                        Offset(0.5f, 0.8f), Offset(0.3f, 0.8f))
                )
            }
            "ข" -> { // kho khai (1)
                listOf(
                    listOf(Offset(0.3f, 0.2f), Offset(0.3f, 0.8f)),
                    listOf(Offset(0.3f, 0.3f), Offset(0.5f, 0.2f), Offset(0.7f, 0.3f),
                        Offset(0.7f, 0.5f), Offset(0.5f, 0.6f), Offset(0.7f, 0.7f), Offset(0.7f, 0.8f))
                )
            }
            "ฃ" -> { // kho khuat (2)
                listOf(
                    listOf(Offset(0.3f, 0.2f), Offset(0.3f, 0.8f)),
                    listOf(Offset(0.3f, 0.3f), Offset(0.5f, 0.2f), Offset(0.7f, 0.3f),
                        Offset(0.7f, 0.5f), Offset(0.5f, 0.6f)),
                    listOf(Offset(0.5f, 0.4f), Offset(0.6f, 0.4f))
                )
            }
            "ค" -> { // kho khwai (3)
                listOf(
                    listOf(Offset(0.3f, 0.2f), Offset(0.3f, 0.8f)),
                    listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.3f), Offset(0.7f, 0.6f),
                        Offset(0.5f, 0.7f), Offset(0.7f, 0.8f))
                )
            }
            "ฅ" -> { // kho khon (4)
                listOf(
                    listOf(Offset(0.3f, 0.2f), Offset(0.3f, 0.8f)),
                    listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.3f), Offset(0.7f, 0.5f),
                        Offset(0.3f, 0.5f)),
                    listOf(Offset(0.5f, 0.5f), Offset(0.5f, 0.7f), Offset(0.7f, 0.8f))
                )
            }
            "ฆ" -> { // kho rakhang (5)
                listOf(
                    listOf(Offset(0.3f, 0.2f), Offset(0.3f, 0.8f)),
                    listOf(Offset(0.3f, 0.25f), Offset(0.7f, 0.25f)),
                    listOf(Offset(0.5f, 0.25f), Offset(0.5f, 0.5f), Offset(0.7f, 0.6f),
                        Offset(0.7f, 0.8f), Offset(0.5f, 0.8f))
                )
            }
            "ง" -> { // ngo ngu (6)
                listOf(
                    listOf(Offset(0.3f, 0.3f), Offset(0.6f, 0.3f), Offset(0.7f, 0.4f),
                        Offset(0.7f, 0.6f), Offset(0.6f, 0.7f), Offset(0.3f, 0.7f)),
                    listOf(Offset(0.3f, 0.7f), Offset(0.3f, 0.8f), Offset(0.5f, 0.9f))
                )
            }

            // Consonants - Second row
            "จ" -> { // cho chan (7)
                listOf(
                    listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.3f)),
                    listOf(Offset(0.5f, 0.3f), Offset(0.5f, 0.6f), Offset(0.3f, 0.7f),
                        Offset(0.5f, 0.8f), Offset(0.7f, 0.7f))
                )
            }
            "ฉ" -> { // cho ching (8)
                listOf(
                    listOf(Offset(0.5f, 0.2f), Offset(0.5f, 0.4f)),
                    listOf(Offset(0.3f, 0.4f), Offset(0.7f, 0.4f)),
                    listOf(Offset(0.5f, 0.4f), Offset(0.5f, 0.7f), Offset(0.3f, 0.8f),
                        Offset(0.7f, 0.9f))
                )
            }
            "ช" -> { // cho chang (9)
                listOf(
                    listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.3f)),
                    listOf(Offset(0.5f, 0.3f), Offset(0.5f, 0.7f)),
                    listOf(Offset(0.3f, 0.7f), Offset(0.7f, 0.7f)),
                    listOf(Offset(0.3f, 0.7f), Offset(0.3f, 0.8f), Offset(0.5f, 0.9f))
                )
            }
            "ซ" -> { // so so (10)
                listOf(
                    listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.3f)),
                    listOf(Offset(0.5f, 0.3f), Offset(0.5f, 0.8f)),
                    listOf(Offset(0.3f, 0.5f), Offset(0.7f, 0.5f)),
                    listOf(Offset(0.3f, 0.8f), Offset(0.7f, 0.8f))
                )
            }
            "ฌ" -> { // cho choe (11)
                listOf(
                    listOf(Offset(0.5f, 0.2f), Offset(0.5f, 0.3f)),
                    listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.3f)),
                    listOf(Offset(0.3f, 0.3f), Offset(0.3f, 0.6f), Offset(0.7f, 0.6f),
                        Offset(0.7f, 0.3f)),
                    listOf(Offset(0.5f, 0.6f), Offset(0.5f, 0.8f), Offset(0.3f, 0.9f))
                )
            }
            "ญ" -> { // yo ying (12)
                listOf(
                    listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.3f)),
                    listOf(Offset(0.5f, 0.3f), Offset(0.5f, 0.7f)),
                    listOf(Offset(0.3f, 0.7f), Offset(0.7f, 0.7f)),
                    listOf(Offset(0.5f, 0.7f), Offset(0.5f, 0.8f), Offset(0.3f, 0.9f))
                )
            }
            "ฎ" -> { // do chada (13)
                listOf(
                    listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.3f)),
                    listOf(Offset(0.5f, 0.3f), Offset(0.5f, 0.7f)),
                    listOf(Offset(0.3f, 0.7f), Offset(0.7f, 0.7f)),
                    listOf(Offset(0.7f, 0.5f), Offset(0.9f, 0.6f))
                )
            }

            // Consonants - Third row
            "ฏ" -> { // to patak (14)
                listOf(
                    listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.3f)),
                    listOf(Offset(0.5f, 0.3f), Offset(0.5f, 0.7f), Offset(0.3f, 0.8f),
                        Offset(0.3f, 0.7f), Offset(0.7f, 0.7f), Offset(0.7f, 0.8f), Offset(0.5f, 0.9f))
                )
            }
            "ฐ" -> { // tho than (15)
                listOf(
                    listOf(Offset(0.5f, 0.2f), Offset(0.5f, 0.9f)),
                    listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.3f)),
                    listOf(Offset(0.3f, 0.7f), Offset(0.7f, 0.7f))
                )
            }
            "ฑ" -> { // tho montho (16)
                listOf(
                    listOf(Offset(0.5f, 0.2f), Offset(0.5f, 0.8f)),
                    listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.3f)),
                    listOf(Offset(0.3f, 0.6f), Offset(0.7f, 0.6f)),
                    listOf(Offset(0.3f, 0.8f), Offset(0.7f, 0.8f))
                )
            }
            "ฒ" -> { // tho phuthao (17)
                listOf(
                    listOf(Offset(0.3f, 0.2f), Offset(0.7f, 0.2f)),
                    listOf(Offset(0.5f, 0.2f), Offset(0.5f, 0.7f)),
                    listOf(Offset(0.3f, 0.5f), Offset(0.7f, 0.5f)),
                    listOf(Offset(0.3f, 0.7f), Offset(0.7f, 0.7f)),
                    listOf(Offset(0.3f, 0.7f), Offset(0.3f, 0.8f), Offset(0.5f, 0.9f))
                )
            }
            "ณ" -> { // no nen (18)
                listOf(
                    listOf(Offset(0.3f, 0.2f), Offset(0.7f, 0.2f)),
                    listOf(Offset(0.5f, 0.2f), Offset(0.5f, 0.7f)),
                    listOf(Offset(0.3f, 0.7f), Offset(0.7f, 0.7f)),
                    listOf(Offset(0.7f, 0.4f), Offset(0.9f, 0.5f))
                )
            }
            "ด" -> { // do dek (19)
                listOf(
                    listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.3f)),
                    listOf(Offset(0.7f, 0.3f), Offset(0.7f, 0.7f), Offset(0.3f, 0.7f),
                        Offset(0.3f, 0.3f))
                )
            }
            "ต" -> { // to tao (20)
                listOf(
                    listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.3f)),
                    listOf(Offset(0.5f, 0.3f), Offset(0.5f, 0.7f)),
                    listOf(Offset(0.3f, 0.7f), Offset(0.7f, 0.7f))
                )
            }

            // Continuing with more consonants
            "ถ" -> { // tho thung (21)
                listOf(
                    listOf(Offset(0.5f, 0.2f), Offset(0.5f, 0.8f)),
                    listOf(Offset(0.3f, 0.4f), Offset(0.7f, 0.4f)),
                    listOf(Offset(0.3f, 0.6f), Offset(0.7f, 0.6f))
                )
            }
            "ท" -> { // tho thahan (22)
                listOf(
                    listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.3f)),
                    listOf(Offset(0.5f, 0.3f), Offset(0.5f, 0.7f)),
                    listOf(Offset(0.3f, 0.5f), Offset(0.7f, 0.5f)),
                    listOf(Offset(0.3f, 0.7f), Offset(0.7f, 0.7f))
                )
            }
            "ธ" -> { // tho thong (23)
                listOf(
                    listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.3f)),
                    listOf(Offset(0.5f, 0.3f), Offset(0.5f, 0.8f)),
                    listOf(Offset(0.3f, 0.5f), Offset(0.7f, 0.5f))
                )
            }
            "น" -> { // no nu (24)
                listOf(
                    listOf(Offset(0.3f, 0.3f), Offset(0.3f, 0.7f)),
                    listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.3f)),
                    listOf(Offset(0.7f, 0.3f), Offset(0.7f, 0.7f)),
                    listOf(Offset(0.3f, 0.7f), Offset(0.7f, 0.7f))
                )
            }
            "บ" -> { // bo baimai (25)
                listOf(
                    listOf(Offset(0.3f, 0.3f), Offset(0.3f, 0.7f)),
                    listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.3f), Offset(0.7f, 0.7f),
                        Offset(0.3f, 0.7f))
                )
            }
            "ป" -> { // po pla (26)
                listOf(
                    listOf(Offset(0.5f, 0.2f), Offset(0.5f, 0.8f)),
                    listOf(Offset(0.3f, 0.4f), Offset(0.7f, 0.4f)),
                    listOf(Offset(0.3f, 0.7f), Offset(0.7f, 0.7f))
                )
            }
            "ผ" -> { // pho phueng (27)
                listOf(
                    listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.3f)),
                    listOf(Offset(0.3f, 0.3f), Offset(0.3f, 0.7f), Offset(0.7f, 0.7f),
                        Offset(0.7f, 0.3f))
                )
            }

            // More consonants
            "ฝ" -> { // fo fa (28)
                listOf(
                    listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.3f)),
                    listOf(Offset(0.5f, 0.3f), Offset(0.5f, 0.8f)),
                    listOf(Offset(0.3f, 0.5f), Offset(0.7f, 0.5f))
                )
            }
            "พ" -> { // pho phan (29)
                listOf(
                    listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.3f)),
                    listOf(Offset(0.3f, 0.3f), Offset(0.3f, 0.7f)),
                    listOf(Offset(0.5f, 0.3f), Offset(0.5f, 0.7f)),
                    listOf(Offset(0.7f, 0.3f), Offset(0.7f, 0.7f)),
                    listOf(Offset(0.3f, 0.7f), Offset(0.7f, 0.7f))
                )
            }
            "ฟ" -> { // fo fan (30)
                listOf(
                    listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.3f)),
                    listOf(Offset(0.5f, 0.2f), Offset(0.5f, 0.8f)),
                    listOf(Offset(0.3f, 0.5f), Offset(0.7f, 0.5f)),
                    listOf(Offset(0.3f, 0.8f), Offset(0.7f, 0.8f))
                )
            }
            "ภ" -> { // pho samphao (31)
                listOf(
                    listOf(Offset(0.3f, 0.2f), Offset(0.7f, 0.2f)),
                    listOf(Offset(0.3f, 0.2f), Offset(0.3f, 0.7f)),
                    listOf(Offset(0.5f, 0.2f), Offset(0.5f, 0.7f)),
                    listOf(Offset(0.7f, 0.2f), Offset(0.7f, 0.7f)),
                    listOf(Offset(0.3f, 0.7f), Offset(0.7f, 0.7f))
                )
            }
            "ม" -> { // mo ma (32)
                listOf(
                    listOf(Offset(0.2f, 0.4f), Offset(0.4f, 0.3f), Offset(0.6f, 0.3f),
                        Offset(0.8f, 0.4f)),
                    listOf(Offset(0.2f, 0.4f), Offset(0.2f, 0.7f), Offset(0.8f, 0.7f),
                        Offset(0.8f, 0.4f))
                )
            }
            "ย" -> { // yo yak (33)
                listOf(
                    listOf(Offset(0.3f, 0.3f), Offset(0.5f, 0.3f), Offset(0.7f, 0.4f),
                        Offset(0.7f, 0.7f), Offset(0.5f, 0.8f), Offset(0.3f, 0.7f))
                )
            }
            "ร" -> { // ro ruea (34)
                listOf(
                    listOf(Offset(0.3f, 0.3f), Offset(0.6f, 0.3f), Offset(0.7f, 0.4f),
                        Offset(0.7f, 0.5f), Offset(0.6f, 0.6f), Offset(0.3f, 0.6f)),
                    listOf(Offset(0.5f, 0.6f), Offset(0.5f, 0.8f))
                )
            }

            // More consonants
            "ฤ" -> { // ru (35)
                listOf(
                    listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.3f)),
                    listOf(Offset(0.5f, 0.3f), Offset(0.5f, 0.5f)),
                    listOf(Offset(0.3f, 0.5f), Offset(0.7f, 0.5f)),
                    listOf(Offset(0.3f, 0.5f), Offset(0.3f, 0.7f), Offset(0.5f, 0.8f),
                        Offset(0.7f, 0.7f), Offset(0.7f, 0.5f))
                )
            }
            "ล" -> { // lo ling (36)
                listOf(
                    listOf(Offset(0.3f, 0.3f), Offset(0.6f, 0.3f), Offset(0.7f, 0.4f),
                        Offset(0.7f, 0.7f), Offset(0.3f, 0.7f), Offset(0.3f, 0.3f))
                )
            }
            "ว" -> { // wo waen (37)
                listOf(
                    listOf(Offset(0.3f, 0.3f), Offset(0.3f, 0.6f), Offset(0.5f, 0.7f),
                        Offset(0.7f, 0.6f), Offset(0.7f, 0.3f))
                )
            }
            "ศ" -> { // so sala (38)
                listOf(
                    listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.3f)),
                    listOf(Offset(0.5f, 0.2f), Offset(0.5f, 0.7f)),
                    listOf(Offset(0.3f, 0.7f), Offset(0.7f, 0.7f)),
                    listOf(Offset(0.7f, 0.5f), Offset(0.8f, 0.4f))
                )
            }
            "ษ" -> { // so ruesi (39)
                listOf(
                    listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.3f)),
                    listOf(Offset(0.5f, 0.2f), Offset(0.5f, 0.7f)),
                    listOf(Offset(0.3f, 0.7f), Offset(0.7f, 0.7f)),
                    listOf(Offset(0.3f, 0.5f), Offset(0.2f, 0.4f))
                )
            }
            "ส" -> { // so suea (40)
                listOf(
                    listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.3f)),
                    listOf(Offset(0.5f, 0.3f), Offset(0.5f, 0.7f)),
                    listOf(Offset(0.3f, 0.5f), Offset(0.7f, 0.5f)),
                    listOf(Offset(0.3f, 0.7f), Offset(0.7f, 0.7f))
                )
            }

            // Final consonants
            "ห" -> { // ho hip (41)
                listOf(
                    listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.3f)),
                    listOf(Offset(0.3f, 0.3f), Offset(0.3f, 0.7f), Offset(0.7f, 0.7f),
                        Offset(0.7f, 0.3f))
                )
            }
            "ฬ" -> { // lo chula (42)
                listOf(
                    listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.3f)),
                    listOf(Offset(0.5f, 0.3f), Offset(0.5f, 0.6f)),
                    listOf(Offset(0.3f, 0.6f), Offset(0.7f, 0.6f)),
                    listOf(Offset(0.3f, 0.6f), Offset(0.3f, 0.8f)),
                    listOf(Offset(0.7f, 0.6f), Offset(0.7f, 0.8f))
                )
            }
            "อ" -> { // o ang (43)
                listOf(
                    listOf(Offset(0.3f, 0.2f), Offset(0.3f, 0.8f)),
                    listOf(Offset(0.45f, 0.3f), Offset(0.6f, 0.3f), Offset(0.75f, 0.4f),
                        Offset(0.75f, 0.6f), Offset(0.7f, 0.75f), Offset(0.5f, 0.8f),
                        Offset(0.35f, 0.7f), Offset(0.35f, 0.6f), Offset(0.45f, 0.5f),
                        Offset(0.55f, 0.5f))
                )
            }

            // Vowels and special characters
            "อะ" -> { // sara a (44)
                listOf(
                    listOf(Offset(0.3f, 0.2f), Offset(0.3f, 0.8f)),
                    listOf(Offset(0.45f, 0.3f), Offset(0.6f, 0.3f), Offset(0.75f, 0.4f),
                        Offset(0.75f, 0.6f), Offset(0.7f, 0.75f), Offset(0.5f, 0.8f),
                        Offset(0.35f, 0.7f), Offset(0.35f, 0.6f), Offset(0.45f, 0.5f),
                        Offset(0.55f, 0.5f)),
                    listOf(Offset(0.8f, 0.3f), Offset(0.9f, 0.4f))
                )
            }
            "อา" -> { // sara aa (45)
                listOf(
                    listOf(Offset(0.3f, 0.2f), Offset(0.3f, 0.8f)),
                    listOf(Offset(0.45f, 0.3f), Offset(0.6f, 0.3f), Offset(0.75f, 0.4f),
                        Offset(0.75f, 0.6f), Offset(0.7f, 0.75f), Offset(0.5f, 0.8f),
                        Offset(0.35f, 0.7f), Offset(0.35f, 0.6f), Offset(0.45f, 0.5f),
                        Offset(0.55f, 0.5f)),
                    listOf(Offset(0.8f, 0.3f), Offset(0.8f, 0.8f))
                )
            }
            "อำ" -> { // sara am (46)
                listOf(
                    listOf(Offset(0.3f, 0.2f), Offset(0.3f, 0.8f)),
                    listOf(Offset(0.45f, 0.3f), Offset(0.6f, 0.3f), Offset(0.75f, 0.4f),
                        Offset(0.75f, 0.6f), Offset(0.7f, 0.75f), Offset(0.5f, 0.8f),
                        Offset(0.35f, 0.7f), Offset(0.35f, 0.6f), Offset(0.45f, 0.5f),
                        Offset(0.55f, 0.5f)),
                    listOf(Offset(0.8f, 0.3f), Offset(0.85f, 0.25f), Offset(0.9f, 0.3f),
                        Offset(0.9f, 0.4f), Offset(0.85f, 0.45f), Offset(0.8f, 0.4f),
                        Offset(0.8f, 0.3f))
                )
            }
            "ฮ" -> { // ho nokhuk (47)
                listOf(
                    listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.3f)),
                    listOf(Offset(0.3f, 0.3f), Offset(0.3f, 0.7f)),
                    listOf(Offset(0.7f, 0.3f), Offset(0.7f, 0.7f)),
                    listOf(Offset(0.3f, 0.5f), Offset(0.7f, 0.5f)),
                    listOf(Offset(0.3f, 0.7f), Offset(0.7f, 0.7f))
                )
            }
            "ฯ" -> { // paiyannoi (48)
                listOf(
                    listOf(Offset(0.3f, 0.5f), Offset(0.4f, 0.6f), Offset(0.5f, 0.5f),
                        Offset(0.6f, 0.4f), Offset(0.7f, 0.5f))
                )
            }

            // Vowel markers
            "เ" -> { // sara e (49)
                listOf(
                    listOf(Offset(0.4f, 0.3f), Offset(0.4f, 0.8f)),
                    listOf(Offset(0.6f, 0.3f), Offset(0.6f, 0.8f))
                )
            }
            "แ" -> { // sara ae (50)
                listOf(
                    listOf(Offset(0.3f, 0.3f), Offset(0.3f, 0.8f)),
                    listOf(Offset(0.5f, 0.3f), Offset(0.5f, 0.8f)),
                    listOf(Offset(0.7f, 0.3f), Offset(0.7f, 0.8f))
                )
            }
            "โ" -> { // sara o (51)
                listOf(
                    listOf(Offset(0.4f, 0.3f), Offset(0.4f, 0.8f)),
                    listOf(Offset(0.3f, 0.3f), Offset(0.5f, 0.3f))
                )
            }
            "ใ" -> { // sara ai mai muan (52)
                listOf(
                    listOf(Offset(0.4f, 0.3f), Offset(0.4f, 0.8f)),
                    listOf(Offset(0.3f, 0.5f), Offset(0.5f, 0.5f))
                )
            }
            "ไ" -> { // sara ai mai malai (53)
                listOf(
                    listOf(Offset(0.4f, 0.3f), Offset(0.4f, 0.8f)),
                    listOf(Offset(0.4f, 0.3f), Offset(0.5f, 0.4f))
                )
            }
            "ๆ" -> { // mai yamok (54)
                listOf(
                    listOf(Offset(0.4f, 0.4f), Offset(0.4f, 0.5f), Offset(0.3f, 0.6f)),
                    listOf(Offset(0.6f, 0.4f), Offset(0.6f, 0.5f), Offset(0.5f, 0.6f))
                )
            }

            // Default empty path for unknown characters
            else -> listOf(emptyList())
        }
    }
}

// does not work at all, infact not what i want
@Composable
fun CharacterGuideOverlay(
    character: String,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    // Get stroke paths for the character
    val characterStrokes = remember(character) {
        ThaiCharacterStrokePaths.getStrokePathsForCharacter(character)
    }

    // Animation progress
    val animationProgress = animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(2000)
    )

    Canvas(modifier = modifier) {
        if (isVisible) {
            // Draw each stroke
            characterStrokes.forEach { strokePoints ->
                if (strokePoints.size > 1) {
                    // Calculate total length of this stroke
                    val totalLength = strokePoints.zipWithNext().sumOf { (a, b) ->
                        distance(a, b, size.width, size.height).toDouble()
                    }.toFloat()

                    var drawnLength = 0f
                    val targetLength = totalLength * animationProgress.value

                    // Draw the stroke segments
                    strokePoints.zipWithNext().forEach { (start, end) ->
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

                            // Draw the segment
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

            // Draw full strokes with low opacity as guides
            characterStrokes.forEach { strokePoints ->
                if (strokePoints.size > 1) {
                    strokePoints.zipWithNext().forEach { (start, end) ->
                        // Draw guide line with low opacity
                        drawLine(
                            color = Color.Gray.copy(alpha = 0.2f),
                            start = Offset(start.x * size.width, start.y * size.height),
                            end = Offset(end.x * size.width, end.y * size.height),
                            strokeWidth = 16f,
                            cap = StrokeCap.Round
                        )
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