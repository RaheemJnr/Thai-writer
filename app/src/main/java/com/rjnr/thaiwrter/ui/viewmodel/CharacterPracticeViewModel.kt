package com.rjnr.thaiwrter.ui.viewmodel

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rjnr.thaiwrter.data.models.ThaiCharacter
import com.rjnr.thaiwrter.data.models.UserProgress
import com.rjnr.thaiwrter.data.repository.ThaiLanguageRepository
import com.rjnr.thaiwrter.ui.drawing.PathWithColor
import com.rjnr.thaiwrter.ui.drawing.Point
import com.rjnr.thaiwrter.utils.StrokeValidator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class CharacterPracticeViewModel(
    private val repository: ThaiLanguageRepository
) : ViewModel() {
    private val _currentCharacter = MutableStateFlow<ThaiCharacter?>(null)
    val currentCharacter = _currentCharacter.asStateFlow()
    private val _strokeFeedback = MutableStateFlow<StrokeFeedback?>(null)
    val strokeFeedback = _strokeFeedback.asStateFlow()

    //
    private val _isDrawingEnabled = MutableStateFlow(true)



    private val _paths = MutableStateFlow<List<PathWithColor>>(emptyList())
    val paths = _paths.asStateFlow()

    private val _currentStrokeIndex = MutableStateFlow(0)
    val currentStrokeIndex = _currentStrokeIndex.asStateFlow()

    init {
        Log.d("CharacterPracticeVM", "ViewModel initialized")
        loadNextCharacter()
    }

    //    private fun loadNextCharacter() {
//        viewModelScope.launch {
//            repository.getCharactersByDifficulty(1)
//                .firstOrNull()
//                ?.let { characters ->
//                    Log.d("CharacterPracticeVM", "Loaded characters: ${characters.size}")
//                    _currentCharacter.value = characters.random()
//                    Log.d(
//                        "CharacterPracticeVM",
//                        "Selected character: ${_currentCharacter.value?.character}"
//                    )
//                    _currentStrokeIndex.value = 0
//                    _paths.value = emptyList()
//                }
//        }
//    }
    private fun loadNextCharacter() {
        viewModelScope.launch {
            repository.getCharactersByDifficulty(1)
                .collect { characters ->
                    if (characters.isNotEmpty()) {
                        _currentCharacter.value = characters.random()
                        _currentStrokeIndex.value = 0
                        _paths.value = emptyList()
                    }
                }
        }
    }

    //    fun validateStroke(points: List<Point>, width: Int, height: Int) {
//        viewModelScope.launch {
//            currentCharacter.value?.let { char ->
//                val strokeData = char.strokeData
//
//                if (_currentStrokeIndex.value < strokeData.strokes.size) {
//                    val isValid = StrokeValidator.validateStroke(
//                        points,
//                        strokeData.strokes[_currentStrokeIndex.value],
//                        width.toFloat(),
//                        height.toFloat()
//                    )
//
//                    _paths.value += PathWithColor(
//                        Path().apply {
//                            moveTo(points.first().x.toFloat(), points.first().y.toFloat())
//                            points.forEach { lineTo(it.x.toFloat(), it.y.toFloat()) }
//                        },
//                        if (isValid) Color.Green else Color.Red
//                    )
//
//                    if (isValid) {
//                        _strokeFeedback.value = StrokeFeedback.Correct
//                        _currentStrokeIndex.value += 1
//
//                        if (_currentStrokeIndex.value >= strokeData.strokes.size) {
//                            updateProgress()
//                            delay(500)
//                            loadNextCharacter()
//                        }
//                    } else {
//                        _strokeFeedback.value = StrokeFeedback.Incorrect
//                    }
//
//                    // Reset feedback after delay
//                    delay(1000)
//                    _strokeFeedback.value = null
//                }
//            }
//        }
//    }
//    fun validateStroke(points: List<Point>, width: Int, height: Int) {
//        viewModelScope.launch {
//            currentCharacter.value?.let { char ->
//                if (_currentStrokeIndex.value < char.strokeData.strokes.size) {
//                    val isValid = StrokeValidator.validateStroke(
//                        drawnPoints = points,
//                        targetStroke = char.strokeData.strokes[_currentStrokeIndex.value],
//                        canvasWidth = width.toFloat(),
//                        canvasHeight = height.toFloat()
//                    )
//
//                    // Create path from points
//                    val newPath = Path().apply {
//                        moveTo(points.first().x, points.first().y)
//                        points.drop(1).forEach { lineTo(it.x, it.y) }
//                    }
//
//                    // Add new path with color based on validation
//                    _paths.value = _paths.value + PathWithColor(
//                        path = newPath,
//                        color = if (isValid) Color.Green else Color.Red
//                    )
//
//                    if (isValid) {
//                        _strokeFeedback.value = StrokeFeedback.Correct
//                        _currentStrokeIndex.value += 1  // Move to next stroke
//
//                        // Check if character is complete
//                        if (_currentStrokeIndex.value >= char.strokeData.strokes.size) {
//                            updateProgress()
//                            delay(500)
//                            loadNextCharacter()
//                        }
//                    } else {
//                        _strokeFeedback.value = StrokeFeedback.Incorrect
//                    }
//
//                    delay(1000)
//                    _strokeFeedback.value = null
//                }
//            }
//        }
//    }
    fun validateStroke(points: List<Point>, width: Int, height: Int) {
        viewModelScope.launch {
            if (!_isDrawingEnabled.value) return@launch

            _isDrawingEnabled.value = false
            currentCharacter.value?.let { char ->
                if (currentStrokeIndex.value >= char.strokeData.strokes.size) return@launch

                val isValid = StrokeValidator.validateStroke(
                    drawnPoints = points,
                    targetStroke = char.strokeData.strokes[currentStrokeIndex.value],
                    canvasWidth = width.toFloat(),
                    canvasHeight = height.toFloat()
                )

                // Update paths
                val newPath = Path().apply {
                    points.forEachIndexed { i, p ->
                        if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                    }
                }

                _paths.value = _paths.value + PathWithColor(
                    path = newPath,
                    color = if (isValid) Color.Green.copy(alpha = 0.5f) else Color.Red.copy(alpha = 0.3f)
                )

                if (isValid) {
                    handleCorrectStroke()
                } else {
                    _strokeFeedback.value = StrokeFeedback.Incorrect
                    delay(1000)
                    _strokeFeedback.value = null
                }
            }
            _isDrawingEnabled.value = true
        }
    }

    private fun handleCorrectStroke() {
        viewModelScope.launch {
            _strokeFeedback.value = StrokeFeedback.Correct
            _currentStrokeIndex.value += 1

            if (_currentStrokeIndex.value >= (currentCharacter.value?.strokeData?.strokes?.size
                    ?: 0)
            ) {

                delay(500)
                updateProgress()
                loadNextCharacter()
            }

            delay(500)
            _strokeFeedback.value = null
        }
    }


    fun clearCanvas() {
        _paths.value = emptyList()
        _currentStrokeIndex.value = 0
        _strokeFeedback.value = null
    }

    fun checkAnswer() {
        viewModelScope.launch {
            currentCharacter.value?.let { char ->
                val strokeData = char.strokeData
                if (_currentStrokeIndex.value >= strokeData.strokes.size) {
                    _strokeFeedback.value = StrokeFeedback.Correct
                    updateProgress()
                    delay(500)
                    loadNextCharacter()
                } else {
                    _strokeFeedback.value = StrokeFeedback.Incorrect
                }
                delay(1000)
                _strokeFeedback.value = null
            }
        }
    }

    fun skipCharacter() {
        clearCanvas()
        loadNextCharacter()
    }

//    fun onStrokeFinished(points: List<Point>, isValid: Boolean) {
//        viewModelScope.launch {
//            if (isValid) {
//                _strokeFeedback.value = StrokeFeedback.Correct
//                _currentStrokeIndex.value += 1
//
//                // Check if character is complete
//                val strokeData = currentCharacter.value?.strokeData ?: return@launch
//
//
//                if (_currentStrokeIndex.value >= strokeData.strokes.size) {
//                    // Character complete
//                    updateProgress()
//                    delay(500) // Give time for feedback
//                    loadNextCharacter()
//                    _currentStrokeIndex.value = 0
//                }
//            } else {
//                _strokeFeedback.value = StrokeFeedback.Incorrect
//            }
//
//            // Reset feedback after delay
//            delay(1000)
//            _strokeFeedback.value = null
//        }
//    }

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

sealed class StrokeFeedback {
    data object Correct : StrokeFeedback()
    data object Incorrect : StrokeFeedback()
}