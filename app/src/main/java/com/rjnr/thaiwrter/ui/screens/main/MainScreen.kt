package com.rjnr.thaiwrter.ui.screens.main

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rjnr.thaiwrter.ui.navigation.NavDestinations
import com.rjnr.thaiwrter.utils.ConnectionStatus
import com.rjnr.thaiwrter.utils.MLStrokeValidator
import io.eyram.iconsax.IconSax
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(viewModel: MainViewModel = koinViewModel(), navController: NavController) {
    val dueReviews by viewModel.dueReviews.collectAsState()
    val networkStatus by viewModel.networkStatus.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
            topBar = { StudioTopBar() },
            bottomBar = {
                StudioBottomBar(
                        currentRoute = NavDestinations.HOME,
                        onNavigate = { route ->
                            navController.navigate(route) { launchSingleTop = true }
                        }
                )
            }
    ) { padding ->
        Column(
                modifier =
                        Modifier.fillMaxSize()
                                .background(Color(0xFFF7F5FA))
                                .verticalScroll(scrollState)
                                .padding(padding)
                                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            if (networkStatus == ConnectionStatus.Unavailable) {
                OfflineBanner()
                Spacer(Modifier.height(12.dp))
            }

            DashboardHeroCard(
                    dueReviewsCount = dueReviews.size,
                    onStartPractice = { navController.navigate(NavDestinations.CHARACTER_PRACTICE) }
            )

            Spacer(Modifier.height(16.dp))

            QuickActionRow(
                    onPractice = { navController.navigate(NavDestinations.CHARACTER_PRACTICE) },
                    onFreewrite = { navController.navigate(NavDestinations.FREEWRITING) },
                    onReview = { navController.navigate(NavDestinations.PROGRESS) }
            )

            Spacer(Modifier.height(24.dp))

            PracticePreviewCard(
                    onPractice = { navController.navigate(NavDestinations.CHARACTER_PRACTICE) }
            )

            Spacer(Modifier.height(24.dp))

            TipsCarousel()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudioTopBar() {
    TopAppBar(
            title = { Text("Thai Writer Studio", fontWeight = FontWeight.SemiBold) },
            modifier = Modifier.statusBarsPadding()
    )
}

@Composable
private fun DashboardHeroCard(dueReviewsCount: Int, onStartPractice: () -> Unit) {
    val gradient = Brush.linearGradient(listOf(Color(0xFF574AE2), Color(0xFF9F87FF)))
    Surface(
            modifier = Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(28.dp)),
            color = Color.Transparent
    ) {
        Box(modifier = Modifier.background(gradient).fillMaxSize().padding(24.dp)) {
            Column(
                    modifier = Modifier.align(Alignment.TopStart),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Welcome back", color = Color.White.copy(alpha = 0.8f))
                Text(
                        text = "Keep the streak alive!",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                )
                Text(
                        text = "$dueReviewsCount reviews waiting",
                        color = Color.White.copy(alpha = 0.9f)
                )
            }

            Button(onClick = onStartPractice, modifier = Modifier.align(Alignment.BottomStart)) {
                Icon(Icons.Rounded.PlayArrow, contentDescription = null)
                Spacer(Modifier.size(6.dp))
                Text("Resume practice")
            }
        }
    }
}

@Composable
private fun QuickActionRow(onPractice: () -> Unit, onFreewrite: () -> Unit, onReview: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        QuickActionCard(
                title = "Guided practice",
                description = "Trace with smart feedback",
                icon = IconSax.Linear.Brush2,
                onClick = onPractice
        )
        QuickActionCard(
                title = "Freewriting lab",
                description = "Let ML guess your strokes",
                icon = IconSax.Linear.Book1,
                onClick = onFreewrite
        )
        QuickActionCard(
                title = "Review stack",
                description = "Spaced repetition queue",
                icon = IconSax.Linear.Cloud,
                onClick = onReview
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickActionCard(title: String, description: String, icon: Int, onClick: () -> Unit) {
    Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            onClick = onClick
    ) {
        Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledIconButton(
                    onClick = onClick,
                    shape = CircleShape,
                    modifier = Modifier.size(42.dp)
            ) { Icon(painter = painterResource(icon), contentDescription = title) }
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(description, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun PracticePreviewCard(onPractice: () -> Unit) {
    val sampleCharacter = remember { MLStrokeValidator.ALL_CHARS.firstOrNull() }
    Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Up next", fontWeight = FontWeight.Bold)
            Text(sampleCharacter?.character ?: "ก", style = MaterialTheme.typography.displayLarge)
            Text(sampleCharacter?.pronunciation ?: "ko kai")
            TextButton(onClick = onPractice) { Text("Jump back in") }
        }
    }
}

@Composable
private fun TipsCarousel() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Studio tips", fontWeight = FontWeight.SemiBold)
        listOf(
                        "Use the grid hints to anchor each stroke.",
                        "Tap the sound icon to hear native pronunciation.",
                        "Switch to blank mode to test your muscle memory."
                )
                .forEach { tip ->
                    Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF5FF)),
                            shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            FilledIconButton(onClick = {}, enabled = false) {
                                Icon(
                                        painter = painterResource(IconSax.Linear.Cloud),
                                        contentDescription = null
                                )
                            }
                            Text(tip)
                        }
                    }
                }
    }
}

@Composable
private fun OfflineBanner() {
    Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD)),
            shape = RoundedCornerShape(20.dp)
    ) {
        Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Rounded.Refresh, contentDescription = null, tint = Color(0xFF856404))
            Column {
                Text("Offline mode", fontWeight = FontWeight.Bold, color = Color(0xFF856404))
                Text(
                        "We'll sync progress when you're back online.",
                        color = Color(0xFF856404),
                        style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun StudioBottomBar(currentRoute: String, onNavigate: (String) -> Unit) {
    NavigationBar {
        NavigationBarItem(
                selected = currentRoute == NavDestinations.HOME,
                onClick = { onNavigate(NavDestinations.HOME) },
                icon = { Icon(painterResource(IconSax.Linear.Home), contentDescription = "Home") },
                label = { Text("Home") }
        )
        NavigationBarItem(
                selected = currentRoute == NavDestinations.CHARACTER_PRACTICE,
                onClick = { onNavigate(NavDestinations.CHARACTER_PRACTICE) },
                icon = {
                    Icon(painterResource(IconSax.Linear.Brush2), contentDescription = "Practice")
                },
                label = { Text("Practice") }
        )
        NavigationBarItem(
                selected = currentRoute == NavDestinations.PROGRESS,
                onClick = { onNavigate(NavDestinations.PROGRESS) },
                icon = {
                    Icon(painterResource(IconSax.Linear.Cloud), contentDescription = "Progress")
                },
                label = { Text("Progress") }
        )
    }
}
