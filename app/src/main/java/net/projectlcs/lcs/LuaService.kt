package net.projectlcs.lcs

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
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

class LuaService : LifecycleService(), SavedStateRegistryOwner, ViewModelStoreOwner {
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

        mSavedStateRegistryController.performRestore(null)
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

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
                ScriptDataManager.getAllScripts().first()
                    .forEach { it ->
                        if (it.isValid) {
                            engine.createTask(
                                code = it.code,
                                name = it.name,
                                ref = it,
                                repeat = false
                            ).isPaused = it.isPaused
                        } else Log.e("LUA_LOAD", "tried to load invalid script")
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
                        ScriptDataManager.updateAllScript(ref, invalidateExisting = false)
                    }
                    if (it.taskStatus == LuaEngine.TaskStatus.FINISHED) {
                        val task = (it as AndroidLuaEngine.AndroidLuaTask).ref
                        task.isPaused = true
                        ScriptDataManager.updateAllScript(task, invalidateExisting = false)
                    }
                }
                delay(100)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        INSTANCE = null

        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)

        Log.d("onDestroy", "service destroyed")

        job.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun onBind(intent: Intent): IBinder? {
        return super.onBind(intent)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    private var mLifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
    private var mSavedStateRegistryController: SavedStateRegistryController = SavedStateRegistryController.create(this)

    val isInitialized: Boolean
        get() = true

    override val lifecycle: Lifecycle
        get() = mLifecycleRegistry

    fun setCurrentState(state: Lifecycle.State) {
        mLifecycleRegistry.currentState = state
    }

    fun handleLifecycleEvent(event: Lifecycle.Event) {
        mLifecycleRegistry.handleLifecycleEvent(event)
    }

    override val savedStateRegistry: SavedStateRegistry
        get() = mSavedStateRegistryController.savedStateRegistry

    fun performRestore(savedState: Bundle?) {
        mSavedStateRegistryController.performRestore(savedState)
    }

    fun performSave(outBundle: Bundle) {
        mSavedStateRegistryController.performSave(outBundle)
    }

    private val mViewModelStore = ViewModelStore()
    override val viewModelStore: ViewModelStore
        get() = mViewModelStore
}