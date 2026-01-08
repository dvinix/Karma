package com.dvinix.karma.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities = [Task::class],
    version = 1,
    exportSchema = false
)
abstract class KarmaDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private  var INSTANCE: KarmaDatabase ?= null

        fun getDatabase(context: Context): KarmaDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KarmaDatabase::class.java,
                    "karma_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }



        }
    }

}