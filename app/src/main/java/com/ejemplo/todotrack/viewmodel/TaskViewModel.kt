package com.ejemplo.todotrack.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ejemplo.todotrack.database.entities.Task
import com.ejemplo.todotrack.repository.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    // ✅ MOVER FilterType DENTRO de TaskViewModel
    enum class FilterType {
        ALL, PENDING, COMPLETED
    }

    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> = _tasks

    private val _pendingCount = MutableLiveData<Int>()
    val pendingCount: LiveData<Int> = _pendingCount

    private var currentFilter: FilterType = FilterType.ALL

    init {
        loadTasks()
    }

    fun loadTasks() {
        CoroutineScope(Dispatchers.IO).launch {
            val taskList = when (currentFilter) {
                FilterType.ALL -> repository.getAllTasks()
                FilterType.PENDING -> repository.getPendingTasks()
                FilterType.COMPLETED -> repository.getCompletedTasks()
                // ✅ EL 'when' ES EXHAUSTIVO PORQUE CUBRE TODOS LOS CASOS DEL ENUM
            }
            _tasks.postValue(taskList)
            _pendingCount.postValue(repository.getPendingCount())
        }
    }

    fun addTask(title: String) {
        if (title.isNotBlank()) {
            CoroutineScope(Dispatchers.IO).launch {
                repository.insertTask(Task(title = title))
                loadTasks()
            }
        }
    }

    fun toggleTaskCompletion(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            val updatedTask = task.copy(completed = !task.completed)
            repository.updateTask(updatedTask)
            loadTasks()
        }
    }

    fun deleteTask(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.deleteTask(task)
            loadTasks()
        }
    }

    // ✅ AGREGAR ESTA FUNCIÓN PARA USAR deleteCompletedTasks
    fun deleteCompletedTasks() {
        CoroutineScope(Dispatchers.IO).launch {
            repository.deleteCompletedTasks()
            loadTasks()
        }
    }

    fun setFilter(filter: FilterType) {
        currentFilter = filter
        loadTasks()
    }
}