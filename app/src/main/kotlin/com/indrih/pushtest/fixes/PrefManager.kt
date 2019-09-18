package com.indrih.pushtest.fixes

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

internal class PrefManager(context: Context) {
    private val autoStartPropName = "auto_start"
    private val batteryOptimizationPropName = "battery_optimization"

    private val pref: SharedPreferences by lazy {
        context.getSharedPreferences("run_status", Context.MODE_PRIVATE)
    }

    var isFirstRunAutoStartDialod: Boolean
        get() = pref.getBoolean(autoStartPropName, true)
        set(value) = pref.edit { putBoolean(autoStartPropName, value) }

    var isFirstRunBatteryOptimizationDialog: Boolean
        get() = pref.getBoolean(batteryOptimizationPropName, true)
        set(value) = pref.edit { putBoolean(batteryOptimizationPropName, value) }
}