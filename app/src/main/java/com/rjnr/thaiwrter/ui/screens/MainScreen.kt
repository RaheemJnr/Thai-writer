package com.rjnr.thaiwrter.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.rjnr.thaiwrter.ui.navigation.NavDestinations
import com.rjnr.thaiwrter.ui.viewmodel.MainViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel(),
    navController: NavController
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = true,
                    onClick = { navController.navigate(NavDestinations.HOME) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Create, contentDescription = "Practice") },
                    label = { Text("Practice") },
                    selected = false,
                    onClick = { navController.navigate(NavDestinations.CHARACTER_PRACTICE) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Refresh, contentDescription = "Progress") },
                    label = { Text("Progress") },
                    selected = false,
                    onClick = { navController.navigate(NavDestinations.PROGRESS) }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Content will go here
            Text("Main Screen Content")
        }
    }
}
