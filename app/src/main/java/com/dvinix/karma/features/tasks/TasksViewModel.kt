package com.dvinix.karma.features.tasks

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.dvinix.karma.KarmaApp
import com.dvinix.karma.data.local.Task
import com.dvinix.karma.data.local.TaskDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

class TasksViewModel(
    application: Application,
    private val taskDao: TaskDao
) : AndroidViewModel(application) {

    // Store categories persistently in a separate flow
    private val _permanentCategories = MutableStateFlow<Set<String>>(setOf("Inbox"))
    
    // Selected category state
    private val _selectedCategory = MutableStateFlow("Inbox")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Combine permanent categories with categories from tasks
    val categories: StateFlow<List<String>> = combine(
        _permanentCategories,
        taskDao.getAllCategories()
    ) { permanent, fromTasks ->
        (permanent + fromTasks).distinct().sorted()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = listOf("Inbox")
    )

    // Tasks filtered by selected category
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<List<Task>> = _selectedCategory
        .flatMapLatest { category ->
            taskDao.getTasksByCategory(category)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Select a category
    fun selectCategory(categoryName: String) {
        _selectedCategory.value = categoryName
    }

    // Add a new category
    fun addCategory(name: String) {
        viewModelScope.launch {
            if (name.isNotBlank() && !categories.value.contains(name)) {
                // Add to permanent categories set
                _permanentCategories.update { current ->
                    current + name
                }
                // Also select the new category
                _selectedCategory.value = name
            }
        }
    }

    fun deleteCategory(categoryName: String) {
        if (categoryName == "Inbox") return // Don't allow deleting Inbox
        
        viewModelScope.launch {
            // Remove from permanent categories
            _permanentCategories.update { current ->
                current - categoryName
            }
            
            // Move all tasks in this category to Inbox
            val tasksInCategory = taskDao.getTasksByCategory(categoryName).first()
            tasksInCategory.forEach { task ->
                taskDao.updateTask(task.copy(category = "Inbox"))
            }
            
            // Switch to Inbox if we were viewing the deleted category
            if (_selectedCategory.value == categoryName) {
                _selectedCategory.value = "Inbox"
            }
        }
    }

    // Add task to current selected category
    fun addTask(
        title: String,
        date: Long?,
        hour: Int?,
        minute: Int?,
        category: String = _selectedCategory.value
    ) {
        viewModelScope.launch {
            val newTask = Task(
                title = title,
                category = category,
                reminderDate = date,
                reminderHour = hour,
                reminderMinute = minute
            )
            taskDao.insertTask(newTask)

            if (date != null && hour != null && minute != null) {
                scheduleNotification(title, date, hour, minute)
            }
        }
    }
    private fun scheduleNotification(title: String, dateMillis: Long, hour: Int, minute: Int) {
        val context = getApplication<Application>().applicationContext
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Set the specific time on the calendar
        val calendar = Calendar.getInstance().apply {
            timeInMillis = dateMillis
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Guard against scheduling an alarm in the past
        if (calendar.timeInMillis <= System.currentTimeMillis()) return

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("TASK_TITLE", title)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            title.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Handle Exact Alarm Permission for Android 12+ (API 31+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                val settingsIntent = Intent().apply {
                    action = android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM

                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(settingsIntent)
                return
            }
        }

        // 3. The Final Step: Actually schedule the alarm
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskDao.deleteTask(task)
        }
    }

    fun toggleTaskCompletion(task: Task, isCompleted: Boolean) {
        viewModelScope.launch {
            val updatedTask = task.copy(isCompleted = isCompleted)
            taskDao.updateTask(updatedTask)
        }
    }

    // Update an existing task
    fun updateTask(task: Task, newTitle: String, newCategory: String? = null) {
        viewModelScope.launch {
            val updatedTask = task.copy(
                title = newTitle,
                category = newCategory ?: task.category
            )
            taskDao.updateTask(updatedTask)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as KarmaApp)
                TasksViewModel(
                    application,
                    application.database.taskDao()
                )
            }
        }
    }
}