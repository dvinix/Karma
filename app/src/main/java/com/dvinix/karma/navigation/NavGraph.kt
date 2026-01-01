package com.dvinix.karma.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.dvinix.karma.features.tasks.TasksScreen
import com.dvinix.karma.features.tasks.TasksViewModel
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable data object Tasks : Screen
    @Serializable data object Focus : Screen
}


@Composable
fun NavGraph() {
    val backStack = remember {
        mutableStateListOf<Screen>(Screen.Tasks)
    }

    NavDisplay(
        backStack = backStack,
        onBack = {
            if (backStack.size > 1) {
                backStack.removeAt(backStack.lastIndex)
            }
        }
    ) { screen ->

        when (screen) {

            Screen.Tasks -> NavEntry(Screen.Tasks) {
                val viewModel: TasksViewModel =
                    viewModel(factory = TasksViewModel.Factory)

                TasksScreen(
                    viewModel = viewModel,
                    onNavigateToFocus = {
                        backStack.add(Screen.Focus)
                    }
                )
            }

            Screen.Focus -> NavEntry(Screen.Focus) {
                // FocusScreen()
            }
        }
    }
}