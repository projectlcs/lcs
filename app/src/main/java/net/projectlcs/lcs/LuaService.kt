package net.projectlcs.lcs

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.projectlcs.lcs.data.ScriptDataManager
import party.iroiro.luajava.LuaException
import party.iroiro.luajava.luajit.LuaJit

class LuaService : Service() {
    companion object {
        // Use context instead of its exact service for debugging purpose
        var INSTANCE: LuaService? = null

        var testScript: String? = null
    }

    val engine = AndroidLuaEngine(LuaJit())

    val luaDispatcher = Dispatchers.Default.limitedParallelism(1, "lua")

    lateinit var job: Job

    override fun onCreate() {
        super.onCreate()

        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(
                NotificationChannel(
                    "LCS_MAIN",
                    "LCS main service",
                    NotificationManager.IMPORTANCE_LOW
                )
            )

        startForeground(
            1, Notification.Builder(this, "LCS_MAIN")
                .setContentTitle("LCS is running")
                .setContentText("스크립트를 실행중입니다.")
                .build(),
            0 or ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
        )

        INSTANCE = this

        job = CoroutineScope(luaDispatcher).launch {
            try {
                ScriptDataManager.getAllScripts().first()
                    .forEach { it ->
                        if (it.isValid) {
                            engine.createTask(
                                code = it.code,
                                name = it.name,
                                ref = it,
                                repeat = true
                            ).isPaused = it.isPaused
                        }
                    }
            } catch (e: LuaException) {
                Log.e("LUA_LOAD", "Lua exception on script loading: ${e.type}, ${e.message}")
            }
            ScriptDataManager.deleteAllScript(*ScriptDataManager.getAllScripts().first().toTypedArray())
            if (ScriptDataManager.getAllScripts().first().isEmpty()) {
                val sc = ScriptDataManager.createNewScript("Test1")
                sc.code =
                    testScript ?: resources.assets.open("test.lua").readBytes().decodeToString()
                ScriptDataManager.updateAllScript(sc)
                Log.e("Update", "updated")
            }

            while (true) {
                try {
                    engine.loop()
                } catch (e: LuaException) {
                    Log.e("Lua Runtime", ("Error: " + e.message))
                    // throw e
                }
                delay(100)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        INSTANCE = null

        Log.d("onDestroy", "service destroyed")

        job.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
}