package com.rjnr.thaiwrter.ui.screens

import MorphOverlay
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rjnr.thaiwrter.ui.drawing.DrawingConfig
import com.rjnr.thaiwrter.ui.drawing.OptimizedDrawingCanvas
import com.rjnr.thaiwrter.ui.drawing.StrokeGuide
import com.rjnr.thaiwrter.ui.viewmodel.CharacterPracticeViewModel
import com.rjnr.thaiwrter.ui.viewmodel.PracticeStep
import org.koin.androidx.compose.koinViewModel

@Composable
fun CharacterPracticeScreen(
    viewModel: CharacterPracticeViewModel = koinViewModel(), // or viewModel()
    navController: NavController // If used for back navigation
) {
    val currentCharacter by viewModel.currentCharacter.collectAsState()
    val practiceStep by viewModel.practiceStep.collectAsState()
    val guideAnimationProgress = viewModel.guideAnimationProgress // Direct float
    val userHasStartedTracing by viewModel.userHasStartedTracing.collectAsState() // Collect new state
//    val userDrawnPath by viewModel.userDrawnPath.collectAsState()
    val userDrawnPathForMorph by viewModel.userDrawnPathForMorph.collectAsState()
    val currentStrokeIndex by viewModel.currentStrokeIndex.collectAsState()

    // For the "tap to advance" functionality
    val interactionSource = remember { MutableInteractionSource() }
    // Enabled only when user is supposed to draw and not during morphing/guide animation
    val drawingEnabled = practiceStep == PracticeStep.GUIDE_AND_TRACE ||
            practiceStep == PracticeStep.USER_WRITING_BLANK

    // Trigger the guide animation loop
    LaunchedEffect(practiceStep, currentCharacter, userHasStartedTracing) {
        // Add userHasStartedTracing key
        if (practiceStep == PracticeStep.GUIDE_AND_TRACE && currentCharacter != null && !userHasStartedTracing) {
            viewModel.executeGuideAnimationLoop()
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background) // Use theme colors
                .clickable( // Global click for "tap to advance"
                    interactionSource = interactionSource,
                    indication = null, // No ripple for the whole screen
                    enabled = practiceStep == PracticeStep.AWAITING_BLANK_SLATE ||
                            practiceStep == PracticeStep.AWAITING_NEXT_CHARACTER
                ) {
                    viewModel.advanceToNextStep()
                }
        ) {
            // Top Bar (Character Info, Progress - Placeholder)
            currentCharacter?.let { char ->
                Text(
                    text = char.character,
                    style = MaterialTheme.typography.displayLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Text(
                    text = char.pronunciation,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            Spacer(Modifier.height(8.dp)) // Reduced spacer

            // Drawing Area
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(1f)
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 8.dp) // Reduced padding
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(8.dp)
                    ) // Theme color
            ) {
                // Grid lines
                Canvas(Modifier.fillMaxSize()) {
                    val thirdHeight = size.height / 3
                    val thirdWidth = size.width / 3
                    drawLine(
                        Color.Gray,
                        Offset(0f, thirdHeight),
                        Offset(size.width, thirdHeight),
                        alpha = 0.5f
                    )
                    drawLine(
                        Color.Gray,
                        Offset(0f, 2 * thirdHeight),
                        Offset(size.width, 2 * thirdHeight),
                        alpha = 0.5f
                    )
                    drawLine(
                        Color.Gray,
                        Offset(thirdWidth, 0f),
                        Offset(thirdWidth, size.height),
                        alpha = 0.5f
                    )
                    drawLine(
                        Color.Gray,
                        Offset(2 * thirdWidth, 0f),
                        Offset(2 * thirdWidth, size.height),
                        alpha = 0.5f
                    )
                    drawLine(
                        Color.Gray,
                        Offset(0f, size.height / 2),
                        Offset(size.width, size.height / 2),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)),
                        alpha = 0.5f
                    )
                }

//                if (practiceStep == PracticeStep.GUIDE_AND_TRACE && currentCharacter != null) {
//                    currentCharacter?.let { char ->
////                        StrokeGuide(
////                            strokes = char.strokes,
////                            animationProgress = guideAnimationProgress,
////                            userHasStartedTracing = userHasStartedTracing,
////                            marginToApply = 0.2f,
////                            modifier = Modifier.fillMaxSize()
////                        )
//                        StrokeGuide(
//                            strokes = char.strokes,
//                            animationProgress = guideAnimationProgress,
//                            userHasStartedTracing = userHasStartedTracing,
//                            currentStrokeIndex = currentStrokeIndex,
//                            completedStrokes = userDrawnPaths, // Pass completed user strokes
//                            marginToApply = 0.2f,
//                            modifier = Modifier.fillMaxSize()
//                        )
//                    }
//                }
                if (practiceStep == PracticeStep.GUIDE_AND_TRACE && currentCharacter != null) {
                    currentCharacter?.let { char ->
                        StrokeGuide(
                            strokes = char.strokes,
                            animationProgress = guideAnimationProgress,
                            userHasStartedTracing = userHasStartedTracing,
                            currentStrokeIndex = currentStrokeIndex,
                            marginToApply = 0.2f,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                OptimizedDrawingCanvas(
                    modifier = Modifier.fillMaxSize(),
                    clearSignal = viewModel.clearCanvasSignal,
                    onStrokeFinished = viewModel::onUserStrokeFinished,
                    onDragStartAction = viewModel::userStartedTracing,
                    enabled = drawingEnabled,
                    strokeColor = Color.Black,
                    strokeWidthRatio = DrawingConfig.DEFAULT_STROKE_WIDTH_RATIO
                )

                val lastUserPath = userDrawnPathForMorph
                if ((practiceStep == PracticeStep.MORPHING_TRACE_TO_CORRECT || practiceStep == PracticeStep.MORPHING_WRITE_TO_CORRECT) && lastUserPath != null) {
                    MorphOverlay(
                        userPath = lastUserPath,
                        perfectStrokes = listOf(
                            currentCharacter?.strokes?.getOrNull(
                                currentStrokeIndex
                            ) ?: ""
                        ),
                        onFinished = viewModel::onMorphAnimationFinished,
                        marginToApply = 0.2f,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if ((practiceStep == PracticeStep.AWAITING_BLANK_SLATE || practiceStep == PracticeStep.AWAITING_NEXT_CHARACTER) && currentCharacter?.strokes != null) {
                    currentCharacter?.let { char ->
                        StrokeGuide(
                            strokes = char.strokes,
                            animationProgress = 1f,
                            userHasStartedTracing = true,
                            currentStrokeIndex = char.strokes.size,
                            staticGuideColor = Color.Green.copy(alpha = 0.0f),
                            animatedSegmentColor = Color.Green,
                            finalStaticSegmentColor = Color.Green,
                            marginToApply = 0.2f,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

//            // Instruction Text
//            Text(
//                text = when (practiceStep) {
//                    PracticeStep.INITIAL -> "Loading..."
//                    PracticeStep.GUIDE_AND_TRACE -> if (!userHasStartedTracing) "Trace the character" else "Finish tracing"
//                    PracticeStep.MORPHING_TRACE_TO_CORRECT, PracticeStep.MORPHING_WRITE_TO_CORRECT -> ""
//                    PracticeStep.AWAITING_BLANK_SLATE -> "Tap to try from memory"
//                    PracticeStep.USER_WRITING_BLANK -> "Write the character"
//                    PracticeStep.AWAITING_NEXT_CHARACTER -> "Tap for next"
//                },
//                style = MaterialTheme.typography.titleMedium,
//                color = Color.White, // White text for blue background
//                modifier = Modifier
//                    .align(Alignment.CenterHorizontally)
//                    .padding(top = 24.dp) // More space for instruction
//                    .clickable( // Make "tap to advance" apply here too
//                        interactionSource = interactionSource,
//                        indication = null,
//                        enabled = practiceStep == PracticeStep.AWAITING_BLANK_SLATE ||
//                                practiceStep == PracticeStep.AWAITING_NEXT_CHARACTER
//                    ) {
//                        viewModel.advanceToNextStep()
//                    }
//            )
            // WHAT CHANGED: Instruction text is updated for the new flow.
            // WHY: To provide clear, context-sensitive instructions to the user at each step.
            Text(
                text = when (practiceStep) {
                    PracticeStep.INITIAL -> "Loading..."
                    PracticeStep.GUIDE_AND_TRACE -> if (!userHasStartedTracing) "Trace stroke ${currentStrokeIndex + 1}" else "Finish tracing"
                    PracticeStep.MORPHING_TRACE_TO_CORRECT, PracticeStep.MORPHING_WRITE_TO_CORRECT -> "Perfect!"
                    PracticeStep.AWAITING_BLANK_SLATE -> "Great! Tap to try from memory."
                    PracticeStep.USER_WRITING_BLANK -> "Write stroke ${currentStrokeIndex + 1} from memory"
                    PracticeStep.AWAITING_NEXT_CHARACTER -> "Excellent! Tap for the next character."
                },
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 24.dp)
            )

            Spacer(Modifier.weight(1f))

            // ML Prediction display (your existing code, can be kept)
            // viewModel.prediction.collectAsState().value?.let { pred -> ... }


            // Bottom Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = viewModel::manualClear, enabled = drawingEnabled) { Text("Clear") }
                // "Check" button might be redundant if morphing happens automatically
                // Button(onClick = viewModel::checkAnswer) { Text("Check") }
                Button(onClick = viewModel::requestNextCharacter) { Text("Next Char") } // Or "Skip"
            }

            // Icons (as in video) - Placeholder for functionality
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Practice Mode",
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Pronunciation",
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    Icons.Default.FavoriteBorder,
                    contentDescription = "Toggle Guide (if applicable)",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Practice Complete Dialog (from video) - This would be triggered from ViewModel
        // when practiceStep becomes, e.g., PracticeStep.LESSON_COMPLETE
        if (practiceStep == PracticeStep.AWAITING_NEXT_CHARACTER /* && isLastCharacterInLesson */) {
            // Show your "Practice Complete" dialog here with stars, Retry, Continue
            // For simplicity, this is handled by "Tap for next" now.
            // You could navigate to a summary screen or show an AlertDialog.
        }
    }
}
