package com.rjnr.thaiwrter.ui.screens.free_drawing


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rjnr.thaiwrter.ui.drawing.DrawingCanvas
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun FreewritingScreen(
    viewModel: FreewritingViewModel = koinViewModel()
) {
    val prediction by viewModel.prediction.collectAsState()
    val clearCanvasSignal = remember { MutableSharedFlow<Unit>() }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Free Writing Mode", style = MaterialTheme.typography.headlineSmall)
        Text("Draw any character to get a prediction", style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    RoundedCornerShape(8.dp)
                )
        ) {
            DrawingCanvas(
                modifier = Modifier.fillMaxSize(),
                clearSignal = clearCanvasSignal,
                onStrokeFinished = { points ->
                    // The canvas doesn't give its size directly in this lambda,
                    // but we can access it from the Box's scope. This is a limitation
                    // we can refine later, but for now, this works.
                    // Let's modify DrawingCanvas one more time to pass the size.
                },
                onDragStartAction = { viewModel.clear() },
                enabled = true
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            viewModel.clear()
            scope.launch { clearCanvasSignal.emit(Unit) }
        }) {
            Text("Clear")
        }

        Spacer(Modifier.height(16.dp))

        // Placeholder for prediction results
        prediction?.let {
            Text("Prediction: ${it.character} (${it.pronunciation})")
            Text("Confidence: ${(it.confidence * 100).toInt()}%")
        }
    }
}