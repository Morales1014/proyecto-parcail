package com.ejemplo.todotrack.repository

import com.ejemplo.todotrack.database.TaskDatabaseHelper
import com.ejemplo.todotrack.database.entities.Task

class TaskRepository(private val dbHelper: TaskDatabaseHelper) {

    fun getAllTasks(): List<Task> = dbHelper.getAllTasks()

    fun getPendingTasks(): List<Task> = dbHelper.getPendingTasks()

    fun getCompletedTasks(): List<Task> = dbHelper.getCompletedTasks()

    fun insertTask(task: Task): Long = dbHelper.insertTask(task)

    fun updateTask(task: Task): Int = dbHelper.updateTask(task)

    fun deleteTask(task: Task): Int = dbHelper.deleteTask(task)

    fun deleteCompletedTasks(): Int = dbHelper.deleteCompletedTasks()

    fun getPendingCount(): Int = dbHelper.getPendingCount()
}