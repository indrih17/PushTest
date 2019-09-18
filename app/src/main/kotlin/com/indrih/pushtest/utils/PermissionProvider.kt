package com.indrih.pushtest.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionProvider(
    private val requestCode: Int,
    private val permissions: Array<String>
) {
    /**
     * Are all necessary permissions allowed.
     */
    fun hasAllPermissions(context: Context): Boolean =
        permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    /**
     * Notify the user about the need for consent to all necessary permissions.
     */
    fun checkPermissions(activity: AppCompatActivity) =
        ActivityCompat.requestPermissions(
            activity,
            permissions,
            requestCode
        )

    fun isAllPermissionsGranted(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean =
        this.requestCode == requestCode
                && this.permissions.contentEquals(permissions)
                && grantResults.all { it == PackageManager.PERMISSION_GRANTED }
}