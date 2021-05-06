package com.jhdroid.utils.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.provider.Settings
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
}