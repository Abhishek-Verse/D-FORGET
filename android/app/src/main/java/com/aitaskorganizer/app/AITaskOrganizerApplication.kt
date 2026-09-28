package com.aitaskorganizer.app

import android.app.Application
import android.content.Context
import com.aitaskorganizer.app.data.local.database.AppDatabase
import com.aitaskorganizer.app.data.repository.ReminderRepository
import com.aitaskorganizer.app.data.repository.TaskRepository

/**
 * Application class for AI Task Organizer.
 */
class AITaskOrganizerApplication : Application() {

    companion object {
        lateinit var appContext: Context
            private set
    }

    val database by lazy { AppDatabase.getDatabase(this) }
    val taskRepository by lazy { TaskRepository(database.taskDao()) }
    val reminderRepository by lazy { ReminderRepository(database.reminderDao()) }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}
