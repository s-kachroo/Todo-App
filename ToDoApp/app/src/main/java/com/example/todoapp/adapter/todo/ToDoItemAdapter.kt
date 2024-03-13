package com.example.todoapp.adapter.todo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.data.todo.ToDo
import com.example.todoapp.databinding.TodoItemBinding

/**
 * Adapter for the RecyclerView in ToDoFragment. Displays items and handles click events.
 *
 * @property clickListener A lambda function to be called when an item is clicked, passing the ID of the clicked.
 */
class ToDoItemAdapter(private val clickListener: (todoId: Long) -> Unit) :
    ListAdapter<ToDo, ToDoItemAdapter.ToDoItemViewHolder>(DiffCallback) {
    /**
     * Provides comparison functionality for determining whether items and contents are the same.
     */
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ToDo>() {
            override fun areItemsTheSame(oldItem: ToDo, newItem: ToDo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ToDo, newItem: ToDo): Boolean {
                return oldItem == newItem
            }
        }
    }

    /**
     * Creates new views (invoked by the layout manager).
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoItemViewHolder =
        ToDoItemViewHolder.inflateFrom(parent)

    /**
     * Replaces the contents of a view (invoked by the layout manager).
     */
    override fun onBindViewHolder(holder: ToDoItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }

    /**
     * ViewHolder for items. Contains binding logic and interaction handling.
     */
    class ToDoItemViewHolder(private val binding: TodoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun inflateFrom(parent: ViewGroup): ToDoItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TodoItemBinding.inflate(layoutInflater, parent, false)
                return ToDoItemViewHolder(binding)
            }
        }

        /**
         * Binds a item to this ViewHolder, setting up the displayed data and click listener.
         */
        fun bind(item: ToDo, clickListener: (todoId: Long) -> Unit) {
            binding.todo = item
            binding.root.setOnClickListener { clickListener(item.id) }
        }
    }
}