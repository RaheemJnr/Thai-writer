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
        "M11.7896 488.5C37.5 469 61 427 134.5 431.5C211.973 436.243 271.872 534.446 221.5 593.5C173.131 650.205 86.5723 649.597 37.5 593.5C-7.65158 541.885 19.5 426 21 418C75.8 333.2 134.5 316.364 155.5 303C181.9 286.2 228.5 278 248.5 276L1.5 208.5C58 67.5 114.04 17.0669 248.5 4.99999C365.5 -5.5 466.3 -1.89185 534.5 93C568.339 140.082 582.5 187.5 581 208.5V611.5"
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