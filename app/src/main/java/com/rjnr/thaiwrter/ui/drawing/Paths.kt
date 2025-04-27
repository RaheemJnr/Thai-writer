package com.rjnr.thaiwrter.ui.drawing

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.RectF
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.graphics.createBitmap
import androidx.core.graphics.get
import kotlin.math.max
import kotlin.math.min
import android.graphics.Canvas as AndroidCanvas
import androidx.compose.ui.graphics.Path as ComposePath

data class StrokeSpec(
    val pathData: String,           // raw SVG “d” string
    val strokeWidth: Float = 14f
)

val testStroke = StrokeSpec("M 30 20 L 30 180")

@Composable
fun FirstPath(modifier: Modifier = Modifier) {
//    val strokePath = remember(testStroke.pathData) {
//        androidx.compose.ui.graphics.vector.PathParser()    // UI-graphics 1.7+
//            .parsePathString(testStroke.pathData)
//            .toPath()
//    }
}

/** Compose → Android path helpers */
fun ComposePath.toAndroid() = this.asAndroidPath()
fun android.graphics.Path.toCompose() = this.asComposePath()

/* ---------- Stroke-guide composable ---------- */
//@Composable
//fun StrokeGuide(
//    svgPathData: String,
//    marginRatio: Float = 0.05f,          // 5 % padding around glyph
//    durationMs: Int = 2500,
//    color: Color = Color(0xFF5B4CE0),
//    modifier: Modifier = Modifier
//) {
//    // original, unscaled android Path
//    val rawPath = remember(svgPathData) {
//        androidx.compose.ui.graphics.vector.PathParser()
//            .parsePathString(svgPathData)
//            .toPath()
//            .asAndroidPath()
//    }
//
//    // bounds of the raw path (we’ll use them for scaling)
//    val rawBounds = remember {
//        val r = android.graphics.RectF()
//        rawPath.computeBounds(r, /* exact = */ true)
//        r
//    }
//
//    val progress by animateFloatAsState(
//        targetValue = 1f,
//        animationSpec = tween(durationMs),
//        label = "strokeAnim"
//    )
//    val transformed = remember { android.graphics.Path() }
//    val pm = remember { PathMeasure() }
//
//    Canvas(modifier) {
//        // 1. Build a transform that scales & centres rawPath into this Canvas
//        val scale = 0.8f * min(size.width / rawBounds.width(), size.height / rawBounds.height())
//        val dx = (size.width - rawBounds.width() * scale) / 2f - rawBounds.left * scale
//        val dy = (size.height - rawBounds.height() * scale) / 2f - rawBounds.top * scale
//
//        val m = android.graphics.Matrix().apply {
//            postScale(scale, scale)
//            postTranslate(dx, dy)
//        }
//
//
//        transformed.set(rawPath)
//        transformed.transform(m)              // now fits nicely
//
//        // 2. Use PathMeasure on that transformed path
//        pm.setPath(transformed, /* forceClosed = */ false)
//
//        val seg = android.graphics.Path()
//        pm.getSegment(0f, pm.length * progress, seg, /* startWithMoveTo */ true)
//
//        drawPath(
//            seg.asComposePath(),
//            color = color,
//            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
//        )
//    }
//}

@Composable
fun StrokeGuide(
    svgPathData: String,
    marginRatio: Float = 0.05f,          // 5 % padding around glyph
    durationMs: Int = 2500,              // slower – you’ll see it draw
    color: Color = Color(0xFF5B4CE0),
    modifier: Modifier = Modifier
) {
    /* ------------- parse raw path once ------------- */
    val rawPath = remember(svgPathData) {
        androidx.compose.ui.graphics.vector.PathParser()
            .parsePathString(svgPathData)
            .toPath()
            .asAndroidPath()
    }
    val rawBounds = remember {
        android.graphics.RectF().also { rawPath.computeBounds(it, true) }
    }

    /* ------------- looping progress 0 ➜ 1 ------------- */
    val progress by rememberInfiniteTransition(label = "loop").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glyphDraw"
    )
    val pm = remember { android.graphics.PathMeasure() }
    val pathScaled = remember { android.graphics.Path() }


    Canvas(modifier) {
        /* --- fit glyph into this canvas with padding --- */
        val padX = size.width * marginRatio
        val padY = size.height * marginRatio
        val availW = size.width - padX * 2
        val availH = size.height - padY * 2

        val scale = min(availW / rawBounds.width(), availH / rawBounds.height())
        val dx = padX + (availW - rawBounds.width() * scale) / 2f - rawBounds.left * scale
        val dy = padY + (availH - rawBounds.height() * scale) / 2f - rawBounds.top * scale

        val m = android.graphics.Matrix().apply {
            postScale(scale, scale)
            postTranslate(dx, dy)
        }

        pathScaled.set(rawPath)
        pathScaled.transform(m)

        /* --- stroke width ≈ 3 % of the shortest canvas edge --- */
        val strokePx = 0.03f * min(size.width, size.height)

        /* --- segment according to progress --- */

        pm.setPath(pathScaled, false)
        val seg = android.graphics.Path()
        pm.getSegment(0f, pm.length * progress, seg, true)

        drawPath(
            seg.asComposePath(),
            color = color,
            style = Stroke(width = strokePx, cap = StrokeCap.Round)
        )
    }
}


