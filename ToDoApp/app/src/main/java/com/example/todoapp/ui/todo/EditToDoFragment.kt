package com.example.todoapp.ui.todo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.todoapp.R
import com.example.todoapp.data.todo.ToDoDatabase
import com.example.todoapp.databinding.FragmentEditToDoBinding
import com.example.todoapp.viewmodel.edit.EditToDoViewModel
import com.example.todoapp.viewmodel.edit.EditToDoViewModelFactory
import com.google.android.material.snackbar.Snackbar

/**
 * Fragment for editing an existing item.
 * Utilizes data binding for UI updates and interacts with the ViewModel for data processing.
 */
class EditToDoFragment : Fragment() {
    // Binding property to safely access the views.
    private var _binding: FragmentEditToDoBinding? = null
    private val binding get() = _binding!!

    // Lazily initialize the ViewModel using parameters from the fragment's arguments and a factory.
    private val viewModel: EditToDoViewModel by viewModels {
        val todoId = EditToDoFragmentArgs.fromBundle(requireArguments()).todoId
        val dao = ToDoDatabase.getInstance(requireContext()).todoDao
        EditToDoViewModelFactory(todoId, dao)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using Data Binding and set the ViewModel.
        _binding = FragmentEditToDoBinding.inflate(inflater, container, false).apply {
            this.viewModel = this@EditToDoFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupActionBarTitle()
        observeViewModelEvents()
    }

    /**
     * Sets the ActionBar title to indicate the editing context.
     */
    private fun setupActionBarTitle() {
        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.edit_todo)
    }

    /**
     * Observes events from the ViewModel, including navigation commands and error messages.
     */
    private fun observeViewModelEvents() {
        viewModel.navigateToList.observe(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate) handleNavigationToList()
        }

        viewModel.errorEvent.observe(viewLifecycleOwner) { errorMessage ->
            showErrorSnackbar(errorMessage)
        }
    }

    /**
     * Handles navigation back to the list fragment after an operation is completed.
     */
    private fun handleNavigationToList() {
        // Check current destination to prevent double navigation due to LiveData re-trigger.
        val currentDestination = findNavController().currentDestination?.id
        if (currentDestination == R.id.editTodoFragment) {
            val action = EditToDoFragmentDirections.actionEditTodoFragmentToTodosFragment()
            findNavController().navigate(action)
            viewModel.onNavigatedToList()
        }
    }

    /**
     * Shows an error message using Snackbar.
     *
     * @param errorMessage The error message to display.
     */
    private fun showErrorSnackbar(errorMessage: String) {
        Snackbar.make(requireView(), errorMessage, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Avoid memory leaks by releasing the binding reference when the view is destroyed.
        _binding = null
    }
}