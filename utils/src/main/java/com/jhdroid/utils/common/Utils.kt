package com.jhdroid.utils.common

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.provider.Settings
import android.view.View
import android.webkit.WebView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.jhdroid.utils.BuildConfig
import java.io.UnsupportedEncodingException
import java.util.*

object Utils {
    @SuppressLint("HardwareIds")
    fun getDeviceUUID(context: Context): String? {
        var uuid: UUID? = null
        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        try {
            uuid = if ("9774d56d682e549c" != androidId) {
                UUID.nameUUIDFromBytes(androidId.toByteArray(charset("UTF-8")))
            } else {
                UUID.randomUUID()
            }
        } catch (e: UnsupportedEncodingException) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }

        return uuid?.toString()
    }

    fun getAppIcon(context: Context, packageName: String): Drawable? {
        return try {
            val packageManager = context.packageManager
            val appInfo = packageManager.getApplicationInfo(packageName, 0)

            appInfo.loadIcon(packageManager)
        }
        catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }

            null
        }
    }

    fun getAppNameByPackage(context: Context, packageName: String): String? {
        return try {
            val packageManager = context.packageManager
            val appInfo = packageManager.getApplicationInfo(packageName, 0)

            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }

            null
        }
    }

    @SuppressLint("WebViewApiAvailability")
    fun checkWebView(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val currentWebViewPackage = WebView.getCurrentWebViewPackage()
            currentWebViewPackage != null
        } else {
            try {
                context.packageManager.getPackageInfo("com.google.android.webview", PackageManager.GET_META_DATA)

                true
            } catch (e1: PackageManager.NameNotFoundException) {
                try {
                    context.packageManager.getPackageInfo("com.android.webview", PackageManager.GET_META_DATA)

                    true
                } catch (e2: PackageManager.NameNotFoundException) {
                    false
                }
            }
        }
    }

    fun setStatusBarColor(context: Context, @ColorRes resId: Int) {
        val activity: Activity

        if (context is Activity) {
            activity = context
        } else {
            return
        }

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                val window = activity.window
                window.statusBarColor = ContextCompat.getColor(context, resId)
            }
            else -> {
                activity.titleColor = ContextCompat.getColor(context, resId)
            }
        }
    }

    fun dp2px(view: View, dp: Float): Float {
        return dp * view.resources.displayMetrics.density + 0.5f
    }

    fun sp2px(view: View, sp: Float): Float {
        return sp * view.resources.displayMetrics.scaledDensity
    }
}