package com.rjnr.thaiwrter.ui.screens.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.rjnr.thaiwrter.ui.navigation.NavDestinations
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppEntryScreen(
        navController: NavController,
        viewModel: OnboardingStatusViewModel = koinViewModel()
) {
    val status by viewModel.status.collectAsState()

    LaunchedEffect(status) {
        if (!status.isLoaded) return@LaunchedEffect
        val target =
                if (status.isComplete) {
                    NavDestinations.HOME
                } else {
                    NavDestinations.ONBOARDING
                }
        navController.navigate(target) {
            popUpTo(NavDestinations.SPLASH) { inclusive = true }
            launchSingleTop = true
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}
