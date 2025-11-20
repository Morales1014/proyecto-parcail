package com.ejemplo.todotrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val onTaskToggled: (com.ejemplo.todotrack.database.entities.Task) -> Unit,
    private val onTaskDeleted: (com.ejemplo.todotrack.database.entities.Task) -> Unit
) : ListAdapter<com.ejemplo.todotrack.database.entities.Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskCheckbox: CheckBox = itemView.findViewById(R.id.taskCheckbox)
        private val taskTitle: TextView = itemView.findViewById(R.id.taskTitle)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(task: com.ejemplo.todotrack.database.entities.Task) {
            taskTitle.text = task.title
            taskCheckbox.isChecked = task.completed

            if (task.completed) {
                taskTitle.paintFlags = taskTitle.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                taskTitle.paintFlags = taskTitle.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            taskCheckbox.setOnClickListener {
                onTaskToggled(task)
            }

            deleteButton.setOnClickListener {
                onTaskDeleted(task)
            }
        }
    }

    object TaskDiffCallback : DiffUtil.ItemCallback<com.ejemplo.todotrack.database.entities.Task>() {
        override fun areItemsTheSame(oldItem: com.ejemplo.todotrack.database.entities.Task, newItem: com.ejemplo.todotrack.database.entities.Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: com.ejemplo.todotrack.database.entities.Task, newItem: com.ejemplo.todotrack.database.entities.Task): Boolean {
            return oldItem == newItem
        }
    }
}