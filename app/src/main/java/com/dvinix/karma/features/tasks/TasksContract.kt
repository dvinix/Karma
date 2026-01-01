package com.dvinix.karma.features.tasks

data class TasksState(
    val tasks: List<String> = emptyList(), // Placeholder for now
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface TasksEvent {
    data class AddTask(val title: String) : TasksEvent
    data class DeleteTask(val id: Int) : TasksEvent
    data object Refresh : TasksEvent
}
