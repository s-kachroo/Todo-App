# Todo Application

## Description

This application allows users to maintain a simple TODO list. Users can add/edit/delete tasks to the list, and mark them as DONE or NOT DONE. The app is designed with simplicity in mind, requiring no network connection or external dependencies.

## Architecture

This project follows the Model-View-ViewModel (MVVM) architecture pattern, enhancing separation of concerns, maintainability, and testability. Here's a breakdown of the architecture components and their roles within the application.

### Model

The Model layer embodies the data and business logic of the application. It handles network or database operations.

- **Entities:** 
    - `ToDo.kt` - Represents a ToDo item, encapsulating properties like id, title, and completion status.
- **Database:** 
    - `ToDoDatabase.kt` - Defines the Room database instance.
    - `ToDoDao.kt` - Data Access Object for accessing the database operations.

### View

The View layer renders the UI and forwards user interactions to the ViewModel. It observes LiveData from the ViewModel to react to data changes.

- **Activities/Fragments:** Act as controllers to render Views and handle user actions.
    - `MainActivity.kt` - Hosts navigation components and sets up the toolbar.
    - `ToDoFragment.kt` - Shows the list of ToDo items and allows adding new ones.
    - `EditToDoFragment.kt` - Enables editing an existing ToDo item.
- **Adapters:** Connect UI components with data.
    - `ToDoItemAdapter.kt` - Binds ToDo items to a RecyclerView.

### ViewModel

The ViewModel holds UI data, handles business logic, and abstracts the View layer from the Model. It exposes LiveData for the View to observe.

- **ViewModels:**
    - `ToDoViewModel.kt` - Manages UI data for the ToDo list and operations like adding items.
    - `EditToDoViewModel.kt` - Manages editing of ToDo items.

### Utilities and Supporting Components

- `SingleLiveEvent.kt` - A LiveData class designed for events like navigation which are meant to be consumed only once.
- `BindingAdapters.kt` - Contains custom binding adapter functions for Data Binding in XML layouts.

### Data Binding and Navigation

The project utilizes Data Binding to reduce boilerplate by binding UI components in layouts directly to data sources. The Navigation component manages UI navigation.

## Features and Considerations

- The app starts with an empty TODO list
- Users can add new TODO tasks via a text input field
- Tasks can be marked as DONE or NOT DONE
- Tasks can be edited or deleted
- The app is designed for ease of use and future scalability
- Implement persistent storage for TODO items
- Tasks are sorted based if they are marked DONE or NOT DONE
- All the tasks can be deleted together (Development Phase)
- Users can search among the TODO tasks (Development Phase)

## Build Instructions

You can download the apk file on your local device.

or

To build this project:
1. Clone the repository.
2. Open the project in Android Studio.
3. Run the app on an emulator or a physical device.

## Tech Stack

The project leverages a variety of technologies, libraries, and architectural principles to ensure a robust, maintainable, and user-friendly application. Here's an overview:

* Kotlin - Primary programming language, offering concise syntax and modern language features.
* Material Design - Design system for immersive user interfaces that follow modern design principles.
* Room Database - Abstraction layer over SQLite, simplifying database access and ensuring type safety.
* Coroutines - Managing background tasks more efficiently and in a more readable way than traditional callbacks.
* Navigation Component - Facilitating in-app navigation, ensuring a consistent and predictable user experience.
* AndroidX - The suite of libraries, tools, and guidance for Android development, ensuring backward compatibility and forward innovation.
* Data Binding & View Binding - Reducing boilerplate code by binding UI components directly to data sources and ensuring null-safe view access.
* SOLID Principles - Design principles intended to make software designs more understandable, flexible, and maintainable.
* MVVM Architecture Pattern - Separating the business logic and data presentation from the UI, promoting a cleaner codebase and facilitating easier testing.

### Additional Technologies:

* LiveData - Lifecycle-aware data holder with the observer pattern, ensuring UI components only observe relevant data changes.
* Lifecycle Components - Handling lifecycle events in a clean and decoupled way, ensuring proper management of Android lifecycle.
* Repository Pattern - Providing a clean API for data access to the rest of the application, abstracting the sources of data.
* Unit Testing & UI Testing - Leveraging JUnit, Espresso, and MockK for robust testing practices.
* Git - For version control, illustrating the use of branching, merging, and pull requests in collaborative development environments.

## Future Enhancements
* More Unit Tests 
* Better UIs

# Purpose and Problem Solving
This application helps the users to manage their daily tasks more efficiently. In today's fast-paced world, it's easy to get overwhelmed by tasks complexity, so our application provides a simple yet powerful tool for users to organize their tasks.

By providing a comprehensive solution to task management, our app not only helps users stay organized but also promotes a good work-life balance.

The app solves some key problems faced by anyone:

- Organization: It allows users to capture tasks quickly and organize them in a meaningful way, reducing mental clutter and increasing focus.
- Prioritization: Users can mark tasks as important or urgent, helping to prioritize tasks that need immediate attention, ensuring that deadlines are met.
- Tracking Progress: By marking tasks as completed, users can track their progress through their to-do list, offering a sense of accomplishment and clarity on what's left to be done.
