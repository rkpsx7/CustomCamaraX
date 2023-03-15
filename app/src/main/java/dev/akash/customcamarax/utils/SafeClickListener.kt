package dev.akash.customcamarax.utils

import android.os.SystemClock
import android.view.View
import kotlin.math.abs


abstract class SafeClickListener(private val minimumIntervalMillis: Long = 1500) : View.OnClickListener {
    private var previousClickTimestamp = 0L

    abstract fun onSafeClick(v: View?)

    override fun onClick(clickedView: View) {
        val currentTimestamp: Long = SystemClock.uptimeMillis()
        if (abs(currentTimestamp - previousClickTimestamp) > minimumIntervalMillis) {
            onSafeClick(clickedView)
            previousClickTimestamp = currentTimestamp
        }
    }
}