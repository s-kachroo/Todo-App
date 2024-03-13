package com.example.todoapp.data.todo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * The Room database for this app, containing the [ToDoDao] and utilizing the [ToDo] entity.
 *
 * This abstract class is a singleton to prevent having multiple instances of the database opened
 * at the same time.
 *
 * @property todoDao Access point for managing [ToDo] data.
 */
@Database(entities = [ToDo::class], version = 1, exportSchema = false)
abstract class ToDoDatabase : RoomDatabase() {
    abstract val todoDao: ToDoDao

    companion object {
        // The volatile keyword ensures that changes made by one thread to INSTANCE are visible to all other threads immediately.
        @Volatile
        private var INSTANCE: ToDoDatabase? = null

        /**
         * Returns the singleton instance of [ToDoDatabase].
         *
         * If the instance is `null`, builds the database instance. Otherwise, returns the existing instance.
         * This function uses a synchronized block to ensure only one database instance is created.
         *
         * @param context The context used to get the application context for database creation.
         * @return The singleton instance of [ToDoDatabase].
         */
        fun getInstance(context: Context): ToDoDatabase {
            // Return the existing INSTANCE if it's not null, otherwise create the database instance.
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        /**
         * Builds the database instance using Room's database builder.
         *
         * @param context The context used to get the application context for database creation.
         * @return The newly created database instance.
         */
        private fun buildDatabase(context: Context): ToDoDatabase {
            return Room.databaseBuilder(
                context.applicationContext, ToDoDatabase::class.java, "todo_database"
            )
                // Consider adding migration strategies for schema changes
                .fallbackToDestructiveMigration().build()
        }
    }
}