package com.example.todoapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.todoapp.data.todo.ToDo
import com.example.todoapp.data.todo.ToDoDao
import com.example.todoapp.viewmodel.todo.ToDoViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ToDoViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var testDao: ToDoDao
    private lateinit var todosViewModel: ToDoViewModel
    private val emptyLiveDataList = MutableLiveData<List<ToDo>>()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        every { testDao.getAll() } returns emptyLiveDataList
        todosViewModel = ToDoViewModel(testDao)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addTodo calls insert method in DAO once`() = runTest {
        coEvery { testDao.insert(any()) } just Runs
        todosViewModel.setNewToDoTitle("NYT Task")
        launch { todosViewModel.addTodo() }
        advanceUntilIdle()
        coVerify(exactly = 1) { testDao.insert(any()) }
    }

    @Test
    fun `onTodoItemClicked sets todoId LiveData`() {
        // Given
        val todoId = 1L

        // When
        todosViewModel.onTodoItemClicked(todoId)

        // Then
        assertThat(todosViewModel.navigateToTodo.value).isEqualTo(todoId)
    }

    @Test
    fun `onTodoItemNavigated resets the navigateToTodo value to null`() {
        // Given
        todosViewModel.onTodoItemClicked(1L)

        // When
        todosViewModel.onTodoItemNavigated()

        // Then
        assertThat(todosViewModel.navigateToTodo.value).isNull()
    }

}