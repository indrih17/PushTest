package com.indrih.pushtest.utils

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun Context.materialAlert(
    message: Int,
    title: Int? = null,
    positiveButtonText: Int = android.R.string.ok,
    negativeButtonText: Int = android.R.string.cancel,
    onOkButtonClick: (() -> Unit)?,
    onNoButtonClick: (() -> Unit)? = null,
    windowFeature: Int? = null,
    cancelable: Boolean,
    themeRes: Int = 0
) =
    MaterialAlertDialogBuilder(this, themeRes)
        .also { alert ->
            alert.setMessage(message)
            title?.let(alert::setTitle)

            onOkButtonClick?.let {
                alert.setPositiveButton(positiveButtonText) { _, _ ->
                    it.invoke()
                }
            }
            onNoButtonClick?.let {
                alert.setNegativeButton(negativeButtonText) { _, _ ->
                    it.invoke()
                }
            }
            alert.setCancelable(cancelable)
        }
        .create()
        .also {
            windowFeature?.let(it::requestWindowFeature)
        }
        .show()