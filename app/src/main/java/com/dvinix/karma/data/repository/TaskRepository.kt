package com.dvinix.karma.data.repository

import com.dvinix.karma.data.local.Task
import com.dvinix.karma.data.local.TaskDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


class TaskRepository(private val taskDao: TaskDao) {

    // If a task is added in the DB, the UI updates automatically.
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    // suspend means I'm going to take a moment.

    // It tells Kotlin this function runs in the background...Nigga
    suspend fun insert(task: Task) = taskDao.insertTask(task)
}
