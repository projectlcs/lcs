package com.teammaso.lcs

import android.accessibilityservice.AccessibilityService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.IInterface
import android.os.Parcel
import android.view.accessibility.AccessibilityEvent
import java.io.FileDescriptor

class LuaService: Service() {
    companion object {
        // Use context instead of its exact service for debugging purpose
        var INSTANCE: LuaService? = null
    }

    val lua = LuaHandler.createInstance()

    override fun onCreate() {
        super.onCreate()

        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(NotificationChannel("LCS_MAIN", "LCS main service", NotificationManager.IMPORTANCE_LOW))

        startForeground(1, Notification.Builder(this, "LCS_MAIN")
            .setContentTitle("LCS is running")
            .setContentText("스크립트를 실행중입니다.")
            .build())

        INSTANCE = this

        lua.load("set_do_not_disturb(not get_do_not_disturb())")
        lua.pCall(0,0)
    }

    override fun onDestroy() {
        super.onDestroy()
        INSTANCE = null

        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
}