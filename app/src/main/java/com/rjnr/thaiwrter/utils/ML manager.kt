package com.rjnr.thaiwrter.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import com.rjnr.thaiwrter.data.models.Point
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.util.concurrent.locks.ReentrantLock

data class CharacterPrediction(
    val characterIndex: Int,
    val confidence: Float,
    val alternativePredictions: List<Pair<Int, Float>>
) {
    val character: String
        get() = MLStrokeValidator.getCharacterFromIndex(characterIndex)

    val alternativeCharacters: List<Pair<String, Float>>
        get() = alternativePredictions.map {
            MLStrokeValidator.getCharacterFromIndex(it.first) to it.second
        }

    val pronunciation: String
        get() = MLStrokeValidator.getPronunciation(characterIndex)
}

// MLStrokeValidator.kt
class MLStrokeValidator(private val context: Context) {
    private var interpreter: Interpreter? = null
    private val imageSize = 256
    private val lock = ReentrantLock()

    init {
        loadModel()
    }

    private fun loadModel() {
        try {
            val modelFile = "thai_character_model.tflite"
            val options = Interpreter.Options().apply {
                setNumThreads(4)
                useNNAPI = true // Use Neural Network API if available
            }
            interpreter = Interpreter(loadModelFile(context, modelFile), options)
            Log.d("MLStrokeValidator", "Model loaded successfully")
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

    //    fun predictCharacter(points: List<Point>, width: Float, height: Float): CharacterPrediction? {
//        return try {
//            lock.withLock {
//                val bitmap = convertStrokeToBitmap(points, width, height)
//                val inputArray = preprocessImage(bitmap)
//
//                // Prepare output array
//                val outputArray = Array(1) { FloatArray(55) } // 55 classes
//
//                // Run inference
//                interpreter?.run(inputArray, outputArray)
//
//                // Process results
//                processResults(outputArray[0])
//            }
//        } catch (e: Exception) {
//            Log.e("MLStrokeValidator", "Error during prediction", e)
//            null
//        }
//    }
    fun predictCharacter(points: List<Point>, width: Float, height: Float): CharacterPrediction? {
        return try {
            val bitmap = convertStrokeToBitmap(points, width, height)

            // Debug output for stroke conversion
            Log.d("MLStrokeValidator", "Original bitmap size: ${bitmap.width}x${bitmap.height}")

            val inputArray = preprocessImage(bitmap)
            val outputArray = Array(1) { FloatArray(55) }

            interpreter?.run(inputArray, outputArray)

            // Debug output for prediction
            val predictions = outputArray[0].mapIndexed { index, value ->
                index to value
            }.sortedByDescending { it.second }

            Log.d("MLStrokeValidator", "Top 3 predictions:")
            predictions.take(3).forEach { (index, conf) ->
                Log.d("MLStrokeValidator", "${getCharacterFromIndex(index)}: ${conf * 100}%")
            }

            processResults(outputArray[0])
        } catch (e: Exception) {
            Log.e("MLStrokeValidator", "Error during prediction", e)
            null
        }
    }

    private fun convertToGrayscale(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val grayscaleBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(grayscaleBitmap)

        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(ColorMatrix().apply {
                setSaturation(0f)
            })
        }

        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return grayscaleBitmap
    }    private fun convertStrokeToBitmap(points: List<Point>, width: Float, height: Float): Bitmap {
        val bitmap = Bitmap.createBitmap(imageSize, imageSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Set white background
        canvas.drawColor(Color.WHITE)

        // Scale points to fit image size
        val scaleX = imageSize / width
        val scaleY = imageSize / height

        // Draw strokes in black
        val paint = Paint().apply {
            color = Color.BLACK
            strokeWidth = 8f * scaleX // Scale stroke width
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            isAntiAlias = true
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

    //    private fun preprocessImage(bitmap: Bitmap): Array<Array<Array<FloatArray>>> {
//        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, imageSize, imageSize, true)
//        val grayscaleBitmap = convertToGrayscale(scaledBitmap)
//
//        val inputArray = Array(1) {
//            Array(imageSize) {
//                Array(imageSize) {
//                    FloatArray(1)
//                }
//            }
//        }
//
//        // Match the training preprocessing exactly
//        for (y in 0 until imageSize) {
//            for (x in 0 until imageSize) {
//                val pixel = grayscaleBitmap.getPixel(x, y)
//                // Convert to grayscale and normalize to [0,1]
//                val gray = Color.red(pixel) / 255f
//                inputArray[0][y][x][0] = gray
//            }
//        }
//
//        return inputArray
//    }
    private fun preprocessImage(bitmap: Bitmap): Array<Array<Array<FloatArray>>> {
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, imageSize, imageSize, true)

        // Create a padded bitmap to ensure the character is centered
        val paddedBitmap = Bitmap.createBitmap(imageSize, imageSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(paddedBitmap)
        canvas.drawColor(Color.WHITE)

        // Calculate padding to center the character
        val bounds = calculateBounds(scaledBitmap)
        val centerX = (imageSize - (bounds.right - bounds.left)) / 2f
        val centerY = (imageSize - (bounds.bottom - bounds.top)) / 2f

        // Draw centered character
        canvas.drawBitmap(scaledBitmap, centerX, centerY, Paint())

        // Convert to grayscale
        val grayscaleBitmap = convertToGrayscale(paddedBitmap)

        // Create input array
        val inputArray = Array(1) {
            Array(imageSize) {
                Array(imageSize) {
                    FloatArray(1)
                }
            }
        }

        // Fill array with normalized values
        for (y in 0 until imageSize) {
            for (x in 0 until imageSize) {
                val pixel = grayscaleBitmap.getPixel(x, y)
                val value = 1f - (Color.red(pixel) / 255f)  // Invert values: black=1, white=0
                inputArray[0][y][x][0] = value
            }
        }

        return inputArray
    }

    private fun calculateBounds(bitmap: Bitmap): Rect {
        val bounds = Rect()
        var minX = bitmap.width
        var minY = bitmap.height
        var maxX = 0
        var maxY = 0

        // Find the bounds of the character (non-white pixels)
        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                val pixel = bitmap.getPixel(x, y)
                if (pixel != Color.WHITE) {
                    minX = minOf(minX, x)
                    minY = minOf(minY, y)
                    maxX = maxOf(maxX, x)
                    maxY = maxOf(maxY, y)
                }
            }
        }

        bounds.set(minX, minY, maxX, maxY)
        return bounds
    }

    private fun processResults(outputs: FloatArray): CharacterPrediction {
        val predictions = outputs.mapIndexed { index, confidence ->
            Pair(index, confidence)
        }.sortedByDescending { it.second }

        return CharacterPrediction(
            characterIndex = predictions[0].first,
            confidence = predictions[0].second,
            alternativePredictions = predictions.subList(1, minOf(5, predictions.size))
        )
    }

    // MLStrokeValidator.kt
    companion object {
        val CHARACTER_MAP = mapOf(
            0 to "ก",   1 to "ข",   2 to "ฃ",   3 to "ค",   4 to "ฅ",   5 to "ฆ",   6 to "ง",
            7 to "จ",   8 to "ฉ",   9 to "ช",   10 to "ซ",  11 to "ฌ",  12 to "ญ",  13 to "ฎ",
            14 to "ฏ",  15 to "ฐ",  16 to "ฑ",  17 to "ฒ",  18 to "ณ",  19 to "ด",  20 to "ต",
            21 to "ถ",  22 to "ท",  23 to "ธ",  24 to "น",  25 to "บ",  26 to "ป",  27 to "ผ",
            28 to "ฝ",  29 to "พ",  30 to "ฟ",  31 to "ภ",  32 to "ม",  33 to "ย",  34 to "ร",
            35 to "ฤ",  36 to "ล",  37 to "ว",  38 to "ศ",  39 to "ษ",  40 to "ส",  41 to "ห",
            42 to "ฬ",  43 to "อ",  44 to "อะ", 45 to "อา", 46 to "อำ", 47 to "ฮ",  48 to "ฯ",
            49 to "เ",  50 to "แ",  51 to "โ",  52 to "ใ",  53 to "ไ",  54 to "ๆ"
        )

        fun getCharacterFromIndex(index: Int): String = CHARACTER_MAP[index] ?: "?"

        fun getPronunciation(index: Int): String = when(index) {
            0 -> "ko kai"      1 -> "kho khai"    2 -> "kho khuat"    3 -> "kho khwai"
            4 -> "kho khon"     5 -> "kho rakhang"  6 -> "ngo ngu"     7 -> "cho chan"
            8 -> "cho ching"    9 -> "cho chang"    10 -> "so so"       11 -> "cho choe"
            12 -> "yo ying"     13 -> "do chada"    14 -> "to patak"    15 -> "tho than"
            16 -> "tho montho"  17 -> "tho phuthao" 18 -> "no nen"     19 -> "do dek"
            20 -> "to tao"      21 -> "tho thung"  22 -> "tho thahan" 23 -> "tho thong"
            24 -> "no nu"       25 -> "bo baimai"   26 -> "po pla"     27 -> "pho phueng"
            28 -> "fo fa"       29 -> "pho phan"   30 -> "fo fan"      31 -> "pho samphao"
            32 -> "mo ma"       33 -> "yo yak"      34 -> "ro ruea"     35 -> "ru"
            36 -> "lo ling"     37 -> "wo waen"     38 -> "so sala"     39 -> "so ruesi"
            40 -> "so suea"     41 -> "ho hip"      42 -> "lo chula"    43 -> "o ang"
            44 -> "sara a"      45 -> "sara aa"     46 -> "sara am"     47 -> "ho nokhuk"
            48 -> "paiyannoi"   49 -> "sara e"      50 -> "sara ae"    51 -> "sara o"
            52 -> "sara ai mai muan" 53 -> "sara ai mai malai" 54 -> "mai yamok"
            else -> ""
        }

        fun getAllCharacters(): List<Pair<String, String>> {
            return CHARACTER_MAP.map { (index, char) ->
                char to getPronunciation(index)
            }
        }

        fun getRandomCharacter(): Pair<String, String> {
            val randomIndex = CHARACTER_MAP.keys.random()
            return CHARACTER_MAP[randomIndex]!! to getPronunciation(randomIndex)
        }
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

//private fun convertToGrayscale(bitmap: Bitmap): Bitmap {
//    val width = bitmap.width
//    val height = bitmap.height
//    val grayscaleBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//    val canvas = Canvas(grayscaleBitmap)
//
//    val paint = Paint().apply {
//        colorFilter = ColorMatrixColorFilter(ColorMatrix().apply {
//            // Standard grayscale conversion matrix
//            setSaturation(0f)
//        })
//    }
//
//    // Draw the original bitmap with the grayscale color filter
//    canvas.drawBitmap(bitmap, 0f, 0f, paint)
//
//    // Invert colors if necessary to match training data
//    // (black text on white background or vice versa)
//    val invertColors = ColorMatrixColorFilter(ColorMatrix().apply {
//        val matrix = floatArrayOf(
//            -1f, 0f, 0f, 0f, 255f,  // Red
//            0f, -1f, 0f, 0f, 255f,  // Green
//            0f, 0f, -1f, 0f, 255f,  // Blue
//            0f, 0f, 0f, 1f, 0f      // Alpha
//        )
//        set(matrix)
//    })
//
//    val invertPaint = Paint().apply {
//        colorFilter = invertColors
//    }
//
//    val finalBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//    val finalCanvas = Canvas(finalBitmap)
//    finalCanvas.drawBitmap(grayscaleBitmap, 0f, 0f, invertPaint)
//
//    return finalBitmap
//}