package com.rjnr.thaiwrter.ui.screens.free_drawing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rjnr.thaiwrter.ui.drawing.OptimizedDrawingCanvas
import com.rjnr.thaiwrter.utils.CharacterPrediction
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun FreewritingScreen(viewModel: FreewritingViewModel = koinViewModel()) {
    val prediction by viewModel.prediction.collectAsState()
    val clearCanvasSignal = remember { MutableSharedFlow<Unit>() }
    val scope = rememberCoroutineScope()

    Scaffold(topBar = { TopAppBar(title = { Text("Freewriting Studio") }) }) { padding ->
        Column(
                modifier =
                        Modifier.fillMaxSize()
                                .background(Color(0xFFF4F4F8))
                                .padding(padding)
                                .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                    "Sketch freely and let the model guess the character. Slow down and watch your strokes come to life.",
                    style = MaterialTheme.typography.bodyLarge
            )

            Card(
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(
                        modifier =
                                Modifier.fillMaxSize()
                                        .padding(16.dp)
                                        .background(
                                                MaterialTheme.colorScheme.surfaceVariant,
                                                RoundedCornerShape(24.dp)
                                        )
                ) {
                    OptimizedDrawingCanvas(
                            modifier = Modifier.fillMaxSize(),
                            clearSignal = clearCanvasSignal,
                            onStrokeFinished = {},
                            onDragStartAction = { viewModel.clear() },
                            enabled = true,
                            onStrokePointsCaptured = { points, size ->
                                viewModel.onStrokeFinished(points, size)
                            }
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                        onClick = {
                            viewModel.clear()
                            scope.launch { clearCanvasSignal.emit(Unit) }
                        },
                        modifier = Modifier.weight(1f)
                ) { Text("Clear Canvas") }
                TextButton(onClick = { /* future: save sketch */}, modifier = Modifier.weight(1f)) {
                    Text("Save to journal")
                }
            }

            PredictionPanel(prediction)
        }
    }
}

@Composable
private fun PredictionPanel(prediction: CharacterPrediction?) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp)
    ) {
        Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Live prediction", style = MaterialTheme.typography.titleMedium)
            if (prediction == null) {
                Text("Draw a character to see predictions.", color = Color.Gray)
            } else {
                Text(
                        "${prediction.character} · ${prediction.pronunciation}",
                        style = MaterialTheme.typography.headlineSmall
                )
                Text("Confidence ${(prediction.confidence * 100).toInt()}%")

                if (prediction.alternativeCharacters.isNotEmpty()) {
                    Text("Alternatives", fontWeight = FontWeight.SemiBold)
                    prediction.alternativeCharacters.take(3).forEach { (char, conf) ->
                        Text("$char · ${(conf * 100).toInt()}%")
                    }
                }
            }
        }
    }
}
