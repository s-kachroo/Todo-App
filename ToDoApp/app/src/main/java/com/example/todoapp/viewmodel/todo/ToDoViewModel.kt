package com.example.todoapp.viewmodel.todo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.todo.ToDoDao
import com.example.todoapp.data.todo.ToDo
import com.example.todoapp.util.SingleLiveEvent
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing UI-related data for the screen.
 * It interacts with the database through a DAO (Data Access Object) to perform CRUD operations.
 *
 * @property dao The Data Access Object for performing database operations.
 */
class ToDoViewModel(private val dao: ToDoDao) : ViewModel() {
    // LiveData holding the list of all todos. Automatically updates the UI upon data change.
    val todos = dao.getAll()

    // Backing property for new title entered by the user.
    private val _newToDoTitle = MutableLiveData<String>()

    // Exposed LiveData for new title, ensuring encapsulation.
    val newToDoTitle: LiveData<String> get() = _newToDoTitle

    // Backing property for navigation event to a specific item.
    private val _navigateToTodo = SingleLiveEvent<Long?>()

    // Exposed LiveData for item navigation events.
    val navigateToTodo: LiveData<Long?> get() = _navigateToTodo

    // Backing property for error messages.
    private val _errorEvent = SingleLiveEvent<String>()

    // Exposed LiveData for error messages to be observed by the UI.
    val errorEvent: LiveData<String> get() = _errorEvent

    // Backing property for hide keyboard event.
    private val _resetFocusEvent = SingleLiveEvent<Unit>()

    // Exposed LiveData for hide keyboard events.
    val resetFocusEvent: LiveData<Unit> get() = _resetFocusEvent

    /**
     * Updates the new title LiveData with user input.
     *
     * @param title The new title entered by the user.
     */
    fun setNewToDoTitle(title: String) {
        _newToDoTitle.value = title
    }

    /**
     * Adds a new item to the database.
     * Resets the input field and requests keyboard hide upon successful insertion.
     */
    fun addTodo() {
        _newToDoTitle.value?.let { title ->
            if (title.isBlank()) {
                _errorEvent.value = "The todo title cannot be empty."
                return
            }
            viewModelScope.launch {
                try {
                    val todo = ToDo(title = title)
                    dao.insert(todo)
                    _newToDoTitle.postValue("") // Reset title after insertion
                    _resetFocusEvent.postValue(Unit) // Signal to reset focus of the keyboard
                } catch (e: Exception) {
                    _errorEvent.postValue("Failed to add todo item: ${e.localizedMessage}")
                }
            }
        } ?: _errorEvent.postValue("The todo content is missing.")
    }

    /**
     * Deletes all items from the database.
     */
    fun deleteAllTodos() {
        viewModelScope.launch {
            try {
                dao.deleteAll()
            } catch (e: Exception) {
                _errorEvent.value = "Failed to delete all todo items: ${e.localizedMessage}"
            }
        }
    }

    /**
     * Search` all items from the database.
     */
    fun searchTodo(searchQuery: String): LiveData<List<ToDo>> {
        return try {
            dao.search(searchQuery)
        } catch (e: Exception) {
            _errorEvent.value = "Failed to search todo items: ${e.localizedMessage}"
            MutableLiveData(emptyList())
        }
    }


    /**
     * Handles user action of clicking on a item by posting the item's ID for navigation.
     *
     * @param todoId The ID of the clicked item.
     */
    fun onTodoItemClicked(todoId: Long) {
        _navigateToTodo.value = todoId
    }

    /**
     * Resets navigation event after navigating to the item detail screen.
     */
    fun onTodoItemNavigated() {
        _navigateToTodo.value = null
    }
}