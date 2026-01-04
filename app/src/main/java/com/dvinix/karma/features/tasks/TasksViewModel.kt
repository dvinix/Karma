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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class TasksViewModel(application: Application, private val taskDao: TaskDao) : AndroidViewModel(application) {

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
    fun addTask(
        title: String,
        date: Long?,
        hour: Int?,
        minute: Int?,
        folder: String = "Inbox"
    ) {
        viewModelScope.launch {
            val newTask = Task(
                title = title,
                category = folder,
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
                TasksViewModel(application,application.database.taskDao())
            }
        }
    }
}