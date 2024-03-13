package com.example.todoapp.ui

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.todoapp.R

/**
 * MainActivity serves as the primary activity of the application.
 * It hosts the NavController for managing app navigation and also handles back press actions
 * to navigate within the app or exit.
 */
class MainActivity : AppCompatActivity() {
    // Property for NavController to manage app navigation.
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        setupNavController()
        handleBackPress()
    }

    /**
     * Initializes the NavController from the NavHostFragment.
     */
    private fun setupNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navController = navHostFragment.navController
    }

    /**
     * Sets up back press handling to customize navigation or exit behavior.
     */
    private fun handleBackPress() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // If the current destination is the ToDoFragment, finish the Activity to exit.
                // Otherwise, allow the default back press behavior to take place.
                if (navController?.currentDestination?.id == R.id.todosFragment) {
                    finish() // Close the application
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed() // Delegate to the system default behavior
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }
}