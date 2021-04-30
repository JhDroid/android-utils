package com.jhdroid.utils.listener

import android.os.SystemClock
import android.view.View

class OnIntervalClickListener(
    private val listener: View.OnClickListener,
    private val interval: Long = 2000
) : View.OnClickListener {
    private var lastClickTime: Long = 0

    override fun onClick(v: View) {
        val currentClickTime = SystemClock.uptimeMillis()
        val elapsedTime = currentClickTime - lastClickTime
        lastClickTime = currentClickTime

        // 중복 클릭으로 판단
        if (elapsedTime <= interval) {
            return
        }

        listener.onClick(v)
    }
}