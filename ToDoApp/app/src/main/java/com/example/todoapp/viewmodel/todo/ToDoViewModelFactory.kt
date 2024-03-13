package com.example.todoapp.viewmodel.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.data.todo.ToDoDao

class ToDoViewModelFactory(private val dao: ToDoDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ToDoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return ToDoViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}