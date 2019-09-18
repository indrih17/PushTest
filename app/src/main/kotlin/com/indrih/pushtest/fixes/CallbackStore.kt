package com.indrih.pushtest.fixes

import android.content.Intent
import android.util.SparseArray
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.contains
import java.util.concurrent.atomic.AtomicInteger

class CallbackStore(private val activity: AppCompatActivity) {
    private val lastCode = AtomicInteger(0)
    private val callbacks = SparseArray<() -> Unit>()

    fun getByRequestCodeOrNull(code: Int): (() -> Unit)? =
        if (callbacks.contains(code))
            callbacks.get(code)
        else
            null

    fun startActivityWithCallback(intent: Intent, callback: () -> Unit) {
        val code = lastCode.incrementAndGet()
        callbacks.put(code, callback)
        activity.startActivityForResult(intent, code)
    }
}