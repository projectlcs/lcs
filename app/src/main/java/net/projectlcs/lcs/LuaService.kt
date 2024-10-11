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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.ddayo.aris.LuaEngine
import party.iroiro.luajava.LuaException

class LuaService: Service() {
    companion object {
        // Use context instead of its exact service for debugging purpose
        var INSTANCE: LuaService? = null

        var testScript: String? = null
    }

    val engine = AndroidLuaEngine()

    val luaDispatcher = Dispatchers.Default.limitedParallelism(1, "lua")

    lateinit var job: Job

    override fun onCreate() {
        super.onCreate()

        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(NotificationChannel("LCS_MAIN", "LCS main service", NotificationManager.IMPORTANCE_LOW))

        startForeground(1, Notification.Builder(this, "LCS_MAIN")
            .setContentTitle("LCS is running")
            .setContentText("스크립트를 실행중입니다.")
            .build())

        INSTANCE = this

        job = CoroutineScope(luaDispatcher).launch {
            try {
                // TODO: https://stackoverflow.com/questions/78922796/frequent-crashes-in-internshrstr-after-multiple-executions-of-lua-script-using-l
                engine.addTask(LuaEngine.LuaTask(engine, testScript ?: resources.assets.open("test.lua").readBytes().decodeToString(), "test"))
                /*
                engine.addTask(LuaEngine.LuaTask(engine, """
                        debug_log("02")
                        task_sleep(1)
                        debug_log("03")
                """.trimIndent(),  "2", true))
                 */
            } catch(e: LuaException) {
                Log.e("LUA_LOAD", "Lua exception on script loading: ${e.type}, ${e.message}")
            }

            while(true) {
                try {
                    engine.loop()
                } catch(e: LuaException) {
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