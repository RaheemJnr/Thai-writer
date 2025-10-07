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
        "M37.0001 229.5C53.5001 190.5 78.0001 178.5 120.5 178.5C199.781 178.5 201.411 305.799 132.5 345C89.9999 369.177 50.4045 353 25 312.5C-9.49993 257.5 -4.12321 146 25 81C54.1232 16 116.743 -14.8447 171 16C213.875 40.374 216 134 216 134C216 134 243.719 9.31221 297 2C342.337 -4.22197 374.071 36.4422 384.5 81C423 245.5 319 564 272.5 621C226 678 156.877 663.5 120.5 621C84.1231 578.5 87.5178 514.665 147 467C229.567 400.837 335.5 511.418 384.5 548C457.5 602.5 522.5 621 583.5 621C644.5 621 612.167 216.333 615.5 16"
    )


    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        StrokeGuide(
            strokes = testStroke,
            animationProgress = scale,
            userHasStartedTracing = false,
            marginToApply = 0.2f,
            modifier = Modifier.fillMaxSize(),
            currentStrokeIndex = testStroke.size,
        )

    }
}