package com.rjnr.thaiwrter.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import com.rjnr.thaiwrter.data.models.Point
import com.rjnr.thaiwrter.data.models.Stroke
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

data class CharacterPrediction(
    val characterIndex: Int,
    val confidence: Float,
    val alternativePredictions: List<Pair<Int, Float>>
)
// MLStrokeValidator.kt
class MLStrokeValidator(private val context: Context) {
    private var interpreter: Interpreter? = null
    private val imageSize = 64 // Match your model's input size

    init {
        loadModel()
    }

    private fun loadModel() {
        try {
            val modelFile = "thai_character_model.tflite" // Your model filename
            val options = Interpreter.Options()
            interpreter = Interpreter(loadModelFile(context, modelFile), options)
        } catch (e: Exception) {
            Log.e("MLStrokeValidator", "Error loading model", e)
        }
    }

    private fun loadModelFile(context: Context, filename: String): ByteBuffer {
        val modelPath = context.assets.openFd(filename)
        val inputStream = FileInputStream(modelPath.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = modelPath.startOffset
        val declaredLength = modelPath.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun predictCharacter(points: List<Point>, width: Float, height: Float): CharacterPrediction {
        // Convert stroke to bitmap
        val bitmap = convertStrokeToBitmap(points, width, height)

        // Prepare input
        val inputBuffer = prepareInput(bitmap)

        // Run inference
        val outputBuffer = ByteBuffer.allocateDirect(NUM_CHARACTERS * 4) // Adjust size based on your model
        outputBuffer.order(ByteOrder.nativeOrder())

        interpreter?.run(inputBuffer, outputBuffer)

        // Process results
        outputBuffer.rewind()
        val results = FloatArray(NUM_CHARACTERS)
        outputBuffer.asFloatBuffer().get(results)

        // Get top predictions
        return processResults(results)
    }

    private fun convertStrokeToBitmap(points: List<Point>, width: Float, height: Float): Bitmap {
        val bitmap = Bitmap.createBitmap(imageSize, imageSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Scale points to fit image size
        val scaleX = imageSize / width
        val scaleY = imageSize / height

        val paint = Paint().apply {
            color = Color.BLACK
            strokeWidth = 3f
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }

        val path = android.graphics.Path()
        points.forEachIndexed { index, point ->
            val scaledX = point.x * scaleX
            val scaledY = point.y * scaleY
            if (index == 0) {
                path.moveTo(scaledX, scaledY)
            } else {
                path.lineTo(scaledX, scaledY)
            }
        }

        canvas.drawPath(path, paint)
        return bitmap
    }

    private fun prepareInput(bitmap: Bitmap): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(imageSize * imageSize * 4)
        buffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(imageSize * imageSize)
        bitmap.getPixels(pixels, 0, imageSize, 0, 0, imageSize, imageSize)

        pixels.forEach { pixel ->
            val r = (pixel shr 16 and 0xFF) / 255.0f
            val g = (pixel shr 8 and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f
            buffer.putFloat((r + g + b) / 3.0f)
        }

        buffer.rewind()
        return buffer
    }

    private fun processResults(results: FloatArray): CharacterPrediction {
        val predictions = results.mapIndexed { index, confidence ->
            Pair(index, confidence)
        }.sortedByDescending { it.second }

        return CharacterPrediction(
            characterIndex = predictions[0].first,
            confidence = predictions[0].second,
            alternativePredictions = predictions.subList(1, minOf(5, predictions.size))
        )
    }

    companion object {
        private const val NUM_CHARACTERS = 44 // Adjust based on your character set
    }
}



// Enhanced StrokeValidator
object StrokeValidator : KoinComponent {
    private val mlValidator: MLStrokeValidator? by inject()



    fun validateStroke(
        drawnPoints: List<Point>,
        targetStroke: Stroke,
        canvasWidth: Float,
        canvasHeight: Float
    ): ValidationResult {
        // Original DTW validation
        val dtwResult = validateWithDTW(drawnPoints, targetStroke, canvasWidth, canvasHeight)

        // ML prediction (if available)
        val mlPrediction = mlValidator?.predictCharacter(drawnPoints, canvasWidth, canvasHeight)

        // Combine results
        return ValidationResult(
            isValid = dtwResult,
            mlPrediction = mlPrediction,
            confidence = mlPrediction?.confidence ?: 0f
        )
    }

    private fun validateWithDTW(
        drawnPoints: List<Point>,
        targetStroke: Stroke,
        canvasWidth: Float,
        canvasHeight: Float
    ): Boolean {
        // 1. Normalize and resample both strokes
        val drawnNormalized = normalizePoints(drawnPoints, canvasWidth, canvasHeight)
        val drawnResampled = resamplePoints(drawnNormalized, RESAMPLE_POINTS)

        val targetResampled = resamplePoints(targetStroke.points, RESAMPLE_POINTS)

        // 2. Calculate DTW distance
        val distance = calculateDTW(drawnResampled, targetResampled)

        // 3. Check direction similarity
        val dirSimilarity = directionSimilarity(
            drawnResampled.first(),
            drawnResampled.last(),
            targetResampled.first(),
            targetResampled.last()
        )

        return distance < DTW_THRESHOLD && dirSimilarity > 0.7f
    }
}
private fun resamplePoints(points: List<Point>, targetCount: Int): List<Point> {
    if (points.size < 2) return points
    val interval = pathLength(points) / (targetCount - 1)
    var currentDistance = 0f
    val newPoints = mutableListOf(points.first())
    var prevPoint = points.first()

    for (i in 1 until points.size) {
        val dist = distanceBetween(prevPoint, points[i])
        if (currentDistance + dist >= interval) {
            val ratio = (interval - currentDistance) / dist
            val newX = prevPoint.x + ratio * (points[i].x - prevPoint.x)
            val newY = prevPoint.y + ratio * (points[i].y - prevPoint.y)
            newPoints.add(Point(newX, newY))
            currentDistance = 0f
            prevPoint = Point(newX, newY)
        } else {
            currentDistance += dist
        }
    }

    while (newPoints.size < targetCount) {
        newPoints.add(points.last())
    }
    return newPoints
}

private fun normalizePoints(
    points: List<Point>,
    width: Float,
    height: Float
): List<Point> {
    return points.map {
        Point(it.x / width, it.y / height)
    }
}

data class ValidationResult(
    val isValid: Boolean,
    val mlPrediction: CharacterPrediction?,
    val confidence: Float
)