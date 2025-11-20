package com.ejemplo.todotrack

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment

class AddTaskDialogFragment : DialogFragment() {

    private var onTaskAddedListener: ((String) -> Unit)? = null

    fun setOnTaskAddedListener(listener: (String) -> Unit) {
        onTaskAddedListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val editText = EditText(requireContext()).apply {
            hint = getString(R.string.new_task)
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.add_task))
            .setView(editText)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                val title = editText.text.toString().trim()
                if (title.isNotEmpty()) {
                    onTaskAddedListener?.invoke(title)
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .create()
    }
}