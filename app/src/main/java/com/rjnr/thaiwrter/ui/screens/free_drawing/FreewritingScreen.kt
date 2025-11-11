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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rjnr.thaiwrter.ui.drawing.OptimizedDrawingCanvas
import com.rjnr.thaiwrter.utils.CharacterPrediction
import io.eyram.iconsax.IconSax
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreewritingScreen(viewModel: FreewritingViewModel = koinViewModel()) {
    val prediction by viewModel.prediction.collectAsState()
    val recentPredictions by viewModel.recentPredictions.collectAsState()
    val clearCanvasSignal = remember { MutableSharedFlow<Unit>() }
    val scope = rememberCoroutineScope()
    var brush by remember { mutableStateOf(BrushWeight.MEDIUM) }

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
                            strokeWidthRatio = brush.ratio,
                            onStrokePointsCaptured = { points, size ->
                                viewModel.onStrokeFinished(points, size)
                            }
                    )
                }
            }

            BrushSelector(brush = brush, onBrushSelected = { brush = it })

            Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                        onClick = {
                            viewModel.clear()
                            scope.launch { clearCanvasSignal.emit(Unit) }
                        },
                        modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Rounded.Refresh, contentDescription = "Clear canvas")
                    Text("Clear", modifier = Modifier.padding(start = 6.dp))
                }
                TextButton(onClick = { /* future: save sketch */}, modifier = Modifier.weight(1f)) {
                    Text("Save to journal")
                }
            }

            PredictionPanel(prediction)

            if (recentPredictions.isNotEmpty()) {
                RecentPredictions(recentPredictions)
            }
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
                    Text("Suggestions", fontWeight = FontWeight.SemiBold)
                    FlowChips(prediction.alternativeCharacters.take(3))
                }
            }
        }
    }
}

@Composable
private fun FlowChips(options: List<Pair<String, Float>>) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { (char, confidence) ->
            AssistChip(
                    onClick = {},
                    label = { Text("$char ${(confidence * 100).toInt()}%") },
                    colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFFE7F1FF))
            )
        }
    }
}

@Composable
private fun RecentPredictions(predictions: List<CharacterPrediction>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Recent", style = MaterialTheme.typography.titleMedium)
        predictions.take(4).forEach { prediction ->
            Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(Color.White)) {
                Row(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(prediction.character, style = MaterialTheme.typography.titleLarge)
                        Text(prediction.pronunciation, color = Color.Gray)
                    }
                    Text("${(prediction.confidence * 100).toInt()}%")
                }
            }
        }
    }
}

@Composable
private fun BrushSelector(brush: BrushWeight, onBrushSelected: (BrushWeight) -> Unit) {
    Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
    ) {
        BrushWeight.entries.forEach { option ->
            val selected = option == brush
            Button(
                    onClick = { onBrushSelected(option) },
                    shape = CircleShape,
                    colors =
                            if (selected)
                                    ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                            else
                                    ButtonDefaults.buttonColors(
                                            MaterialTheme.colorScheme.surfaceVariant
                                    )
            ) {
                Icon(painterResource(IconSax.Linear.Brush2), contentDescription = option.label)
                Text(option.label, modifier = Modifier.padding(start = 6.dp))
            }
        }
    }
}

private enum class BrushWeight(val label: String, val ratio: Float) {
    THIN("Fine", 0.18f),
    MEDIUM("Medium", 0.26f),
    BOLD("Bold", 0.36f)
}
