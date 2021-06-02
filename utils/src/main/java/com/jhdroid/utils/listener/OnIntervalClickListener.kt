package com.jhdroid.utils.listener

import android.view.View

class OnIntervalClickListener(
    private val listener: View.OnClickListener,
    private val interval: Long = 1000
) : View.OnClickListener {
    private var isClickable = true

    override fun onClick(v: View) {
        if (isClickable) {
            isClickable = false
            listener.onClick(v)

            v.postDelayed({
                isClickable = true
            }, interval)
        }
    }
}