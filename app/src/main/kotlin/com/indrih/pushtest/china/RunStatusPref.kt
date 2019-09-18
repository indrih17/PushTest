package com.indrih.pushtest.china

import android.content.Context
import androidx.core.content.edit

object RunStatusPref {
    private const val propName = "isFirstRunBadDevice"

    fun set(context: Context, isFirstRun: Boolean) =
        getPrivatePref(context).edit {
            putBoolean(propName, isFirstRun)
        }

    fun get(context: Context): Boolean =
        getPrivatePref(context).getBoolean(propName, true)

    @Suppress("NOTHING_TO_INLINE")
    private inline fun getPrivatePref(context: Context) =
        context.getSharedPreferences("pref", Context.MODE_PRIVATE)
}