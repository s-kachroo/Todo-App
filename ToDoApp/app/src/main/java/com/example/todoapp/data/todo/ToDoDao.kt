package com.example.todoapp.data.todo

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
/**
 * Data Access Object for the table.
 *
 * Defines methods for using the class with Room, including insert, update,
 * delete operations, and queries.
 */
interface ToDoDao {

    /**
     * Inserts a new item into the todos_table.
     *
     * @param todo The item to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: ToDo)

    /**
     * Updates an existing item in the todos_table.
     *
     * @param todo item to update.
     */
    @Update
    suspend fun update(todo: ToDo)

    /**
     * Deletes a specific item from the todos_table.
     *
     * @param todo The item to delete.
     */
    @Delete
    suspend fun delete(todo: ToDo)

    /**
     * Deletes all items from the todos_table.
     */
    @Query("DELETE FROM todos_table")
    suspend fun deleteAll()

    /**
     * Retrieves a item by its ID.
     *
     * @param todoId The ID of the item.
     * @return LiveData containing the item.
     */
    @Query("SELECT * FROM todos_table WHERE id = :todoId")
    fun getById(todoId: Long): LiveData<ToDo>

    /**
     * Retrieves all items from the todos_table, ordered by their completion status,
     * then by completedAt timestamp in descending order, and finally by ID in descending order.
     *
     * @return LiveData containing the list of items.
     */
    @Query("SELECT * FROM todos_table ORDER BY completed ASC, completedAt DESC, id DESC")
    fun getAll(): LiveData<List<ToDo>>

    /**
     * Searches items by their title, matching against a search query.
     * Results are ordered by their completion status, then by completedAt timestamp in descending
     * order, and finally by ID in descending order.
     *
     * @param searchQuery The query to match against item titles.
     * @return LiveData containing the list of matching items.
     */
    @Query("SELECT * FROM todos_table WHERE title LIKE :searchQuery ORDER BY completed ASC, completedAt DESC, id DESC")
    fun search(searchQuery: String): LiveData<List<ToDo>>
}