package net.projectlcs.lcs

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import party.iroiro.luajava.LuaException

class LuaService: Service() {
    companion object {
        // Use context instead of its exact service for debugging purpose
        var INSTANCE: LuaService? = null
    }

    val lua = LuaHandler.createInstance()

    val luaDispatcher = Dispatchers.Default.limitedParallelism(2, "lua")

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
            lua.load(resources.assets.open("main_gen.lua").readBytes().decodeToString())
            lua.pCall(0, 0)
            lua.load(resources.assets.open("main.lua").readBytes().decodeToString())
            lua.pCall(0, 0)

            lua.getGlobal("register_task")
            lua.push(resources.assets.open("test.lua").readBytes().decodeToString())
            lua.push("test")
            lua.pCall(2, 0)

            while(true) {
                lua.getGlobal("loop")
                try {
                    lua.pCall(0, 0)
                } catch(e: LuaException) {
                    Log.e("LUA", e.message ?: "null message")
                }
                delay(100)
            }
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