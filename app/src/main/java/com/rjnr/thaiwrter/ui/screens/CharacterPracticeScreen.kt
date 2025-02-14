package com.rjnr.thaiwrter.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rjnr.thaiwrter.ui.drawing.DrawingCanvas
import com.rjnr.thaiwrter.ui.viewmodel.CharacterPracticeViewModel
import com.rjnr.thaiwrter.ui.viewmodel.StrokeFeedback
import org.koin.androidx.compose.koinViewModel

@Composable
fun CharacterPracticeScreen(
    viewModel: CharacterPracticeViewModel = koinViewModel(),
    navController: NavController
) {
    //Log.d("CharacterPracticeScreen", "Current character: ${currentCharacter?.character}")
//    val currentCharacter by viewModel.currentCharacter.collectAsState()
//    val paths by viewModel.paths.collectAsState()
//    val strokeFeedback by viewModel.strokeFeedback.collectAsState()
//    val currentStrokeIndex by viewModel.currentStrokeIndex.collectAsState()
//    val mlPrediction by viewModel.mlPrediction.collectAsState()
//    val confidence by viewModel.confidence.collectAsState()
    val prediction by viewModel.prediction.collectAsState()
    val currentCharacter by viewModel.currentCharacter.collectAsState()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background)

        ) {
//            // Character display
//            Text(
//                text = currentCharacter?.character ?: "",
//                style = MaterialTheme.typography.displayLarge,
//                modifier = Modifier.align(Alignment.CenterHorizontally)
//            )
//
//            // Pronunciation guide
//            Text(
//                text = currentCharacter?.pronunciation ?: "",
//                style = MaterialTheme.typography.bodyLarge,
//                modifier = Modifier.align(Alignment.CenterHorizontally)
//            )
//            // Add this after the pronunciation guide
//            Text(
//                text = "Start at green circle → Follow arrow → End at red circle",
//                style = MaterialTheme.typography.bodyMedium,
//                color = MaterialTheme.colorScheme.primary,
//                modifier = Modifier
//                    .align(Alignment.CenterHorizontally)
//                    .padding(bottom = 8.dp)
//            )
//
//            Text(
//                text = "Current stroke: ${(currentStrokeIndex + 1)}",
//                style = MaterialTheme.typography.bodyMedium,
//                color = MaterialTheme.colorScheme.secondary,
//                modifier = Modifier.align(Alignment.CenterHorizontally)
//            )
//            // Add ML Prediction display when available
//            mlPrediction?.let { prediction ->
//                Card(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 8.dp),
//                    colors = CardDefaults.cardColors(
//                        containerColor = MaterialTheme.colorScheme.surfaceVariant
//                    )
//                ) {
//                    Column(
//                        modifier = Modifier
//                            .padding(16.dp)
//                    ) {
//                        Text(
//                            text = "ML Prediction Results",
//                            style = MaterialTheme.typography.titleMedium,
//                            color = MaterialTheme.colorScheme.onSurfaceVariant
//                        )
//
//                        // Show confidence score
//                        confidence?.let { score ->
//                            Text(
//                                text = "Confidence: ${(score * 100).toInt()}%",
//                                style = MaterialTheme.typography.bodyMedium,
//                                color = MaterialTheme.colorScheme.onSurfaceVariant
//                            )
//                        }
//
//                        // Show alternative predictions
//                        prediction.alternativePredictions.take(3).forEach { (index, conf) ->
//                            Text(
//                                text = "Alternative ${index + 1}: ${(conf * 100).toInt()}%",
//                                style = MaterialTheme.typography.bodySmall,
//                                color = MaterialTheme.colorScheme.onSurfaceVariant
//                            )
//                        }
//                    }
//                }
//            }
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
//                DrawingCanvas(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .border(2.dp, Color.Gray, RoundedCornerShape(8.dp))
//                        .clip(RoundedCornerShape(8.dp)),
//                    currentCharacter = currentCharacter,
//                    currentStrokeIndex = currentStrokeIndex,
//                    paths = paths,
//                    onStrokeFinished = viewModel::validateStroke
//                )
                DrawingCanvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(2.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp)),
                    onDrawingComplete = { points,width,height ->
                        viewModel.onDrawingComplete(points, width, height)
                    }
                )
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
                            text = "Prediction: ${pred.characterIndex}",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "Confidence: ${(pred.confidence * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        // Show top alternative predictions
                        pred.alternativePredictions.take(3).forEach { (index, confidence) ->
                            Text(
                                text = "Alternative: ${index} (${(confidence * 100).toInt()}%)",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = viewModel::clearCanvas) {
                    Text("Clear")
                }
                Button(onClick = viewModel::checkAnswer) {
                    Text("Check")
                }
                Button(onClick = viewModel::nextCharacter) {
                    Text("Next")
                }
            }

            // Controls
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 16.dp),
//                horizontalArrangement = Arrangement.SpaceEvenly
//            ) {
//                Button(onClick = viewModel::clearCanvas) {
//                    Text("Clear")
//                }
//                Button(onClick = viewModel::checkAnswer) {
//                    Text("Check")
//                }
//                Button(onClick = viewModel::skipCharacter) {
//                    Text("Skip")
//                }
//            }
//
//            // Feedback text
//            AnimatedVisibility(
//                visible = strokeFeedback != null,
//                enter = fadeIn(),
//                exit = fadeOut()
//            ) {
//                Text(
//                    text = when (strokeFeedback) {
//                        StrokeFeedback.Correct -> "Correct!"
//                        StrokeFeedback.Incorrect -> "Try again"
//                        null -> ""
//                    },
//                    color = when (strokeFeedback) {
//                        StrokeFeedback.Correct -> Color.Green
//                        StrokeFeedback.Incorrect -> Color.Red
//                        null -> Color.Black
//                    },
//                    style = MaterialTheme.typography.headlineMedium,
//                    modifier = Modifier
//                        .align(Alignment.CenterHorizontally)
//                        .padding(vertical = 8.dp)
//                )
//            }
        }
    }
}