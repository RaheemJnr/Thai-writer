package com.rjnr.thaiwrter.ui.screens.conponents


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rjnr.thaiwrter.ui.screens.character_practice.PracticeStep

@Composable
fun StageProgressIndicator(
    practiceStep: PracticeStep,
    modifier: Modifier = Modifier
) {
    val activeColor = MaterialTheme.colorScheme.primary
    val completedColor = MaterialTheme.colorScheme.tertiary // A distinct color for completion
    val inactiveColor = MaterialTheme.colorScheme.surfaceVariant

    // Determine the color for the first dot (Trace stage)
    val dot1Color = when {
        practiceStep == PracticeStep.GUIDE_AND_TRACE ||
                practiceStep == PracticeStep.CROSS_FADING_TRACE -> activeColor
        else -> completedColor
    }

    // Determine the color for the second dot (Write stage)
    val dot2Color = when (practiceStep) {
        PracticeStep.GUIDE_AND_TRACE, PracticeStep.CROSS_FADING_TRACE -> inactiveColor
        PracticeStep.AWAITING_BLANK_SLATE, PracticeStep.USER_WRITING_BLANK, PracticeStep.CROSS_FADING_WRITE -> activeColor
        else -> completedColor
    }

    // Determine the color for the third dot (Complete stage)
    val dot3Color = when {
        practiceStep == PracticeStep.AWAITING_NEXT_CHARACTER -> completedColor
        else -> inactiveColor
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProgressDot(color = dot1Color)
        ProgressDot(color = dot2Color)
        ProgressDot(color = dot3Color)
    }
}

@Composable
private fun ProgressDot(color: Color) {
    Box(
        modifier = Modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(color)
    )
}