/* ---------- Cheap similarity check ---------- */
private fun pathSimilarity(user: ComposePath, target: ComposePath): Float {
    val size = 64                 // tiny bitmap for speed
    return toMaskDiff(user.toMask(), target.toMask())
}

private fun toMaskDiff(a: Bitmap, b: Bitmap): Float {
    var matches = 0
    val total = a.width * a.height
    for (y in 0 until a.height) {
        for (x in 0 until a.width) {
            val same = (a[x, y] != 0) == (b[x, y] != 0)
            if (same) matches++
        }
    }
    return matches / total.toFloat()          // 0‒1
}

fun isCloseEnough(user: ComposePath, target: ComposePath) =
    pathSimilarity(user, target) >= 0.8f

private const val MASK_SIZE = 64
private const val PADDING = 0.05f          // 5 %

private fun ComposePath.normalised(): Path {
    val android = this.asAndroidPath()
    val bounds = RectF().also { android.computeBounds(it, true) }

    // leave a 5 % gutter
    val avail = (1f - PADDING * 2)
    val scale = avail * MASK_SIZE / max(bounds.width(), bounds.height())

    val dx = (MASK_SIZE - bounds.width() * scale) / 2f - bounds.left * scale
    val dy = (MASK_SIZE - bounds.height() * scale) / 2f - bounds.top * scale

    return Path().apply {
        transform(Matrix().apply {
            scale(scale, scale)
            translate(dx, dy)
//            postScale(scale, scale)
//            postTranslate(dx, dy)
        })
    }
}

//private fun Path.toMask(): Bitmap {
//    val bmp = Bitmap.createBitmap(MASK_SIZE, MASK_SIZE, Bitmap.Config.ALPHA_8)
//    val c   = Canvas(bmp)
//    val p   = Paint().apply {
//        color = Color.WHITE
//        style = Paint.Style.STROKE
//        strokeWidth = 6f
//        isAntiAlias = true
//        strokeCap = Paint.Cap.ROUND
//        strokeJoin = Paint.Join.ROUND
//    }
//    c.drawPath(this, p)
//    return bmp
//}
fun ComposePath.toMask(): Bitmap {
    val bmp = createBitmap(MASK_SIZE, MASK_SIZE, Bitmap.Config.ALPHA_8)
    val c = AndroidCanvas(bmp)
    c.drawColor(android.graphics.Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
    val p = Paint().apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 6f
        isAntiAlias = true
    }
    c.drawPath(this@toMask.toAndroid(), p)
    return bmp
}

private fun bitmapSimilarity(a: Bitmap, b: Bitmap): Float {
    var same = 0
    val total = MASK_SIZE * MASK_SIZE
    for (y in 0 until MASK_SIZE) {
        for (x in 0 until MASK_SIZE) {
            val hitA = a.getPixel(x, y) != 0
            val hitB = b.getPixel(x, y) != 0
            if (hitA == hitB) same++
        }
    }
    return same / total.toFloat()      // 1 == identical
}

/** Public entry – call from onStrokeFinished */
fun pathsAreClose(user: ComposePath, perfect: ComposePath, threshold: Float = 0.7f): Boolean {
    val a = user.normalised().toMask()
    val b = perfect.normalised().toMask()
    return bitmapSimilarity(a, b) >= threshold
}

@Preview(showBackground = true)
@Composable
private fun FirstPathPreview() {
    FirstPath()
}