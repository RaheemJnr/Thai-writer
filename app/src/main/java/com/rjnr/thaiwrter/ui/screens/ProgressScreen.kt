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
        "M20.5003 187.5C59.0003 229.5 87.5003 266.5 183.5 254C252.5 245.016 271.129 187.116 271 122C270.846 43.9722 227.017 -0.351513 149 1C75.8901 2.26653 18.5821 41.6158 6.99998 112C-12.5 230.5 20.5 349 80 396C139.046 452.708 257.5 372.5 308 386C209.5 410.5 101 461 80 510.5C94.8 590.5 140.7 620.794 219.5 630C326.5 642.5 426.5 630 484.5 597C542.5 564 552.5 442.5 552.5 442.5L561 1.00001"
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