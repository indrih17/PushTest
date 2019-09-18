package com.indrih.pushtest.china

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.view.Window
import com.indrih.pushtest.utils.materialAlert
import com.indrih.pushtest.utils.toast

/**
 * Add users permission to app manifest:
 *
 * &lt;uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" /&gt;
 */
fun showBadDeviceWarning(
    context: Context,
    messageForUser: Int
) {
    val isFirstRun = RunStatusPref.get(context)
    if (isFirstRun) {
        RunStatusPref.set(context, isFirstRun = false)

        showWarning(context, messageForUser)
    }
}

private fun showWarning(context: Context, messageForUser: Int) {
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

    if (isCallable(context, intent))
        showWarningAlert(context, intent, messageForUser)
}

private fun isCallable(context: Context, intent: Intent): Boolean =
    context
        .packageManager
        .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        .isNotEmpty()

private fun showWarningAlert(
    context: Context,
    intent: Intent,
    messageForUser: Int
) {
    materialAlert(
        context = context,
        message = messageForUser,
        positiveButtonText = android.R.string.yes,
        onOkButtonClick = {
            try {
                context.startActivity(intent)
            } catch (e: SecurityException) {
                toast(context, "Unable to show settings")
            }
        },
        onNoButtonClick = {},
        windowFeature = Window.FEATURE_NO_TITLE,
        cancelable = false
    )
}