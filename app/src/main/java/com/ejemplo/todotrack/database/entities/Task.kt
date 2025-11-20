package com.ejemplo.todotrack.database.entities

import java.util.*

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val completed: Boolean = false,
    val createdAt: Date = Date()
)