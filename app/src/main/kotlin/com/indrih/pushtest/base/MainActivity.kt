package com.indrih.pushtest.base

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.indrih.pushtest.R
import com.indrih.pushtest.fixes.AlertCreatorForPushFixes
import com.indrih.pushtest.toast

class MainActivity : AppCompatActivity() {
    private val loggerTag = "MainActivity"
    private val pushFixes = AlertCreatorForPushFixes(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Base

        try {
            pushFixes.autoStart(R.string.auto_start_message)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                pushFixes.batteryOptimization(R.string.battery_optimization_message)
        } catch (e: Exception) {
            toast("Unable to show settings")
        }

        // Other

        intent.extras?.let {
            for (key in it.keySet()) {
                val value = intent.extras?.get(key)
                Log.d(loggerTag, "Key: $key, Value: $value")
            }
        }

        FirebaseInstanceId
            .getInstance()
            .instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(loggerTag, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token
                Log.i(loggerTag, token ?: "")
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        pushFixes.onActivityResult(requestCode)
    }
}
