package net.projectlcs.lcs

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch

class LuaService: Service() {
    companion object {
        // Use context instead of its exact service for debugging purpose
        var INSTANCE: LuaService? = null
    }

    val lua = LuaHandler.createInstance()

    val luaDispatcher = Dispatchers.Default.limitedParallelism(2, "lua")

    // maybe there are better solution...
    inline fun withSuspend(crossinline fn: suspend CoroutineScope.() -> Unit) {
        val latch = CountDownLatch(1)
        GlobalScope.launch(luaDispatcher) {
            fn()
            latch.countDown()
        }
        latch.await()
    }

    override fun onCreate() {
        super.onCreate()

        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(NotificationChannel("LCS_MAIN", "LCS main service", NotificationManager.IMPORTANCE_LOW))

        startForeground(1, Notification.Builder(this, "LCS_MAIN")
            .setContentTitle("LCS is running")
            .setContentText("스크립트를 실행중입니다.")
            .build())

        INSTANCE = this

        GlobalScope.launch(luaDispatcher) {
            lua.load("""
                |set_do_not_disturb(not get_do_not_disturb())
            """.trimMargin())
            lua.pCall(0, 0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        INSTANCE = null

        Log.d("onDestroy", "service destroyed")

        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
}