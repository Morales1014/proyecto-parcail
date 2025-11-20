package com.ejemplo.todotrack.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ejemplo.todotrack.database.entities.Task
import java.util.*

class TaskDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "todo_database.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_TASKS = "tasks"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_COMPLETED = "completed"
        private const val COLUMN_CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_TASKS (
                $COLUMN_ID TEXT PRIMARY KEY,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_COMPLETED INTEGER DEFAULT 0,
                $COLUMN_CREATED_AT INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        onCreate(db)
    }

    fun insertTask(task: Task): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID, task.id)
            put(COLUMN_TITLE, task.title)
            put(COLUMN_COMPLETED, if (task.completed) 1 else 0)
            put(COLUMN_CREATED_AT, task.createdAt.time)
        }
        return db.insert(TABLE_TASKS, null, values)
    }

    fun getAllTasks(): List<Task> {
        val tasks = mutableListOf<Task>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_TASKS,
            null, null, null, null, null,
            "$COLUMN_CREATED_AT DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                val task = Task(
                    id = it.getString(it.getColumnIndexOrThrow(COLUMN_ID)),
                    title = it.getString(it.getColumnIndexOrThrow(COLUMN_TITLE)),
                    completed = it.getInt(it.getColumnIndexOrThrow(COLUMN_COMPLETED)) == 1,
                    createdAt = Date(it.getLong(it.getColumnIndexOrThrow(COLUMN_CREATED_AT)))
                )
                tasks.add(task)
            }
        }
        return tasks
    }

    fun getPendingTasks(): List<Task> = getAllTasks().filter { !it.completed }
    fun getCompletedTasks(): List<Task> = getAllTasks().filter { it.completed }

    fun updateTask(task: Task): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, task.title)
            put(COLUMN_COMPLETED, if (task.completed) 1 else 0)
        }
        return db.update(TABLE_TASKS, values, "$COLUMN_ID = ?", arrayOf(task.id))
    }

    fun deleteTask(task: Task): Int {
        val db = writableDatabase
        return db.delete(TABLE_TASKS, "$COLUMN_ID = ?", arrayOf(task.id))
    }

    fun deleteCompletedTasks(): Int {
        val db = writableDatabase
        return db.delete(TABLE_TASKS, "$COLUMN_COMPLETED = 1", null)
    }

    fun getPendingCount(): Int = getPendingTasks().size
}