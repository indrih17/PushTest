package com.indrih.pushtest.base

import android.Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.indrih.pushtest.R
import com.indrih.pushtest.china.showBadDeviceWarning
import com.indrih.pushtest.utils.PermissionProvider

class MainActivity : AppCompatActivity() {
    private val loggerTag = "MainActivity"
    private val permissionProvider = PermissionProvider(
        requestCode = 1,
        permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            arrayOf(REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        else
            emptyArray()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Base

        if (!permissionProvider.hasAllPermissions(this))
            permissionProvider.checkPermissions(this)

        showBadDeviceWarning(
            context = this,
            messageForUser = R.string.first_start_warning
        )

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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (!permissionProvider.isAllPermissionsGranted(requestCode, permissions, grantResults))
            closeApp()
    }

    fun closeApp() {
        moveTaskToBack(true)
        finish()
    }
}
