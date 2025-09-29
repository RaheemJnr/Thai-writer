package com.rjnr.thaiwrter.ui.screens.free_drawing


import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rjnr.thaiwrter.data.models.Point
import com.rjnr.thaiwrter.utils.CharacterPrediction
import com.rjnr.thaiwrter.utils.MLStrokeValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FreewritingViewModel(
    private val mlStrokeValidator: MLStrokeValidator
) : ViewModel() {

    private val _prediction = MutableStateFlow<CharacterPrediction?>(null)
    val prediction = _prediction.asStateFlow()

    fun onStrokeFinished(points: List<Offset>, canvasSize: Size) {
        if (points.isEmpty() || canvasSize == Size.Zero) return

        // Convert Compose Offsets to the Point data class the model expects
        val modelPoints = points.map { Point(it.x, it.y) }

        viewModelScope.launch {
            _prediction.value = mlStrokeValidator.predictCharacter(
                modelPoints,
                canvasSize.width,
                canvasSize.height
            )
        }
    }

    fun clear() {
        _prediction.value = null
    }
}