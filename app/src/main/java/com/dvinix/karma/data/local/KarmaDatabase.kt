package com.dvinix.karma.data.local

import android.content.Context
import androidx.annotation.UiContext
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Task::class], version = 3, exportSchema = false)
abstract class KarmaDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao  // connect our Dao to the Database

    companion object {
        @Volatile
        private  var INSTANCE: KarmaDatabase ?= null


        // The Singleton pattern: This function checks if the DB is already open.
        // If it is, it returns the existing one. If not, it builds a new one.

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