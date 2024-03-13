package com.example.todoapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
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
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

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
        val todoId = 1L
        todosViewModel.onTodoItemClicked(todoId)
        assertThat(todosViewModel.navigateToTodo.value).isEqualTo(todoId)
    }

    @Test
    fun `onTodoItemNavigated resets the navigateToTodo value to null`() {
        todosViewModel.onTodoItemClicked(1L)
        todosViewModel.onTodoItemNavigated()
        assertThat(todosViewModel.navigateToTodo.value).isNull()
    }

    @Test
    fun `deleteAllTodos calls deleteAll method in DAO once`() = runTest {
        coEvery { testDao.deleteAll() } just Runs
        launch { todosViewModel.deleteAllTodos() }
        advanceUntilIdle()
        coVerify(exactly = 1) { testDao.deleteAll() }
    }

    @Test
    fun `addTodo successfully resets newToDoTitle LiveData`() = runTest {
        coEvery { testDao.insert(any()) } just Runs
        todosViewModel.setNewToDoTitle("New Task")
        launch { todosViewModel.addTodo() }
        advanceUntilIdle()
        assertThat(todosViewModel.newToDoTitle.getOrAwaitValue()).isEmpty()
    }

    @Test
    fun `searchTodo posts error message on exception`() = runTest {
        val errorMessage = "Search error"
        val searchQuery = "query"
        every { testDao.search(searchQuery) } throws Exception(errorMessage)
        val resultLiveData = todosViewModel.searchTodo(searchQuery)
        val errorValue = todosViewModel.errorEvent.getOrAwaitValue()
        assertThat(errorValue).contains(errorMessage)
        assertThat(resultLiveData.getOrAwaitValue()).isEmpty()
    }

    @Test
    fun `addTodo with empty title posts error message`() = runTest {
        todosViewModel.setNewToDoTitle("")
        todosViewModel.addTodo()
        val errorValue = todosViewModel.errorEvent.getOrAwaitValue()
        assertThat(errorValue).isNotEmpty()
    }

    @Test
    fun `deleteAllTodos handles exceptions`() = runTest {
        val errorMessage = "Database error"
        coEvery { testDao.deleteAll() } throws Exception(errorMessage)
        launch { todosViewModel.deleteAllTodos() }
        advanceUntilIdle()
        val errorValue = todosViewModel.errorEvent.getOrAwaitValue()
        assertThat(errorValue).contains(errorMessage)
    }

    /**
     * Gets the value of a LiveData safely.
     */
    fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = 2, timeUnit: TimeUnit = TimeUnit.SECONDS, afterObserve: () -> Unit = {}
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(value: T) {
                data = value
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }
        this.observeForever(observer)
        try {
            afterObserve.invoke()
            if (!latch.await(time, timeUnit)) {
                throw TimeoutException("LiveData value was never set.")
            }
        } finally {
            this.removeObserver(observer)
        }
        @Suppress("UNCHECKED_CAST") return data as T
    }
}