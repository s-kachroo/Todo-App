package com.example.todoapp.ui.todo

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.todoapp.R
import com.example.todoapp.adapter.todo.ToDoItemAdapter
import com.example.todoapp.data.todo.ToDo
import com.example.todoapp.data.todo.ToDoDatabase
import com.example.todoapp.databinding.FragmentToDoBinding
import com.example.todoapp.viewmodel.todo.ToDoViewModel
import com.example.todoapp.viewmodel.todo.ToDoViewModelFactory
import com.google.android.material.snackbar.Snackbar


/**
 * ToDoFragment displays a list of items and allows the user to add new items.
 * It interacts with ToDoViewModel to observe and update data.
 */
class ToDoFragment : Fragment() {
    // Binding property to access views safely.
    private var _binding: FragmentToDoBinding? = null
    private val binding get() = _binding!!

    // Lazy initialization of the ViewModel using a factory for dependency injection.
    private val viewModel: ToDoViewModel by viewModels {
        ToDoViewModelFactory(ToDoDatabase.getInstance(requireContext()).todoDao)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using Data Binding
        _binding = FragmentToDoBinding.inflate(inflater, container, false).apply {
            this.viewModel = this@ToDoFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setActionBarTitle()
        setupListAdapter()
        observeViewModel()
        setupListeners()
    }

    /**
     * Sets the ActionBar title.
     */
    private fun setActionBarTitle() {
        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.todo_list)
    }

    /**
     * Setup listeners to update the ViewModel.
     */
    private fun setupListeners() {
        binding.todoTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }


            override fun afterTextChanged(s: Editable?) {
                viewModel.setNewToDoTitle(s.toString())
            }
        })
    }

    /**
     * Initializes the adapter for the RecyclerView and sets up item click handling.
     */
    private fun setupListAdapter() {
        val adapter = ToDoItemAdapter { todoId ->
            viewModel.onTodoItemClicked(todoId)
        }
        binding.todosList.adapter = adapter
    }

    /**
     * Sets up LiveData observers to update UI in response to data changes.
     */
    private fun observeViewModel() {
        viewModel.todos.observe(viewLifecycleOwner) { todos ->
            todos?.let {
                updateUI(it)
            }
        }

        viewModel.navigateToTodo.observe(viewLifecycleOwner) { todoId ->
            todoId?.let {
                navigateToEditTodoFragment(it)
            }
        }

        viewModel.newToDoTitle.observe(viewLifecycleOwner) { newTitle ->
            if (binding.todoTitle.text.toString() != newTitle) {
                binding.todoTitle.setText(newTitle)
            }
        }

        viewModel.errorEvent.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                showErrorSnackbar(it)
            }
        }

        viewModel.resetFocusEvent.observe(viewLifecycleOwner) {
            hideKeyboard()
            clearFocus()
        }
    }

    /**
     * Updates the visibility of the RecyclerView and the "no todos" text view.
     */
    private fun updateUI(todos: List<ToDo>) {
        with(binding) {
            todosList.visibility = if (todos.isEmpty()) View.GONE else View.VISIBLE
            noTodosText.visibility = if (todos.isEmpty()) View.VISIBLE else View.GONE
            (todosList.adapter as ToDoItemAdapter).submitList(todos)
        }
    }

    /**
     * Navigates to the EditTodoFragment when a item is clicked.
     */
    private fun navigateToEditTodoFragment(todoId: Long) {
        val action = ToDoFragmentDirections.actionTodosFragmentToEditTodoFragment(todoId)
        findNavController().navigate(action)
        viewModel.onTodoItemNavigated()
    }

    /**
     * Shows a Snackbar with an error message.
     */
    private fun showErrorSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    /**
     * Clears editText Focus.
     */
    private fun clearFocus() {
        binding.todoTitle.clearFocus()
    }

    /**
     * Hides the soft keyboard.
     */
    private fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        val currentFocusedView = activity?.currentFocus
        currentFocusedView?.let {
            imm?.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    /**
     * Inflates the options menu and initializes the search view.
     *
     * @param menu The options menu in which you place your items.
     * @param inflater The menu inflater to be used to inflate the menu.
     */
    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.task_menu, menu)

        // Retrieve the SearchView and set up the search logic.
        val searchMenuItem = menu.findItem(R.id.action_search)
        val searchView = searchMenuItem.actionView as? SearchView ?: return
        setupSearchView(searchView)
    }

    /**
     * Sets up the SearchView's query text listeners.
     *
     * @param searchView The SearchView obtained from the menu item.
     */
    private fun setupSearchView(searchView: SearchView) {
        // Set listeners for search query text changes and query submission.
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Optionally handle query submission.
                // Could be used for search query validation or to hide the keyboard.
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Perform the search operation based on the new text input.
                newText?.let { runQuery(it) }
                return true
            }
        })
    }

    /**
     * Performs the search operation and updates the UI based on the query.
     *
     * @param query The search query to execute.
     */
    private fun runQuery(query: String) {
        // Format the query for SQL LIKE operation and observe changes.
        val searchQuery = "%$query%"
        viewModel.searchTodo(searchQuery).observe(viewLifecycleOwner) { todos ->
            // Update the list based on the search result.
            todos?.let { updateTodoList(it) }
        }
    }

    /**
     * Updates the list adapter with new data.
     *
     * @param todos The list of items to be displayed.
     */
    private fun updateTodoList(todos: List<ToDo>) {
        // Safely cast the adapter to ToDoItemAdapter and submit the new list.
        (binding.todosList.adapter as? ToDoItemAdapter)?.submitList(todos)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Avoid memory leaks by releasing the binding reference
        _binding = null
    }
}