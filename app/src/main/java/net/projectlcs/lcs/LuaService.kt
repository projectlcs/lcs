package net.projectlcs.lcs

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.ddayo.aris.LuaEngine
import net.projectlcs.lcs.data.ScriptDataManager
import party.iroiro.luajava.LuaException
import party.iroiro.luajava.luajit.LuaJit

class LuaService : AbstractLuaService() {
    companion object {
        // Use context instead of its exact service for debugging purpose
        var INSTANCE: LuaService? = null

        fun runQuery(f: suspend CoroutineScope.() -> Unit) {
            CoroutineScope(INSTANCE?.luaDispatcher ?: Dispatchers.IO).launch(block = f)
        }
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
            1,
            Notification.Builder(this, "LCS_MAIN")
                .setContentTitle("LCS is running")
                .setContentText("스크립트를 실행중입니다.")
                .build(),

            )

        INSTANCE = this

        job = CoroutineScope(luaDispatcher).launch {
            try {
                ScriptDataManager.getRunningScripts().first()
                    .forEach { ref ->
                        if (ref.isValid) {
                            engine.createTask(
                                code = ref.code,
                                name = ref.name,
                                ref = ref,
                                repeat = false
                            ).isPaused = ref.isPaused
                            CoroutineScope(Dispatchers.IO).launch {
                                ScriptDataManager.updateAllScript(ref, rerun = false)
                            }
                        }
                    }
            } catch (e: LuaException) {
                Log.e("LUA_LOAD", "Lua exception on script loading: ${e.type}, ${e.message}")
            }

            while (true) {
                engine.loop()
                engine.tasks.forEach {
                    if (it.errorMessage.isNotEmpty()) {
                        Log.e("Lua Runtime", ("Error: " + it.pullError()))
                        val ref = (it as AndroidLuaEngine.AndroidLuaTask).ref
                        ref.isPaused = true
                        ScriptDataManager.updateAllScript(ref, rerun = false)
                    }
                    if (it.taskStatus == LuaEngine.TaskStatus.FINISHED || it.taskStatus == LuaEngine.TaskStatus.RUNTIME_ERROR || it.taskStatus == LuaEngine.TaskStatus.LOAD_ERROR) {
                        val task = (it as AndroidLuaEngine.AndroidLuaTask).ref
                        task.isRunning = false
                        ScriptDataManager.updateAllScript(task, rerun = false)
                    }
                }
                engine.removeAllFinished()
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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }
}