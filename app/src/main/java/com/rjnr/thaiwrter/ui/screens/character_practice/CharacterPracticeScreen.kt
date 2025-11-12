package com.rjnr.thaiwrter.ui.screens.character_practice

import android.graphics.RectF
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rjnr.thaiwrter.data.models.ThaiCharacter
import com.rjnr.thaiwrter.ui.drawing.DrawingConfig
import com.rjnr.thaiwrter.ui.drawing.OptimizedDrawingCanvas
import com.rjnr.thaiwrter.ui.drawing.StrokeGuide
import com.rjnr.thaiwrter.ui.screens.conponents.StageProgressIndicator
import com.rjnr.thaiwrter.utils.MLStrokeValidator
import io.eyram.iconsax.IconSax
import kotlin.math.min
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import org.koin.androidx.compose.koinViewModel

@Composable
fun CharacterPracticeScreen(
        viewModel: CharacterPracticeViewModel = koinViewModel(), // or viewModel()
        navController: NavController // If used for back navigation
) {
    val currentCharacter by viewModel.currentCharacter.collectAsState()
    val practiceStep by viewModel.practiceStep.collectAsState()
    val guideAnimationProgress = viewModel.guideAnimationProgress // Direct float
    val userHasStartedTracing by
            viewModel.userHasStartedTracing.collectAsState() // Collect new state
    //    val userDrawnPath by viewModel.userDrawnPath.collectAsState()
    val pathForCrossFade by viewModel.pathForCrossFade.collectAsState()
    val currentStrokeIndex by viewModel.currentStrokeIndex.collectAsState()
    val crossFadeProgress by viewModel.crossFadeAnimation.asState()

    // For the "tap to advance" functionality
    val interactionSource = remember { MutableInteractionSource() }
    // Enabled only when user is supposed to draw and not during morphing/guide animation
    val drawingEnabled =
            practiceStep == PracticeStep.GUIDE_AND_TRACE ||
                    practiceStep == PracticeStep.USER_WRITING_BLANK

    // Trigger the guide animation loop
    LaunchedEffect(practiceStep, currentCharacter, userHasStartedTracing) {
        // Add userHasStartedTracing key
        if (practiceStep == PracticeStep.GUIDE_AND_TRACE && !userHasStartedTracing) {
            viewModel.executeGuideAnimationLoop()
        }
    }

    LaunchedEffect(practiceStep) {
        val isCrossFading =
                practiceStep == PracticeStep.CROSS_FADING_TRACE ||
                        practiceStep == PracticeStep.CROSS_FADING_WRITE
        if (isCrossFading) {
            viewModel.crossFadeAnimation.snapTo(0f)
            viewModel.crossFadeAnimation.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 600)
            )
            viewModel.onCrossFadeFinished()
        }
    }
    //
    val rightRevealProgress = remember { Animatable(0f) }

    LaunchedEffect(practiceStep, currentCharacter) {
        if (practiceStep == PracticeStep.AWAITING_BLANK_SLATE ||
                        practiceStep == PracticeStep.AWAITING_NEXT_CHARACTER
        ) {
            rightRevealProgress.snapTo(0f)
            rightRevealProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
            )
        } else {
            // Ensure we don't accidentally draw any of the green segment in other steps
            rightRevealProgress.snapTo(0f)
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        ContentUI(
                innerPadding,
                interactionSource,
                practiceStep,
                currentCharacter,
                guideAnimationProgress,
                userHasStartedTracing,
                currentStrokeIndex,
                drawingEnabled,
                pathForCrossFade,
                crossFadeProgress,
                rightRevealProgress,
                advanceToNextStep = { viewModel.advanceToNextStep() },
                onDragStartAction = { viewModel.userStartedTracing() },
                onStrokeFinished = { viewModel.onUserStrokeFinished(it) },
                clearSignal = viewModel.clearCanvasSignal,
                playCurrentCharacterSound = { viewModel.playCurrentCharacterSound() },
                manualClear = { viewModel.manualClear() },
                requestNextCharacter = { viewModel.nextCharacter() },
                previousCharacter = { viewModel.previousCharacter() }
        )
    }
}

