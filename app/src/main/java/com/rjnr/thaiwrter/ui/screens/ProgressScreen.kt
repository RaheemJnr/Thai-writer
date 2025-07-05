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
        "M218.539 166.333C163.31 228.432 157.039 233.833 77.5386 233.833C11.37 233.833 -23.6758 87.8423 21.0384 41.8333C65.1157 -3.5203 126.074 -11.4507 181.039 19.8333C251.098 59.7088 202.366 192.397 206.539 248.833L196.039 486.833C196.039 531.233 211.039 585.333 218.539 606.833C264.139 510.833 396.205 410.833 456.539 372.833C546.539 349.233 601.514 361.03 636.539 425.333C672.076 490.576 650.671 577.211 582.539 606.833C548.039 621.833 507.559 608.055 479.039 567.833C449.511 526.192 456.539 452.333 456.539 425.333L470.039 286.333V19.8333"
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