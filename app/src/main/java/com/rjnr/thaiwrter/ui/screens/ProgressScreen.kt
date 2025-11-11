package com.rjnr.thaiwrter.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rjnr.thaiwrter.data.models.UserProgress
import com.rjnr.thaiwrter.ui.screens.progress.ProgressViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(navController: NavController, viewModel: ProgressViewModel = koinViewModel()) {
    val dueReviews by viewModel.dueReviews.collectAsState()
    val streak by remember { mutableIntStateOf(6) }

    Scaffold(topBar = { TopAppBar(title = { Text("Progress & Review") }) }) { padding ->
        LazyColumn(
                modifier =
                        Modifier.fillMaxSize()
                                .background(Color(0xFFF6F4FB))
                                .padding(padding)
                                .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { StreakCard(streakDays = streak, dueCount = dueReviews.size) }
            item { ReviewQueueCard(dueReviews) }
            item { TipsCard() }
        }
    }
}

@Composable
private fun StreakCard(streakDays: Int, dueCount: Int) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF312ECB)),
            shape = RoundedCornerShape(32.dp)
    ) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Daily streak", color = Color.White.copy(alpha = 0.8f))
            Text(
                    "$streakDays days",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
            )
            Text("$dueCount reviews waiting", color = Color.White.copy(alpha = 0.8f))
        }
    }
}

@Composable
private fun ReviewQueueCard(queue: List<UserProgress>) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Review queue", style = MaterialTheme.typography.titleMedium)
            if (queue.isEmpty()) {
                Text("You're caught up! Next reviews will appear here.")
            } else {
                queue.take(5).forEach { progress -> ReviewRow(progress) }
            }
        }
    }
}

@Composable
private fun ReviewRow(progress: UserProgress) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Character #${progress.characterId}")
        Text(
                "Next ${(progress.nextReviewDate)}",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun TipsCard() {
    Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF8F0)),
            shape = RoundedCornerShape(28.dp)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Growth tips", fontWeight = FontWeight.Bold)
            Text("• Alternate between guided and blank modes for muscle memory.")
            Text("• Revisit tricky strokes from freewriting to reinforce shapes.")
        }
    }
}