@Composable
private fun ContentUI(
        innerPadding: PaddingValues,
        interactionSource: MutableInteractionSource,
        practiceStep: PracticeStep,
        currentCharacter: ThaiCharacter,
        guideAnimationProgress: Float,
        userHasStartedTracing: Boolean,
        currentStrokeIndex: Int,
        drawingEnabled: Boolean,
        pathForCrossFade: Path?,
        crossFadeProgress: Float,
        rightRevealProgress: Animatable<Float, AnimationVector1D>,
        advanceToNextStep: () -> Unit,
        onDragStartAction: () -> Unit,
        onStrokeFinished: (Path) -> Unit,
        clearSignal: SharedFlow<Unit>,
        playCurrentCharacterSound: () -> Unit,
        manualClear: () -> Unit,
        requestNextCharacter: () -> Unit,
        previousCharacter: () -> Unit
) {
    Column(
            modifier =
                    Modifier.fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp)
                            .background(MaterialTheme.colorScheme.background) // Use theme colors
                            .clickable( // Global click for "tap to advance"
                                    interactionSource = interactionSource,
                                    indication = null, // No ripple for the whole screen
                                    enabled =
                                            practiceStep == PracticeStep.AWAITING_BLANK_SLATE ||
                                                    practiceStep ==
                                                            PracticeStep.AWAITING_NEXT_CHARACTER
                            ) {
                            }
    ) {
        // Drawing Area
        Box(
                modifier =
                        Modifier.fillMaxWidth(0.9f)
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

            if (practiceStep == PracticeStep.GUIDE_AND_TRACE) {
                StrokeGuide(
                        strokes = currentCharacter.strokes,
                        animationProgress = guideAnimationProgress,
                        userHasStartedTracing = userHasStartedTracing,
                        currentStrokeIndex = currentStrokeIndex,
                        marginToApply = 0.2f,
                        modifier = Modifier.fillMaxSize()
                )
            }

            OptimizedDrawingCanvas(
                    modifier = Modifier.fillMaxSize(),
                    clearSignal = clearSignal,
                    onStrokeFinished = onStrokeFinished,
                    onDragStartAction = onDragStartAction,
                    enabled = drawingEnabled,
                    strokeColor = Color.Black,
                    strokeWidthRatio = DrawingConfig.DEFAULT_STROKE_WIDTH_RATIO
            )
            val isCrossFading =
                    practiceStep == PracticeStep.CROSS_FADING_TRACE ||
                            practiceStep == PracticeStep.CROSS_FADING_WRITE
            if (isCrossFading && pathForCrossFade != null) {
                val perfectStrokeSvg = currentCharacter.strokes[currentStrokeIndex]
                val perfectPath =
                        remember(perfectStrokeSvg) {
                            PathParser().parsePathString(perfectStrokeSvg).toPath()
                        }

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidthPx = DrawingConfig.getStrokeWidth(min(size.width, size.height))
                  //  Log.d("CharacterPracticeScreen", "Stroke width: $strokeWidthPx")
                    val strokeStyle =
                            Stroke(
                                    width = strokeWidthPx,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                            )

                    // Draw the user's path, fading out
                    drawPath(
                            path = pathForCrossFade,
                            color = Color.Black,
                            style = strokeStyle,
                            alpha = 1f - crossFadeProgress
                    )

                    // Draw the perfect path, fading in green
                    // We need to scale it correctly, just like in StrokeGuide
                    val rawBounds =
                            RectF().also { perfectPath.asAndroidPath().computeBounds(it, true) }
                    val marginToApply = 0.2f
                    val padX = size.width * marginToApply
                    val padY = size.height * marginToApply
                    val availW = size.width - padX * 2
                    val availH = size.height - padY * 2

                    if (rawBounds.width() > 0 && rawBounds.height() > 0) {
                        val finalScale =
                                min(availW / rawBounds.width(), availH / rawBounds.height()) * 0.9f
                        val finalDx =
                                padX + (availW - rawBounds.width() * finalScale) / 2f -
                                        (rawBounds.left * finalScale)
                        val finalDy =
                                padY + (availH - rawBounds.height() * finalScale) / 2f -
                                        (rawBounds.top * finalScale)

                        val matrix = Matrix()
                        matrix.scale(finalScale, finalScale)
                        matrix.translate(finalDx / finalScale, finalDy / finalScale)
                        perfectPath.transform(matrix)

                        drawPath(
                                path = perfectPath,
                                color = Color(0xFF13C296),
                                style = strokeStyle,
                                alpha = crossFadeProgress
                        )
                    }
                }
            } else if ((practiceStep == PracticeStep.AWAITING_BLANK_SLATE ||
                            practiceStep == PracticeStep.AWAITING_NEXT_CHARACTER) &&
                            currentCharacter?.strokes != null
            ) {
                currentCharacter.let { char ->
                    // One-shot "RIGHT" animation: write-on the correct character in green
                    StrokeGuide(
                            strokes = char.strokes,
                            animationProgress = rightRevealProgress.value, // 0→1 reveal
                            userHasStartedTracing = false, // don't snap to end; animate
                            staticGuideColor = Color.Transparent, // no faint base line
                            animatedSegmentColor = Color(0xFF13C296), // punchy "right" green
                            finalStaticSegmentColor =
                                    Color(0xFF13C296), // remains green when finished
                            marginToApply = 0.2f,
                            currentStrokeIndex = char.strokes.size,
                            modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
        StageProgressIndicator(
            practiceStep = practiceStep,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

//        Spacer(Modifier.height(12.dp))
//        Row(
//                modifier = Modifier.fillMaxWidth().padding(12.dp),
//                horizontalArrangement = Arrangement.SpaceEvenly
//        ) {
//            IconButton(
//                    onClick = manualClear,
//                    enabled = drawingEnabled,
//                    modifier = Modifier.size(40.dp)
//            ) {
//                Icon(
//                        painter = painterResource(IconSax.Linear.Eraser1),
//                        contentDescription = "Practice Mode",
//                        tint = MaterialTheme.colorScheme.onSurface
//                )
//            }
//        }
        Spacer(Modifier.height(12.dp))

        Text(
                text =
                        when (practiceStep) {
                            PracticeStep.INITIAL -> "Loading..."
                            PracticeStep.GUIDE_AND_TRACE ->
                                    if (!userHasStartedTracing) "Watch and then trace the character"
                                    else "Finish tracing"
                            PracticeStep.CROSS_FADING_TRACE, PracticeStep.CROSS_FADING_WRITE ->
                                    "Great!"
                            PracticeStep.AWAITING_BLANK_SLATE -> "Good job! Tap to try from memory."
                            PracticeStep.USER_WRITING_BLANK -> "Now, write the character."
                            PracticeStep.AWAITING_NEXT_CHARACTER ->
                                    "Excellent! Tap for the next character."
                        },
                style = MaterialTheme.typography.titleMedium,
                modifier =
                        Modifier.align(Alignment.CenterHorizontally).padding(top = 24.dp).clickable(
                                        interactionSource = interactionSource,
                                        indication = null,
                                        // enabled = practiceStep == PracticeStep.USER_WRITING_BLANK
                                        ) { advanceToNextStep() }
        )
        Spacer(Modifier.height(12.dp))

        CharacterHeroCard(
            currentCharacter = currentCharacter,
            practiceStep = practiceStep,
            onPlayAudio = playCurrentCharacterSound,
            onNext = requestNextCharacter,
            onPrevious = previousCharacter
        )

        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun CharacterHeroCard(
        currentCharacter: ThaiCharacter,
        practiceStep: PracticeStep,
        onPlayAudio: () -> Unit,
        onNext: () -> Unit,
        onPrevious: () -> Unit
) {
    val gradient =
            Brush.linearGradient(
                    listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
            )
    Column(
            modifier =
                    Modifier.fillMaxWidth().padding(12.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(brush = gradient)
                            .padding(20.dp)
    ) {
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                        text = currentCharacter.character,
                        style = MaterialTheme.typography.displayLarge,
                        color = Color.White
                )
                Text(
                        text = currentCharacter.pronunciation,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.8f)
                )
            }
            IconButton(onClick = onPlayAudio) {
                Icon(
                        painter = painterResource(IconSax.Linear.VolumeHigh),
                        contentDescription = "Audio",
                        tint = Color.White
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = practiceStep.name.replace("_", " "), color = Color.White.copy(alpha = 0.9f))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                IconButton(onClick = onPrevious, enabled = currentCharacter.id > 0) {
                    Icon(
                            painter = painterResource(IconSax.Linear.ArrowLeft1),
                            contentDescription = "Previous",
                            tint = Color.White
                    )
                }
                IconButton(onClick = onNext) {
                    Icon(
                            painter = painterResource(IconSax.Linear.ArrowRight1),
                            contentDescription = "Next",
                            tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun StrokeTimeline(totalStrokes: Int, currentStrokeIndex: Int, practiceStep: PracticeStep) {
    if (totalStrokes == 0) return
    Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalStrokes) { index ->
            val isCompleted = index < currentStrokeIndex
            val isActive = index == currentStrokeIndex
            val color =
                    when {
                        isActive -> MaterialTheme.colorScheme.primary
                        isCompleted -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
//            Box(
//                    modifier =
//                            Modifier.weight(1f)
//                                    .height(8.dp)
//                                    .clip(RoundedCornerShape(20.dp))
//                                    .background(color)
//            )
            if (index != totalStrokes - 1) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
    Text(
            text =
                    when (practiceStep) {
                        PracticeStep.GUIDE_AND_TRACE -> "Follow the animated guide"
                        PracticeStep.USER_WRITING_BLANK -> "Write from memory"
                        PracticeStep.AWAITING_NEXT_CHARACTER -> "Character complete!"
                        else -> "Keep flowing"
                    },
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
    )
}

@Preview
@Composable
private fun ContentUIPreview() {
    ContentUI(
            innerPadding = PaddingValues(16.dp),
            interactionSource = remember { MutableInteractionSource() },
            practiceStep = PracticeStep.GUIDE_AND_TRACE,
            currentCharacter = MLStrokeValidator.ALL_CHARS[1],
            guideAnimationProgress = 0.8f,
            userHasStartedTracing = false,
            currentStrokeIndex = 0,
            drawingEnabled = true,
            pathForCrossFade = null,
            crossFadeProgress = 0.5f,
            rightRevealProgress = remember { Animatable(0f) },
            advanceToNextStep = {},
            onDragStartAction = {},
            onStrokeFinished = {},
            clearSignal = MutableSharedFlow(),
            playCurrentCharacterSound = {},
            manualClear = {},
            requestNextCharacter = {},
            previousCharacter = {}
    )
}
