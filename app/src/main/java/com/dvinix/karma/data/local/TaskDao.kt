package com.dvinix.karma.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @androidx.room.Update
    suspend fun updateTask(task: Task)

    // Get tasks by specific category
    @Query("SELECT * FROM tasks WHERE category = :categoryName ORDER BY createdAt DESC")
    fun getTasksByCategory(categoryName: String): Flow<List<Task>>

    // Get tasks by specific folder (e.g., 'Second Brain')
    @Query("SELECT * FROM tasks WHERE category = :folderName ORDER BY createdAt DESC")
    fun getTasksByFolder(folderName: String): Flow<List<Task>>

    // Get all distinct categories from tasks
    @Query("SELECT DISTINCT category FROM tasks ORDER BY category ASC")
    fun getAllCategories(): Flow<List<String>>

}
