package com.rjnr.thaiwrter.ui.viewmodel

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.PathParser
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rjnr.thaiwrter.data.models.ThaiCharacter
import com.rjnr.thaiwrter.data.repository.ThaiLanguageRepository
import com.rjnr.thaiwrter.ui.drawing.pathsAreClose
import com.rjnr.thaiwrter.utils.MLStrokeValidator
import com.rjnr.thaiwrter.utils.SoundManager
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException


// Add to your ViewModel or a separate file

enum class PracticeStep {
    INITIAL,
    GUIDE_AND_TRACE,
    CROSS_FADING_TRACE,  // After guided tracing
    AWAITING_BLANK_SLATE,
    USER_WRITING_BLANK,
    CROSS_FADING_WRITE, // After blank slate writing
    AWAITING_NEXT_CHARACTER
}

// In CharacterPracticeViewModel
class CharacterPracticeViewModel(
    private val repository: ThaiLanguageRepository,
    private val mlStrokeValidator: MLStrokeValidator,
    private val soundManager: SoundManager
) : ViewModel() {

    // all characters
    private val allCharacters = MLStrokeValidator.ALL_CHARS

    //current character index
    private var currentCharacterIndex = -1


    private val _currentCharacter = MutableStateFlow<ThaiCharacter>(ThaiCharacter())
    val currentCharacter = _currentCharacter.asStateFlow()

    private val _practiceStep = MutableStateFlow(PracticeStep.INITIAL)
    val practiceStep: StateFlow<PracticeStep> = _practiceStep.asStateFlow()

    private val _guideAnimationProgress = Animatable(0f) // For one-shot guide animation
    val guideAnimationProgress: Float get() = _guideAnimationProgress.value


    private val _pathForCrossFade = MutableStateFlow<Path?>(null)
    val pathForCrossFade: StateFlow<Path?> = _pathForCrossFade.asStateFlow()

    val crossFadeAnimation = Animatable(0f)

    // To trigger clearing the DrawingCanvas from the ViewModel
    private val _clearCanvasSignal = MutableSharedFlow<Unit>(replay = 0)
    val clearCanvasSignal: SharedFlow<Unit> = _clearCanvasSignal.asSharedFlow()

    // NEW: Tracks if user interacted with current guide
    private val _userHasStartedTracing = MutableStateFlow(false)
    val userHasStartedTracing: StateFlow<Boolean> = _userHasStartedTracing.asStateFlow()

    private val _currentStrokeIndex = MutableStateFlow(0)
    val currentStrokeIndex: StateFlow<Int> = _currentStrokeIndex.asStateFlow()


    init {
        // Load sounds when the ViewModel is created
        soundManager.loadSoundsForCharacters(allCharacters)
        // Load the first character when ViewModel is created
        initialLoadAndPrepareCharacter()
    }

    // WHAT CHANGED: Logic now resets the stroke index and clears the user path list.
    // WHY: Ensures that every new character starts fresh from the first stroke.
    private fun setupForNewCharacter() {
        _pathForCrossFade.value = null
        _userHasStartedTracing.value = false
        _currentStrokeIndex.value = 0
        viewModelScope.launch {
            _guideAnimationProgress.snapTo(0f)
            crossFadeAnimation.snapTo(0f)
        }
        _practiceStep.value = PracticeStep.GUIDE_AND_TRACE
    }

    private fun loadNextCharacter() {
        currentCharacterIndex++
        // Loop back to the start if we've reached the end of the list
        if (currentCharacterIndex >= allCharacters.size) {
            currentCharacterIndex = 0
        }
        _currentCharacter.value = allCharacters[currentCharacterIndex]
    }

    private fun loadPreviousCharacter() {
        currentCharacterIndex--
        // Loop back to the start if we've reached the end of the list
        if (currentCharacterIndex >= allCharacters.size) {
            currentCharacterIndex = 0
        }
        _currentCharacter.value = allCharacters[currentCharacterIndex]
    }

    private fun initialLoadAndPrepareCharacter() {
        loadNextCharacter()
        _currentCharacter.value?.let {
            setupForNewCharacter()
        }
    }

    private fun loadNextCharacterAndPrepareAnimation() {
        loadNextCharacter()
        _currentCharacter.value?.let {
            setupForNewCharacter()
        }
    }

    private fun loadPreviousCharacterAndPrepareAnimation() {
        loadPreviousCharacter()
        _currentCharacter.value?.let {
            setupForNewCharacter()
        }
    }

    // WHAT CHANGED: The animation loop now depends on the currentStrokeIndex.
    // WHY: The guide animation should only run for the currently active stroke.
    suspend fun executeGuideAnimationLoop() {
        if (_practiceStep.value != PracticeStep.GUIDE_AND_TRACE || currentCharacter.value == null) {
            return
        }

        while (isActive &&
            _practiceStep.value == PracticeStep.GUIDE_AND_TRACE &&
            !_userHasStartedTracing.value &&
            currentCharacter.value != null
        ) {
            _guideAnimationProgress.snapTo(0f)
            try {
                _guideAnimationProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 1700,
                        easing = CubicBezierEasing(0.22f, 0f, 0.2f, 1f)
                    )
                )
                if (isActive && !_userHasStartedTracing.value && _practiceStep.value == PracticeStep.GUIDE_AND_TRACE) {
                    delay(500)
                }
            } catch (e: CancellationException) {
                break
            }
        }

        if (isActive && _userHasStartedTracing.value && _practiceStep.value == PracticeStep.GUIDE_AND_TRACE) {
            _guideAnimationProgress.snapTo(1f)
        }
    }

    // Called from onDragStart in DrawingCanvas
    fun userStartedTracing() {
        if (_practiceStep.value == PracticeStep.GUIDE_AND_TRACE) {
            _userHasStartedTracing.value = true
            // The animation loop in executeGuideAnimationLoop will see this flag and stop.
            // We might want to immediately snap the animation to its end if it was mid-animation.
            viewModelScope.launch {
                _guideAnimationProgress.stop() // Stop any ongoing animation
                _guideAnimationProgress.snapTo(1f) // Ensure the animated part is fully "drawn" if we want to show it statically
            }
        }
    }

    fun onUserStrokeFinished(path: Path) {
        viewModelScope.launch { _clearCanvasSignal.emit(Unit) }

        val char = _currentCharacter.value ?: return
        if (_currentStrokeIndex.value >= char.strokes.size) return

        val perfectSvgPath = char.strokes[_currentStrokeIndex.value]
        val perfectPath = PathParser().parsePathString(perfectSvgPath).toPath()

        if (pathsAreClose(user = path, perfect = perfectPath, threshold = 0.55f)) {
            _pathForCrossFade.value = path
            val targetStep = if (_practiceStep.value == PracticeStep.GUIDE_AND_TRACE) {
                PracticeStep.CROSS_FADING_TRACE
            } else {
                PracticeStep.CROSS_FADING_WRITE
            }
            _practiceStep.value = targetStep
        }
    }

    fun playCurrentCharacterSound() {
        _currentCharacter.value?.let {
            soundManager.playSoundForCharacter(it.id)
        }
    }


    private fun triggerCrossFadeAnimation() {
        viewModelScope.launch {
            crossFadeAnimation.snapTo(0f)
            crossFadeAnimation.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600) // A quick, smooth fade
            )
            onCrossFadeFinished()
        }
    }

    fun onCrossFadeFinished() {
        val char = _currentCharacter.value ?: return
        val step = _practiceStep.value
        _pathForCrossFade.value = null

        when (step) {
            PracticeStep.CROSS_FADING_TRACE -> {
                if (_currentStrokeIndex.value < char.strokes.size - 1) {
                    _currentStrokeIndex.value++
                    _userHasStartedTracing.value = false
                    _practiceStep.value = PracticeStep.GUIDE_AND_TRACE
                } else {
                    _practiceStep.value = PracticeStep.AWAITING_BLANK_SLATE
                }
            }

            PracticeStep.CROSS_FADING_WRITE -> {
                if (_currentStrokeIndex.value < char.strokes.size - 1) {
                    _currentStrokeIndex.value++
                    _practiceStep.value = PracticeStep.USER_WRITING_BLANK
                } else {
                    _practiceStep.value = PracticeStep.AWAITING_NEXT_CHARACTER
                }
            }

            else -> {}
        }
    }

    fun advanceToNextStep() {
        viewModelScope.launch {
            when (_practiceStep.value) {
                PracticeStep.AWAITING_BLANK_SLATE -> {
                    _pathForCrossFade.value = null
                    _currentStrokeIndex.value = 0
                    _userHasStartedTracing.value = false
                    _clearCanvasSignal.emit(Unit)
                    _practiceStep.value = PracticeStep.USER_WRITING_BLANK
                }

                PracticeStep.AWAITING_NEXT_CHARACTER -> {
                    loadNextCharacterAndPrepareAnimation()
                }

                else -> {}
            }
        }
    }

    fun manualClear() {
        viewModelScope.launch {
            _pathForCrossFade.value = null
            _clearCanvasSignal.emit(Unit)
            setupForNewCharacter()
        }
    }

    // When "Next Char" button is pressed
    fun nextCharacter() {
        viewModelScope.launch {
            loadNextCharacterAndPrepareAnimation()
        }
    }

    fun previousCharacter() {
        viewModelScope.launch {
            loadPreviousCharacterAndPrepareAnimation()
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }

}



