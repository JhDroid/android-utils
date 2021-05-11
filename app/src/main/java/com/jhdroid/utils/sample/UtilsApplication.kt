package com.jhdroid.utils.sample

import android.app.Application
import com.jhdroid.utils.security.AES256Util

class UtilsApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        AES256Util.initialize(this)
    }
}