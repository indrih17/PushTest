package com.indrih.pushtest.base

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.indrih.pushtest.R
import com.indrih.pushtest.exceptions.IntentListForActionNotFoundException
import com.indrih.pushtest.exceptions.IntentNotAvailableException
import com.indrih.pushtest.utils.Logger
import com.indrih.pushtest.utils.MaterialActionDialogCreator
import com.thelittlefireman.appkillermanager.*
import com.thelittlefireman.appkillermanager.managers.DevicesManager
import com.thelittlefireman.appkillermanager.models.KillerManagerActionType

class AppActivity : AppCompatActivity(), Logger {
    private val alertCreator: MaterialActionDialogCreator?

    init {
        alertCreator = DevicesManager
            .getDevice()
            .fold(
                ifLeft = {
                    onFailure(it)
                    null
                },
                ifRight = { device ->
                    MaterialActionDialogCreator(device, this, ::onFailure)
                }
            )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                    error(message = "getInstanceId failed", exception = task.exception)
                    Crashlytics.logException(task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token
                info("token: $token")
            })
    }

    private fun onFailure(failure: Failure) {
        handleFail(failure)?.let(Crashlytics::logException)
    }

    private fun handleFail(failure: Failure): Exception? =
        when (failure) {
            is InternalFail -> {
                error(
                    tag = failure.tag,
                    exception = failure.exception,
                    message = failure.message
                )
                failure.exception
            }

            is UnknownDeviceFail ->
                null

            is IntentFailure ->
                handleIntentFail(failure)
        }

    private fun handleIntentFail(intentFailure: IntentFailure): Exception? =
        when (intentFailure) {
            is IntentNotAvailableFail ->
                IntentNotAvailableException(
                    intentFailure.intent.component.toString()
                )

            is IntentListForActionNotFoundFail ->
                IntentListForActionNotFoundException(
                    intentFailure.debugInformation
                )
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        alertCreator?.onActivityResult(requestCode)
    }
}
