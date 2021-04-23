package com.jhdroid.utils.listener

import android.os.SystemClock
import android.view.View

abstract class OnIntervalClickListener : View.OnClickListener {
    private var mLastClickTime: Long = 0

    abstract fun onIntervalClick(v: View?)

    override fun onClick(v: View) {
        val currentClickTime = SystemClock.uptimeMillis()
        val elapsedTime = currentClickTime - mLastClickTime
        mLastClickTime = currentClickTime

        // 중복 클릭으로 판단
        if (elapsedTime <= MIN_CLICK_INTERVAL) {
            return
        }
        onIntervalClick(v)
    }

    companion object {
        private const val MIN_CLICK_INTERVAL: Long = 2000
    }
}