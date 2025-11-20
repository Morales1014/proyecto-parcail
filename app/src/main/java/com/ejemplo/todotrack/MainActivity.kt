package com.ejemplo.todotrack

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ejemplo.todotrack.database.TaskDatabaseHelper
import com.ejemplo.todotrack.repository.TaskRepository
import com.ejemplo.todotrack.viewmodel.TaskViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var taskList: RecyclerView
    private lateinit var taskCounter: TextView
    private lateinit var emptyState: View
    private lateinit var fabAddTask: FloatingActionButton
    private lateinit var viewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskList = findViewById(R.id.taskList)
        taskCounter = findViewById(R.id.taskCounter)
        emptyState = findViewById(R.id.emptyState)
        fabAddTask = findViewById(R.id.fabAddTask)

        val dbHelper = TaskDatabaseHelper(this)
        val repository = TaskRepository(dbHelper)
        viewModel = TaskViewModel(repository)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        viewModel.loadTasks()
    }

    private fun setupRecyclerView() {
        val adapter = TaskAdapter(
            onTaskToggled = { task ->
                viewModel.toggleTaskCompletion(task)
            },
            onTaskDeleted = { task ->
                showDeleteConfirmation(task)
            }
        )

        taskList.layoutManager = LinearLayoutManager(this)
        taskList.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.tasks.observe(this, Observer { tasks ->
            (taskList.adapter as TaskAdapter).submitList(tasks)
            updateEmptyState(tasks.isEmpty())
        })

        viewModel.pendingCount.observe(this, Observer { count ->
            taskCounter.text = getString(R.string.pending_tasks, count)
        })
    }

    private fun setupClickListeners() {fabAddTask.setOnClickListener {
        showAddTaskDialog()
    }

        findViewById<View>(R.id.filterAll).setOnClickListener {
            viewModel.setFilter(TaskViewModel.FilterType.ALL)
        }

        findViewById<View>(R.id.filterPending).setOnClickListener {
            viewModel.setFilter(TaskViewModel.FilterType.PENDING)
        }

        findViewById<View>(R.id.filterCompleted).setOnClickListener {
            viewModel.setFilter(TaskViewModel.FilterType.COMPLETED)
        }
    }


    private fun showAddTaskDialog() {
        val dialog = AddTaskDialogFragment()
        dialog.setOnTaskAddedListener { title ->
            viewModel.addTask(title)
        }
        dialog.show(supportFragmentManager, "AddTaskDialog")
    }

    private fun showDeleteConfirmation(task: com.ejemplo.todotrack.database.entities.Task) {
        android.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete))
            .setMessage(getString(R.string.delete_task_message))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                viewModel.deleteTask(task)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            emptyState.visibility = View.VISIBLE
            taskList.visibility = View.GONE
        } else {
            emptyState.visibility = View.GONE
            taskList.visibility = View.VISIBLE
        }
    }
}