package com.jhdroid.utils.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.webkit.WebView
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

    fun getAppNameByPackage(context: Context, packageName: String): String {
        return try {
            val packageManager = context.packageManager
            val appInfo = packageManager.getApplicationInfo(packageName, 0)

            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }

            "unknown"
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
                    // 안드로이드 시스템 웹뷰를 사용하지 않으면 기본 웹뷰를 참조함
                    context.packageManager.getPackageInfo("com.android.webview", PackageManager.GET_META_DATA)
                    true
                } catch (e2: PackageManager.NameNotFoundException) {
                    false
                }
            }
        }
    }
}