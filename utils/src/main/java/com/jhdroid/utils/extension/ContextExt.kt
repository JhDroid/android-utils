package com.jhdroid.utils.extension

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf

fun Context.startActivity(target: Activity) {
    startActivity(Intent(this, target::class.java))
}

fun Context.startActivityWithData(target: Activity, vararg extraPair: Pair<String, Any>) {
    Intent(this, target::class.java).run {
        putExtras(bundleOf(*extraPair))
        startActivity(this)
    }
}

fun Context.toast(message: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, duration).show()
}

fun Context.getCompatColor(@ColorRes colorResId: Int) =
    ContextCompat.getColor(this, colorResId)

fun Context.getCompatDrawable(drawableId: Int) =
    ContextCompat.getDrawable(this, drawableId)

