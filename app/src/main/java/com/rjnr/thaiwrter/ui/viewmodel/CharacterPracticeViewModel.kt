package com.rjnr.thaiwrter.ui.viewmodel

import android.util.Log
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rjnr.thaiwrter.data.models.Point
import com.rjnr.thaiwrter.data.models.ThaiCharacter
import com.rjnr.thaiwrter.data.models.UserProgress
import com.rjnr.thaiwrter.data.repository.ThaiLanguageRepository
import com.rjnr.thaiwrter.utils.CharacterPrediction
import com.rjnr.thaiwrter.utils.MLStrokeValidator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class CharacterPracticeViewModel(
    private val repository: ThaiLanguageRepository,
    private val mlStrokeValidator: MLStrokeValidator
) : ViewModel()
{
    private val allCharacters = MLStrokeValidator.CHARACTER_MAP.map { (index, char) ->
        ThaiCharacter(
            id = index,
            character = char,
            pronunciation = MLStrokeValidator.getPronunciation(index)
        )
    }
    private val _currentCharacter = MutableStateFlow<ThaiCharacter?>(null)
    val currentCharacter = _currentCharacter.asStateFlow()

    private val _shouldClearCanvas = MutableStateFlow(false)
    val shouldClearCanvas = _shouldClearCanvas.asStateFlow()

    private val _prediction = MutableStateFlow<CharacterPrediction?>(null)
    val prediction = _prediction.asStateFlow()
    //
    // Add these properties to CharacterPracticeViewModel
    private val _drawingPaths = MutableStateFlow<List<Path>>(emptyList())
    val drawingPaths = _drawingPaths.asStateFlow()

    private val _pathColor = MutableStateFlow(Color.Black)
    val pathColor = _pathColor.asStateFlow()

    private val _isCorrect = MutableStateFlow(false)
    val isCorrect = _isCorrect.asStateFlow()

    private val _instructionText = MutableStateFlow("trace the character")
    val instructionText = _instructionText.asStateFlow()

    private val _showGuide = MutableStateFlow(true)
    val showGuide = _showGuide.asStateFlow()

    init {
        loadNextCharacter()
    }

    fun onDrawingComplete(points: List<Point>, width: Int, height: Int) {
        viewModelScope.launch {
            val prediction =
                mlStrokeValidator.predictCharacter(points, width.toFloat(), height.toFloat())
            prediction?.let {
                Log.d("Prediction", "Character: ${it.character}")
                Log.d("Prediction", "Confidence: ${it.confidence * 100}%")
                Log.d("Prediction", "Alternatives: ${it.alternativeCharacters}")
            }
            _prediction.value = prediction
        }
    }

    fun clearCanvas() {
        _prediction.value = null
        _shouldClearCanvas.value = true
        // Reset the flag after a brief delay
        viewModelScope.launch {
            delay(100)
            _shouldClearCanvas.value = false
        }
    }

//    fun checkAnswer() {
//        viewModelScope.launch {
//            // Compare prediction with current character
//            prediction.value?.let { pred ->
//                if (pred.confidence > 0.7f) {  // Adjust threshold as needed
//                    updateProgress()
//                }
//            }
//        }
//    }
fun checkAnswer() {
    viewModelScope.launch {
        // Compare prediction with current character
        prediction.value?.let { pred ->
            val isCorrectPrediction = currentCharacter.value?.id == pred.characterIndex &&
                    pred.confidence > 0.7f

            if (isCorrectPrediction) {
                // Animate to green color
                _pathColor.value = Color.Green
                _isCorrect.value = true
                _instructionText.value = "tap to advance"
                updateProgress()
            } else {
                // Show feedback
                _isCorrect.value = false
            }
        }
    }
}
    // New method to handle animated correction
    fun animateCorrection() {
        viewModelScope.launch {
            // This would morph the path to the correct shape
            // For a placeholder effect:
            delay(500) // Simulate morph animation time
            _pathColor.value = Color.Green
            _isCorrect.value = true
            _instructionText.value = "tap to advance"
        }
    }

    // Update next character to progress through modes
    fun nextCharacter() {
        clearCanvas()
        if (_isCorrect.value) {
            // If completed correctly, load next character
            _isCorrect.value = false
            _pathColor.value = Color.Black

            // First time show with guide, second time without
            _showGuide.value = !_showGuide.value
            _instructionText.value = if (_showGuide.value) "trace the character" else "write the character"

            loadNextCharacter()
        } else {
            // If still practicing, just clear the canvas
            _instructionText.value = if (_showGuide.value) "trace the character" else "write the character"
        }
    }

//    fun nextCharacter() {
//        clearCanvas()
//        loadNextCharacter()
//    }

    private fun loadNextCharacter() {
        _currentCharacter.value = allCharacters.random()
        clearCanvas()
    }
    fun skipCharacter() {
        loadNextCharacter()
    }
    private suspend fun updateProgress() {
        currentCharacter.value?.let { char ->
            repository.updateUserProgress(
                UserProgress(
                    characterId = char.id,
                    lastReviewed = System.currentTimeMillis(),
                    nextReviewDate = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1),
                    correctCount = 1,
                    incorrectCount = 0,
                    streak = 1,
                    easeFactor = 2.5f
                )
            )
        }
    }
}



