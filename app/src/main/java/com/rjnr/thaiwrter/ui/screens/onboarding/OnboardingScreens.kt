package com.rjnr.thaiwrter.ui.screens.onboarding

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rjnr.thaiwrter.ui.navigation.NavDestinations
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingFlow(navController: NavController, viewModel: OnboardingViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (uiState.settings.isComplete) {
        LaunchedEffect(Unit) {
            navController.navigate(NavDestinations.HOME) {
                popUpTo(NavDestinations.ONBOARDING) { inclusive = true }
            }
        }
        return
    }

    val pagerState =
            rememberPagerState(
                    initialPage = uiState.currentStep,
                    pageCount = { uiState.totalSteps }
            )
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) { viewModel.setStep(pagerState.currentPage) }

    Column(
            modifier =
                    Modifier.fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(24.dp)
    ) {
        StepIndicators(current = pagerState.currentPage, total = uiState.totalSteps)

        Spacer(Modifier.height(24.dp))

        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
            when (page) {
                0 -> HeroStep(onContinue = { scope.launch { pagerState.animateScrollToPage(1) } })
                1 ->
                        GoalSelectionStep(
                                selectedGoal = uiState.settings.goal,
                                onGoalSelected = viewModel::updateGoal,
                                onContinue = { scope.launch { pagerState.animateScrollToPage(2) } }
                        )
                2 ->
                        PracticeSetupStep(
                                pace = uiState.settings.paceMinutes,
                                confidence = uiState.settings.confidence,
                                onPaceChange = viewModel::updatePace,
                                onConfidenceChange = viewModel::updateConfidence,
                                onContinue = { scope.launch { pagerState.animateScrollToPage(3) } }
                        )
                else ->
                        CommitmentStep(
                                settings = uiState.settings,
                                onEdit = { scope.launch { pagerState.animateScrollToPage(it) } },
                                onFinish = {
                                    viewModel.completeOnboarding()
                                    navController.navigate(NavDestinations.HOME) {
                                        popUpTo(NavDestinations.ONBOARDING) { inclusive = true }
                                    }
                                }
                        )
            }
        }

        Spacer(Modifier.height(16.dp))

        TextButton(
                onClick = { viewModel.completeOnboarding() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
        ) { Text("Skip for now") }
    }
}

@Composable
private fun StepIndicators(current: Int, total: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(total) { index ->
            val isActive = index == current
            val alpha by
                    animateFloatAsState(
                            targetValue = if (isActive) 1f else 0.35f,
                            label = "indicator"
                    )
            Box(
                    modifier =
                            Modifier.alpha(alpha)
                                    .height(6.dp)
                                    .weight(if (isActive) 2f else 1f)
                                    .clip(CircleShape)
                                    .background(
                                            brush =
                                                    Brush.horizontalGradient(
                                                            colors =
                                                                    listOf(
                                                                            MaterialTheme
                                                                                    .colorScheme
                                                                                    .primary,
                                                                            MaterialTheme
                                                                                    .colorScheme
                                                                                    .tertiary
                                                                    )
                                                    )
                                    )
            )
        }
    }
}

@Composable
private fun HeroStep(onContinue: () -> Unit) {
    Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
                modifier =
                        Modifier.fillMaxWidth()
                                .weight(1f)
                                .clip(RoundedCornerShape(32.dp))
                                .background(
                                        Brush.linearGradient(
                                                listOf(
                                                        MaterialTheme.colorScheme.primary,
                                                        MaterialTheme.colorScheme.secondary
                                                )
                                        )
                                ),
                contentAlignment = Alignment.Center
        ) {
            Text(
                    text = "สวัสดี",
                    style =
                            MaterialTheme.typography.displayLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                            )
            )
        }
        Spacer(Modifier.height(24.dp))
        Text(
                text =
                        "Master Thai handwriting with rich guidance, spaced repetition, and immersive audio.",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) {
            Text("Begin the Journey")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoalSelectionStep(
        selectedGoal: String,
        onGoalSelected: (String) -> Unit,
        onContinue: () -> Unit
) {
    val goals =
            listOf(
                    "travel" to "Navigate Thailand with confidence",
                    "heritage" to "Reconnect with your roots",
                    "study" to "Prepare for academic success"
            )
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                    "Why are you learning Thai?",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold
            )
            goals.forEach { (value, description) ->
                val isSelected = value == selectedGoal
                Card(
                        colors =
                                CardDefaults.cardColors(
                                        containerColor =
                                                if (isSelected) {
                                                    MaterialTheme.colorScheme.primaryContainer
                                                } else {
                                                    MaterialTheme.colorScheme.surfaceVariant
                                                }
                                ),
                        onClick = { onGoalSelected(value) }
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text(
                                text = value.replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                        )
                        Text(
                                text = description,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) { Text("Looks good") }
    }
}

@Composable
private fun PracticeSetupStep(
        pace: Int,
        confidence: Int,
        onPaceChange: (Int) -> Unit,
        onConfidenceChange: (Int) -> Unit,
        onContinue: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
        Column(verticalArrangement = Arrangement.spacedBy(32.dp)) {
            Text(
                    "Shape your daily groove",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
            )

            MetricSlider(
                    title = "Daily focus (minutes)",
                    value = pace.toFloat(),
                    valueRange = 5f..25f,
                    onValueChange = { onPaceChange(it.toInt()) },
                    display = "${pace}m"
            )

            MetricSlider(
                    title = "Writing confidence",
                    value = confidence.toFloat(),
                    valueRange = 0f..4f,
                    steps = 3,
                    onValueChange = { onConfidenceChange(it.toInt()) },
                    display = listOf("New", "Curious", "Growing", "Solid", "Flow")[confidence]
            )
        }

        Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) { Text("Continue") }
    }
}

@Composable
private fun MetricSlider(
        title: String,
        value: Float,
        valueRange: ClosedFloatingPointRange<Float>,
        onValueChange: (Float) -> Unit,
        display: String,
        steps: Int = 0
) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            Slider(
                    value = value,
                    onValueChange = onValueChange,
                    valueRange = valueRange,
                    steps = steps
            )
            Text(
                    display,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
private fun CommitmentStep(
        settings: com.rjnr.thaiwrter.data.preferences.OnboardingSettings,
        onEdit: (Int) -> Unit,
        onFinish: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
        Text(
                "Your Thai handwriting ritual",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
        )
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryItem(
                    title = "Goal",
                    body = settings.goal.replaceFirstChar { it.uppercase() },
                    onEdit = { onEdit(1) }
            )
            SummaryItem(
                    title = "Daily focus",
                    body = "${settings.paceMinutes} minutes",
                    onEdit = { onEdit(2) }
            )
            SummaryItem(
                    title = "Confidence",
                    body =
                            listOf("New", "Curious", "Growing", "Solid", "Flow")[
                                    settings.confidence.coerceIn(0, 4)],
                    onEdit = { onEdit(2) }
            )
        }

        Button(onClick = onFinish, modifier = Modifier.fillMaxWidth()) { Text("Enter the Studio") }
    }
}

@Composable
private fun SummaryItem(title: String, body: String, onEdit: () -> Unit) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
                modifier = Modifier.padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(title, style = MaterialTheme.typography.labelMedium)
                Text(
                        body,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                )
            }
            TextButton(onClick = onEdit) { Text("Edit") }
        }
    }
}
