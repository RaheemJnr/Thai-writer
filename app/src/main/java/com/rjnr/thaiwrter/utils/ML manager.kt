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
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

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

    fun predictCharacter(points: List<Point>, width: Float, height: Float): CharacterPrediction? {
        return try {
            lock.withLock {
                val bitmap = convertStrokeToBitmap(points, width, height)
                val inputArray = preprocessImage(bitmap)

                // Prepare output array
                val outputArray = Array(1) { FloatArray(55) } // 55 classes

                // Run inference
                interpreter?.run(inputArray, outputArray)

                // Process results
                processResults(outputArray[0])
            }
        } catch (e: Exception) {
            Log.e("MLStrokeValidator", "Error during prediction", e)
            null
        }
    }

    private fun convertStrokeToBitmap(points: List<Point>, width: Float, height: Float): Bitmap {
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

    private fun preprocessImage(bitmap: Bitmap): Array<Array<Array<FloatArray>>> {
        val inputArray = Array(1) {
            Array(imageSize) {
                Array(imageSize) {
                    FloatArray(1)
                }
            }
        }

        for (y in 0 until imageSize) {
            for (x in 0 until imageSize) {
                val pixel = bitmap.getPixel(x, y)
                // Convert to grayscale and normalize
                val gray = 1f - (Color.red(pixel) / 255f)
                inputArray[0][y][x][0] = gray
            }
        }

        return inputArray
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
            // Consonants
            0 to "ก",  // ko kai
            1 to "ข",  // kho khai
            2 to "ฃ",  // kho khuat
            3 to "ค",  // kho khwai
            4 to "ฅ",  // kho khon
            5 to "ฆ",  // kho rakhang
            6 to "ง",  // ngo ngu
            7 to "จ",  // cho chan
            8 to "ฉ",  // cho ching
            9 to "ช",  // cho chang
            10 to "ซ", // so so
            11 to "ฌ", // cho choe
            12 to "ญ", // yo ying
            13 to "ฎ", // do chada
            14 to "ฏ", // to patak
            15 to "ฐ", // tho than
            16 to "ฑ", // tho nangmontho
            17 to "ฒ", // tho phuthao
            18 to "ณ", // no nen
            19 to "ด", // do dek
            20 to "ต", // to tao
            21 to "ถ", // tho thung
            22 to "ท", // tho thahan
            23 to "ธ", // tho thong
            24 to "น", // no nu
            25 to "บ", // bo baimai
            26 to "ป", // po pla
            27 to "ผ", // pho phueng
            28 to "ฝ", // fo fa
            29 to "พ", // pho phan
            30 to "ฟ", // fo fan
            31 to "ภ", // pho samphao
            32 to "ม", // mo ma
            33 to "ย", // yo yak
            34 to "ร", // ro ruea
            35 to "ล", // lo ling
            36 to "ว", // wo waen
            37 to "ศ", // so sala
            38 to "ษ", // so ruesi
            39 to "ส", // so suea
            40 to "ห", // ho hip
            41 to "ฬ", // lo chula
            42 to "อ", // o ang
            43 to "ฮ", // ho nokhuk

            // Vowels and tone marks
            44 to "ะ", // sara a
            45 to "า", // sara aa
            46 to "ิ",  // sara i
            47 to "ี",  // sara ii
            48 to "ึ",  // sara ue
            49 to "ื",  // sara uue
            50 to "ุ",  // sara u
            51 to "ู",  // sara uu
            52 to "เ", // sara e
            53 to "แ", // sara ae
            54 to "โ"  // sara o
        )

        // Helper function to get character from index
        fun getCharacterFromIndex(index: Int): String {
            return CHARACTER_MAP[index] ?: "?"
        }

        fun getPronunciation(index: Int): String {
            return when(index) {
                // Consonants
                0 -> "ko kai"
                1 -> "kho khai"
                2 -> "kho khuat"
                3 -> "kho khwai"
                4 -> "kho khon"
                5 -> "kho rakhang"
                6 -> "ngo ngu"
                7 -> "cho chan"
                8 -> "cho ching"
                9 -> "cho chang"
                10 -> "so so"
                11 -> "cho choe"
                12 -> "yo ying"
                13 -> "do chada"
                14 -> "to patak"
                15 -> "tho than"
                16 -> "tho nangmontho"
                17 -> "tho phuthao"
                18 -> "no nen"
                19 -> "do dek"
                20 -> "to tao"
                21 -> "tho thung"
                22 -> "tho thahan"
                23 -> "tho thong"
                24 -> "no nu"
                25 -> "bo baimai"
                26 -> "po pla"
                27 -> "pho phueng"
                28 -> "fo fa"
                29 -> "pho phan"
                30 -> "fo fan"
                31 -> "pho samphao"
                32 -> "mo ma"
                33 -> "yo yak"
                34 -> "ro ruea"
                35 -> "lo ling"
                36 -> "wo waen"
                37 -> "so sala"
                38 -> "so ruesi"
                39 -> "so suea"
                40 -> "ho hip"
                41 -> "lo chula"
                42 -> "o ang"
                43 -> "ho nokhuk"

                // Vowels and tone marks
                44 -> "sara a"
                45 -> "sara aa"
                46 -> "sara i"
                47 -> "sara ii"
                48 -> "sara ue"
                49 -> "sara uue"
                50 -> "sara u"
                51 -> "sara uu"
                52 -> "sara e"
                53 -> "sara ae"
                54 -> "sara o"

                else -> ""
            }
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