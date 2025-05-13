package com.rjnr.thaiwrter.ui.screens

import MorphOverlay
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rjnr.thaiwrter.ui.drawing.DrawingCanvas
import com.rjnr.thaiwrter.ui.drawing.StrokeGuide
import com.rjnr.thaiwrter.ui.drawing.isCloseEnough
import com.rjnr.thaiwrter.ui.drawing.pathsAreClose
import com.rjnr.thaiwrter.ui.drawing.testStroke
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

    // System metrics for proper scaling
    val metrics = LocalDensity.current.density
    val strokePath = remember(testStroke.pathData) {
        androidx.compose.ui.graphics.vector.PathParser()    // UI-graphics 1.7+
            .parsePathString(testStroke.pathData)
            .toPath()
    }
    val perfectStroke =
        "M14 127C11.6 38.2 7 55 28 33L1 15C1 15 26.9941 0.0325775 45 1C60.4269 1.82886 82 5.00001 82 15C82 25 82 127 82 127"

    val perfectPath = remember(perfectStroke) {
        PathParser()
            .parsePathString(perfectStroke)
            .toPath()
    }
    /* --- state --- */
    var showGuide by remember { mutableStateOf(true) }
    var showMorph by remember { mutableStateOf(false) }
    var userPathForMorph by remember { mutableStateOf<Path?>(null) }


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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .padding(16.dp)
                    ) {
                        // Main drawing canvas
                        if (showGuide) {
                            StrokeGuide(
                                svgPathData = perfectStroke,
                                marginRatio = 0.15f,            // shrink the guide
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        DrawingCanvas(
                            modifier = Modifier.fillMaxSize(),
                            shouldClear = shouldClearCanvas,
                            onStrokeFinished = { userPath ->
                                if (pathsAreClose(userPath, perfectPath)) {
                                    userPathForMorph = userPath
                                    showGuide = false            // ① hide purple loop
                                    showMorph = true
                                }
                            }
                        )
                        // ③ Morph overlay – only when showMorph == true
                        if (showMorph && userPathForMorph != null) {
                            MorphOverlay(
                                userPath = userPathForMorph!!,
                                perfectSvg = perfectStroke,
                                onFinished = {
                                    showMorph = false            // reset
                                    viewModel::clearCanvas
                                    showGuide = true             // ready for next try
                                },
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
        }
    }
}

