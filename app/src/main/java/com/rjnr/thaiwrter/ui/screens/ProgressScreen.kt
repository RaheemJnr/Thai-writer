package com.rjnr.thaiwrter.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.rjnr.thaiwrter.ui.drawing.StrokeGuide


@Composable
fun ProgressScreen(navController: NavController) {

    val infiniteTransition = rememberInfiniteTransition()

    val scale by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec =
            infiniteRepeatable(
                // Infinitely repeating a 1000ms tween animation using default easing curve.
                animation = tween(3500, easing = LinearEasing),
                // After each iteration of the animation (i.e. every 1000ms), the animation
                // will
                // start again from the [initialValue] defined above.
                // This is the default [RepeatMode]. See [RepeatMode.Reverse] below for an
                // alternative.
                repeatMode = RepeatMode.Restart,
            ),
    )
    val testStroke = listOf(
        "M0.5 270.5C21 212.5 109.993 204.408 156.5 245C213.851 295.057 170.619 425.183 94.5 426C14.2531 426.861 9.08282 315 21.5 227C33 145.5 65.1 23.8 94.5 7.5C123.9 -8.8 138.7 145 172.5 145C206.3 145 240.9 -13.8 263.5 7.5C286.1 28.8 289.3 124.4 285.5 251.5C281.7 378.6 235 643 247.5 643C260 643 297.2 378.6 348 251.5C398.8 124.4 449 28.8 498.5 7.5C548 -13.8 571.1 17.9 595.5 145C619.9 272.1 622.5 543.4 627.5 643"
    )



    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        StrokeGuide(
            strokes = testStroke,
            animationProgress = scale,
            userHasStartedTracing = false,
            marginToApply = 0.2f,
            modifier = Modifier.fillMaxSize()
        )
    }
}