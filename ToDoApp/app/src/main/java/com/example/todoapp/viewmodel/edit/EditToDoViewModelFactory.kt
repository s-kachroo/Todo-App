package com.example.todoapp.viewmodel.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.data.todo.ToDoDao

class EditToDoViewModelFactory(private val todoId: Long, private val dao: ToDoDao) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(EditToDoViewModel::class.java)) {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
        try {
            return modelClass.getConstructor(Long::class.java, ToDoDao::class.java)
                .newInstance(todoId, dao) as T
        } catch (e: NoSuchMethodException) {
            throw IllegalArgumentException("Invalid constructor for ${modelClass.simpleName}", e)
        } catch (e: Exception) {
            throw RuntimeException("Cannot create an instance of ${modelClass.simpleName}", e)
        }
    }
}
