package com.jhdroid.utils.sample

import android.view.View
import androidx.databinding.BindingAdapter
import com.jhdroid.utils.listener.OnIntervalClickListener

@BindingAdapter("android:onIntervalClick")
fun onIntervalClick(view: View, listener: View.OnClickListener) {
    view.setOnClickListener(OnIntervalClickListener(listener))
}