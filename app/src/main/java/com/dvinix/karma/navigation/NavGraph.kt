package com.dvinix.karma.navigation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Build// Added for better Focus icon
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.dvinix.karma.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.dvinix.karma.features.tasks.TasksScreen
import com.dvinix.karma.features.tasks.TasksViewModel
import com.dvinix.karma.features.focus.FocusScreen
import com.dvinix.karma.data.local.KarmaDatabase
import kotlinx.serialization.Serializable


sealed class BottomBarIcon {
    data class Vector(val imageVector: ImageVector) : BottomBarIcon()
    data class Drawable(@DrawableRes val resId: Int) : BottomBarIcon()
}

@Serializable
sealed interface Screen {
    @Serializable data object Tasks : Screen
    @Serializable data object Focus : Screen
    @Serializable data object Settings : Screen
}

@Composable
fun KarmaBottomBar(
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = Color.Black,
        contentColor = Color.White,
        tonalElevation = 0.dp
    ) {
        val items = listOf(
            Triple("Tasks", BottomBarIcon.Vector(Icons.AutoMirrored.Filled.List), Screen.Tasks),
            Triple("Focus", BottomBarIcon.Drawable(R.drawable.hourglass), Screen.Focus),
            Triple("Settings", BottomBarIcon.Vector(Icons.Default.Settings), Screen.Settings)
        )

        items.forEach { (label, icon, screen) ->
            val isSelected = currentScreen == screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onScreenSelected(screen) },
                icon = {
                    when (icon) {
                        is BottomBarIcon.Vector -> Icon(
                            imageVector = icon.imageVector,
                            contentDescription = label,
                            tint = if (isSelected) Color.White else Color.DarkGray
                        )
                        is BottomBarIcon.Drawable -> Icon(
                            painter = painterResource(icon.resId),
                            contentDescription = label,
                            tint = if (isSelected) Color.White else Color.DarkGray
                        )
                    }
                },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) Color.White else Color.DarkGray
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFF1A1A1A)
                )
            )

        }
    }
}

@Composable
fun NavGraph() {
    val backStack = remember { mutableStateListOf<Screen>(Screen.Tasks) }
    val currentScreen = backStack.lastOrNull() ?: Screen.Tasks

    Scaffold(
        containerColor = Color.Black,
        bottomBar = {
            KarmaBottomBar(
                currentScreen = currentScreen,
                onScreenSelected = { selectedScreen ->
                    if (currentScreen != selectedScreen) {
                        backStack.clear()
                        backStack.add(selectedScreen)
                    }
                }
            )
        }
    ) { padding ->
        NavDisplay(
            backStack = backStack,
            modifier = Modifier.padding(padding)
        ) { screen ->
            when (screen) {
                Screen.Tasks -> NavEntry(Screen.Tasks) {
                    val vm: TasksViewModel = viewModel(factory = TasksViewModel.Factory)
                    TasksScreen(
                        viewModel = vm,
                        onNavigateToFocus = {
                            backStack.clear()
                            backStack.add(Screen.Focus)
                        },
                        onNavigateToSettings = { // Pass the missing parameter here
                            backStack.clear()
                            backStack.add(Screen.Settings)
                        }
                    )
                }
                Screen.Focus -> NavEntry(Screen.Focus) {
                    FocusScreen(
                        task = null, // Will be enhanced later to pass selected task
                        onBack = {
                            backStack.clear()
                            backStack.add(Screen.Tasks)
                        }
                    )
                }
                Screen.Settings -> NavEntry(Screen.Settings) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Settings Screen", color = Color.White)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewM3NavBar() {
    MaterialTheme {
        Surface(color = Color.Black) {
            KarmaBottomBar(
                currentScreen = Screen.Tasks,
                onScreenSelected = {}
            )
        }
    }
}