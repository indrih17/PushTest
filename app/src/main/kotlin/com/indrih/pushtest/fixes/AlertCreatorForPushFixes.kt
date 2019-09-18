package com.indrih.pushtest.fixes

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.indrih.pushtest.materialAlert

/**
 * Add users permission to app manifest:
 *
 * &lt;uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" /&gt;
 */
class AlertCreatorForPushFixes(private val activity: AppCompatActivity) {
    private val prefManager = PrefManager(activity.applicationContext)
    private val callbackStore = CallbackStore(activity)

    fun autoStart(messageForUser: Int) = activity.run {
        if (prefManager.isFirstRunAutoStartDialod)
            autoStartIntentOrNull()?.let { intent ->
                showAlert(
                    messageForUser,
                    okButton = {
                        callbackStore.startActivityWithCallback(intent) {
                            prefManager.isFirstRunAutoStartDialod = false
                        }
                    },
                    noButton = {
                        prefManager.isFirstRunAutoStartDialod = false
                    }
                )
            }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun batteryOptimization(messageForUser: Int) = activity.run {
        if (prefManager.isFirstRunBatteryOptimizationDialog)
            optimizationIntentOrNull()?.let { intent ->
                showAlert(
                    messageForUser,
                    okButton = {
                        callbackStore.startActivityWithCallback(intent) {
                            if (enabledBatteryOptimizations())
                                prefManager.isFirstRunBatteryOptimizationDialog = false
                        }
                    },
                    noButton = {
                        prefManager.isFirstRunBatteryOptimizationDialog = false
                    }
                )
            }
    }

    fun onActivityResult(requestCode: Int) {
        callbackStore
            .getByRequestCodeOrNull(requestCode)
            ?.invoke()
    }

    private fun Context.autoStartIntentOrNull(): Intent? {
        val intent = Intent()
        when {
            Build.BRAND.equals("Xiaomi", ignoreCase = true) ->
                intent.component = ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )

            Build.BRAND.equals("Letv", ignoreCase = true) ->
                intent.component = ComponentName(
                    "com.letv.android.letvsafe",
                    "com.letv.android.letvsafe.AutobootManageActivity"
                )

            Build.BRAND.equals("Honor", ignoreCase = true) ->
                intent.component = ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.optimize.process.ProtectActivity"
                )

            Build.BRAND.equals("Huawei", ignoreCase = true) ->
                intent.component = ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.optimize.process.ProtectActivity"
                )
        }

        return if (isCallable(intent)) intent else null
    }

    private fun Context.isCallable(intent: Intent): Boolean =
        packageManager
            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            .isNotEmpty()

    @RequiresApi(Build.VERSION_CODES.M)
    private fun Context.optimizationIntentOrNull(): Intent? =
        if (enabledBatteryOptimizations())
            Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        else
            null

    @RequiresApi(Build.VERSION_CODES.M)
    private fun Context.enabledBatteryOptimizations(): Boolean {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val packageName = applicationContext.packageName
        return !powerManager.isIgnoringBatteryOptimizations(packageName)
    }

    private fun Context.showAlert(
        messageForUser: Int,
        okButton: () -> Unit,
        noButton: () -> Unit
    ) =
        materialAlert(
            message = messageForUser,
            positiveButtonText = android.R.string.yes,
            onOkButtonClick = okButton,
            onNoButtonClick = noButton,
            windowFeature = Window.FEATURE_NO_TITLE,
            cancelable = false
        )
}