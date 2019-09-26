package com.indrih.pushtest.utils

import android.content.Context
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.thelittlefireman.appkillermanager.devices.DeviceBase
import com.thelittlefireman.appkillermanager.ui.ActionDialogCreator

class MaterialActionDialogCreator(
    device: DeviceBase,
    activity: AppCompatActivity
) : ActionDialogCreator(device, activity) {
    override fun Context.showDialog(
        messageForUser: Int,
        okButton: () -> Unit,
        noButton: () -> Unit
    ) {
        materialAlert(
            message = messageForUser,
            positiveButtonText = android.R.string.yes,
            onOkButtonClick = okButton,
            onNoButtonClick = noButton,
            windowFeature = Window.FEATURE_NO_TITLE,
            cancelable = false
        )
    }
}