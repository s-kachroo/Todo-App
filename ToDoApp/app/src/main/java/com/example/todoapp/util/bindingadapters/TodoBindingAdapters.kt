package com.example.todoapp.util.bindingadapters

import android.graphics.Paint
import android.widget.TextView
import androidx.databinding.BindingAdapter

/**
 * Binding adapter used to strike through the text of a TextView based on a condition.
 * This is commonly used for to-do lists or task applications where completed items
 * are visually distinguished by striking through their text.
 *
 * @param view The TextView to modify.
 * @param completed The condition that determines if the strikethrough effect should be applied.
 */
@BindingAdapter("textStrikethrough")
fun setTextStrikethrough(view: TextView, completed: Boolean) {
    if (completed) {
        // Apply a strikethrough effect if the condition is true.
        view.paintFlags = view.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    } else {
        // Remove the strikethrough effect if the condition is false.
        // Use bitwise AND with the inverse of STRIKE_THRU_TEXT_FLAG to clear it.
        view.paintFlags = view.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }
}