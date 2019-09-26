package com.indrih.pushtest.base

import android.app.Application
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
        Crashlytics.setUserEmail("indrih17@gmail.com")
        Crashlytics.setUserIdentifier("indrih17")
    }
}