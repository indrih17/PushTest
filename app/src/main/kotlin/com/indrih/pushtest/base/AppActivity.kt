package com.indrih.pushtest.base

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.indrih.pushtest.R
import com.indrih.pushtest.utils.Logger
import com.indrih.pushtest.utils.MaterialActionDialogCreator
import com.thelittlefireman.appkillermanager.managers.KillerManager
import com.thelittlefireman.appkillermanager.models.KillerManagerActionType
import com.thelittlefireman.appkillermanager.utils.LogUtils

class AppActivity : AppCompatActivity(), Logger {
    private val alertCreator: MaterialActionDialogCreator? by lazy {
        val device = KillerManager.device
        if (device != null)
            MaterialActionDialogCreator(device, this)
        else
            null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LogUtils.logCustomListener = object : LogUtils.LogCustomListener {
            override fun i(tag: String, message: String) =
                info(message, tag)

            override fun e(tag: String, message: String?, exception: Exception) =
                error(message, exception, tag)
        }

        // Base
        alertCreator?.run {
            showDialogForAction(
                KillerManagerActionType.ActionAutoStart,
                R.string.auto_start_message
            )

            showDialogForAction(
                KillerManagerActionType.ActionPowerSaving,
                R.string.battery_optimization_message
            )

            showDialogForAction(
                KillerManagerActionType.ActionNotifications,
                R.string.notification_message
            )
        }

        // Other

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = getString(R.string.default_notification_channel_id)
            val channelName = getString(R.string.default_notification_channel_name)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
                )
            )
        }

        FirebaseInstanceId
            .getInstance()
            .instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    error("getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token
                info("token: $token")
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        alertCreator?.onActivityResult(requestCode)
    }
}
