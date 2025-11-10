package com.rjnr.thaiwrter

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rjnr.thaiwrter.ui.navigation.NavDestinations
import com.rjnr.thaiwrter.ui.screens.ProgressScreen
import com.rjnr.thaiwrter.ui.screens.character_practice.CharacterPracticeScreen
import com.rjnr.thaiwrter.ui.screens.free_drawing.FreewritingScreen
import com.rjnr.thaiwrter.ui.screens.main.MainScreen
import com.rjnr.thaiwrter.ui.screens.onboarding.OnboardingFlow
import com.rjnr.thaiwrter.ui.screens.onboarding.OnboardingViewModel
import com.rjnr.thaiwrter.ui.theme.ThaiWrterTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ThaiWriterApp() {
    ThaiWrterTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            val navController = rememberNavController()
            val onboardingViewModel: OnboardingViewModel = koinViewModel()
            val onboardingState by onboardingViewModel.uiState.collectAsState()

            val startDestination =
                    if (onboardingState.settings.isComplete) {
                        NavDestinations.HOME
                    } else {
                        NavDestinations.ONBOARDING
                    }

            NavHost(navController = navController, startDestination = startDestination) {
                composable(NavDestinations.ONBOARDING) {
                    OnboardingFlow(navController = navController)
                }
                composable(NavDestinations.HOME) { MainScreen(navController = navController) }
                composable(NavDestinations.CHARACTER_PRACTICE) {
                    CharacterPracticeScreen(navController = navController)
                }
                composable(NavDestinations.PROGRESS) {
                    ProgressScreen(navController = navController)
                }
                composable(NavDestinations.FREEWRITING) { FreewritingScreen() }
            }
        }
    }
}
