package com.dvinix.karma.features.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.dvinix.karma.KarmaApp
import com.dvinix.karma.data.local.Task
import com.dvinix.karma.data.local.TaskDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TasksViewModel(private val taskDao: TaskDao) : ViewModel() {

    // 1. Transforming Data (The "State")
    // We take the "Flow" from the database and turn it into a "StateFlow".
    // StateFlow is what Jetpack Compose uses to know when to redraw the screen.
    val uiState: StateFlow<List<Task>> = taskDao.getAllTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Hibernate if app is hidden
            initialValue = emptyList()
        )

    // 2. Handling Actions
    // We use viewModelScope.launch to run database work on a background thread.
    // This prevents the UI from freezing.
    fun addTask(title: String) {
        viewModelScope.launch {
            taskDao.insertTask(
                Task(title = title)
            )
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskDao.deleteTask(task)
        }
    }

    fun toggleTaskCompletion(task: Task, isCompleted: Boolean) {
        viewModelScope.launch {
            // Create a copy of the task with the updated status
            val updatedTask = task.copy(isCompleted = isCompleted)
            taskDao.insertTask(updatedTask) // Room 'insert' with OnConflict.REPLACE acts as an update
        }
    }

    // 3. The Factory (The "Dependency Injector")
    // This tells Android how to create this ViewModel and give it the TaskDao.
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as KarmaApp)
                TasksViewModel(application.database.taskDao())
            }
        }
    }
}