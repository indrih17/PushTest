package com.indrih.pushtest.base

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebasePushService : FirebaseMessagingService() {
    private val tag = "FirebasePushService"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        remoteMessage.data.forEach {
            Log.i(tag, "remote message data: ${it.key} to ${it.value}")
        }
        remoteMessage.notification?.let {
            Log.i(tag, "remote message notification: ${it.title}, ${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i(tag, "new token: $token")
    }
}