package com.example.service

import android.util.Log
import com.example.data.model.DesktopPushEvent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class AccountingFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed FCM Token: $token")
        DesktopPushNotificationManager.updateToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From: ${remoteMessage.from}")

        val data = remoteMessage.data
        val eventType = data["eventType"] ?: data["type"] ?: "DESKTOP_RECORD_CHANGED"
        val machineCode = data["machineCode"] ?: data["source"] ?: "AC-DESKTOP-892A"
        val entityType = data["entityType"] ?: data["table"] ?: "VOUCHER"
        val count = data["recordsCount"]?.toIntOrNull() ?: 1
        val summary = remoteMessage.notification?.body
            ?: data["summary"]
            ?: data["message"]
            ?: "Phần mềm Desktop [$machineCode] đã cập nhật chứng từ kế toán mới"

        val event = DesktopPushEvent(
            eventType = eventType,
            sourceMachineCode = machineCode,
            entityType = entityType,
            recordsCount = count,
            summary = summary,
            timestamp = System.currentTimeMillis()
        )

        DesktopPushNotificationManager.dispatchPushEvent(
            context = applicationContext,
            event = event,
            showNotification = true
        )
    }

    companion object {
        private const val TAG = "AC_FirebaseMsgService"
    }
}
