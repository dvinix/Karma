package com.dvinix.karma.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY  createdAt DESC")
    fun getAllTasks(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    // Get tasks by specific folder (e.g., 'Second Brain')
    @Query("SELECT * FROM tasks WHERE category = :folderName ORDER BY createdAt DESC")
    fun getTasksByFolder(folderName: String): Flow<List<Task>>

}
