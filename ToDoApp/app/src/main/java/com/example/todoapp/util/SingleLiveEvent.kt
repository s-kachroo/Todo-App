package com.example.todoapp.util

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A lifecycle-aware observable that sends only new updates after subscription,
 * used for events like navigation and Snackbar messages.
 * This avoids a common problem with events: on configuration change (like rotation)
 * an update can be emitted if the observer is active. This LiveData only calls the observable
 * if there's an explicit call to setValue() or call().
 *
 * Note: Only one observer is going to be notified of changes.
 */
class SingleLiveEvent<T> : MutableLiveData<T>() {
    private val mPending = AtomicBoolean(false)

    /**
     * Observes the [SingleLiveEvent] respecting the lifecycle of the [owner].
     * The event is only triggered if [mPending] is set and then immediately reset.
     *
     * @param owner The LifecycleOwner which controls the observer
     * @param observer The observer that will receive the events
     */
    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        if (hasActiveObservers()) {
            Log.w(TAG, "Multiple observers registered but only one will be notified of changes.")
        }

        // Observe the internal MutableLiveData
        super.observe(owner) { t ->
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        }
    }

    /**
     * Sets the value and indicates that it's pending.
     * Any observer will be notified of the change only if it's the first change since last observation.
     *
     * @param t The new value
     */
    @MainThread
    override fun setValue(t: T?) {
        mPending.set(true)
        super.setValue(t)
    }

    companion object {
        private const val TAG = "SingleLiveEvent"
    }
}