package com.rjnr.thaiwrter.ui.screens

import CharacterGuideOverlay
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rjnr.thaiwrter.ui.drawing.DrawingCanvas
import com.rjnr.thaiwrter.ui.viewmodel.CharacterPracticeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CharacterPracticeScreen(
    viewModel: CharacterPracticeViewModel = koinViewModel(),
    navController: NavController
) {
    //
    val prediction by viewModel.prediction.collectAsState()
    val currentCharacter by viewModel.currentCharacter.collectAsState()
    val shouldClearCanvas by viewModel.shouldClearCanvas.collectAsState()
    val pathColor by viewModel.pathColor.collectAsState()
    val isCorrect by viewModel.isCorrect.collectAsState()
    val instructionText by viewModel.instructionText.collectAsState()
    val showGuide by viewModel.showGuide.collectAsState()

    // System metrics for proper scaling
    val metrics = LocalDensity.current.density


    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background)

        ) {
            // Character display
            Text(
                text = currentCharacter?.character ?: "",
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Pronunciation guide
            Text(
                text = currentCharacter?.pronunciation ?: "",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Drawing canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(vertical = 16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Box {
                        DrawingCanvas(
                            modifier = Modifier.fillMaxSize(),
                            shouldClear = shouldClearCanvas,
                            onDrawingComplete = { points, width, height ->
                                viewModel.onDrawingComplete(points, width, height)
                            }
                        )

                        // Character guide overlay
                        if (showGuide) {
                            CharacterGuideOverlay(
                                character = currentCharacter?.character ?: "",
                                isVisible = true,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            // Prediction results
            prediction?.let { pred ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Prediction: ${pred.character}",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "Confidence: ${(pred.confidence * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        // Pronunciation
                        Text(
                            text = pred.pronunciation,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        // Show top alternative predictions
//                        pred.alternativePredictions.take(3).forEach { (index, confidence) ->
//                            Text(
//                                text = "Alternative: ${index} (${(confidence * 100).toInt()}%)",
//                                style = MaterialTheme.typography.bodyMedium
//                            )
//                        }
                        if (pred.alternativeCharacters.isNotEmpty()) {
                            Text(
                                text = "Alternative predictions:",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            pred.alternativeCharacters.take(3).forEach { (char, conf) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(char, style = MaterialTheme.typography.bodyLarge)
                                    Text(
                                        "${(conf * 100).toInt()}%",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = viewModel::clearCanvas,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C6BC0))
                ) {
                    Text("Clear", color = Color.White)
                }

                Button(
                    onClick = viewModel::checkAnswer,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C6BC0))
                ) {
                    Text("Check", color = Color.White)
                }

                Button(
                    onClick = viewModel::nextCharacter,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C6BC0))
                ) {
                    Text("Next", color = Color.White)
                }
            }
        }
    }
}