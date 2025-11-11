package com.rjnr.thaiwrter.ui.screens.free_drawing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rjnr.thaiwrter.data.models.Point
import com.rjnr.thaiwrter.utils.CharacterPrediction
import com.rjnr.thaiwrter.utils.MLStrokeValidator
import com.rjnr.thaiwrter.utils.TelemetryLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FreewritingViewModel(
        private val mlStrokeValidator: MLStrokeValidator,
        private val telemetry: TelemetryLogger
) : ViewModel() {

    private val _prediction = MutableStateFlow<CharacterPrediction?>(null)
    val prediction = _prediction.asStateFlow()

    private val _recentPredictions = MutableStateFlow<List<CharacterPrediction>>(emptyList())
    val recentPredictions = _recentPredictions.asStateFlow()

    fun onStrokeFinished(points: List<Offset>, canvasSize: Size) {
        if (points.isEmpty() || canvasSize == Size.Zero) return

        // Convert Compose Offsets to the Point data class the model expects
        val modelPoints = points.map { Point(it.x, it.y) }

        viewModelScope.launch {
            telemetry.logEvent(
                    "freewriting_prediction",
                    mapOf("points" to points.size, "canvasWidth" to canvasSize.width)
            )
            val result =
                    mlStrokeValidator.predictCharacter(
                            modelPoints,
                            canvasSize.width,
                            canvasSize.height
                    )
            _prediction.value = result
            if (result == null) {
                telemetry.logEvent("freewriting_prediction_null")
            } else {
                telemetry.logEvent(
                        "freewriting_prediction_result",
                        mapOf("character" to result.character, "confidence" to result.confidence)
                )
                storeRecentPrediction(result)
            }
        }
    }

    fun clear() {
        _prediction.value = null
        telemetry.logEvent("freewriting_clear")
    }

    private fun storeRecentPrediction(prediction: CharacterPrediction) {
        val updated =
                (listOf(prediction) + _recentPredictions.value).distinctBy { it.character }.take(5)
        _recentPredictions.value = updated
    }
}
