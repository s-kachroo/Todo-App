package com.example.todoapp.viewmodel.edit

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.todo.ToDoDao
import com.example.todoapp.util.SingleLiveEvent
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing UI-related data for editing a item.
 * It interacts with the database through a DAO (Data Access Object) to perform update and delete operations.
 *
 * @param todoId The ID of the item to be edited.
 * @property dao The Data Access Object for performing database operations.
 */
class EditToDoViewModel(todoId: Long, private val dao: ToDoDao) : ViewModel() {
    // LiveData holding the item fetched by ID. Automatically updates the UI upon data change.
    val todo = dao.getById(todoId)

    // Backing property for navigation events back to the list.
    private val _navigateToList = SingleLiveEvent<Boolean>()

    // Exposed LiveData for navigation events.
    val navigateToList: LiveData<Boolean> get() = _navigateToList

    // Backing property for error messages.
    private val _errorEvent = SingleLiveEvent<String>()

    // Exposed LiveData for error messages to be observed by the UI.
    val errorEvent: LiveData<String> get() = _errorEvent

    /**
     * Updates the current item in the database.
     * Sets the completion time based on the completed status.
     */
    fun updateTodoItem() {
        viewModelScope.launch {
            try {
                val currentTodo = todo.value
                if (currentTodo != null) {
                    Log.d("UpdateTodo", "Attempting to update: $currentTodo")
                    // Optionally set the completion date based on the completed status.
                    currentTodo.completedAt =
                        if (currentTodo.completed) System.currentTimeMillis() else null
                    dao.update(currentTodo)
                    // Signal successful update and navigation
                    _navigateToList.value = true
                } else {
                    _errorEvent.value = "Todo item not found."
                }
            } catch (e: Exception) {
                _errorEvent.value = "Failed to update todo item: ${e.message}"
            }
        }
    }

    /**
     * Deletes the current item from the database.
     */
    fun deleteTodoItem() {
        viewModelScope.launch {
            try {
                val currentTodo = todo.value
                if (currentTodo != null) {
                    Log.d("DeleteTodo", "Attempting to delete: $currentTodo")
                    dao.delete(currentTodo)
                    // Signal successful deletion and navigation
                    _navigateToList.value = true
                } else {
                    _errorEvent.value = "Todo item not found."
                }
            } catch (e: Exception) {
                _errorEvent.value = "Failed to delete todo item: ${e.message}"
            }
        }
    }

    /**
     * Resets navigation event after navigation to the list has been completed.
     */
    fun onNavigatedToList() {
        _navigateToList.value = false
    }
}