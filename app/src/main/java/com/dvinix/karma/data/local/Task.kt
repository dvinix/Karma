package com.dvinix.karma.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val isCompleted: Boolean = false,
    val category: String = "Inbox", // Default folder
    val reminderDate: Long? = null, // Selected date millis
    val reminderHour: Int? = null, // Selected hour (0-23)
    val reminderMinute: Int? = null, // Selected minute (0-59)
    val createdAt: Long = System.currentTimeMillis()
)