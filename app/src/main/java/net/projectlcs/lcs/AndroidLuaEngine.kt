package net.projectlcs.lcs

import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.ddayo.aris.ILuaStaticDecl
import me.ddayo.aris.LuaEngine
import me.ddayo.aris.luagen.LuaProvider
import net.projectlcs.lcs.data.ScriptReference
import net.projectlcs.lcs.lua.glue.LuaGenerated.AndroidLuaTask_LuaGenerated
import net.projectlcs.lcs.lua.glue.LuaGenerated
import party.iroiro.luajava.Lua

open class AndroidLuaEngine(lua: Lua) : LuaEngine(lua) {
    init {
        LuaGenerated.initLua(lua)
    }

    fun createTask(code: String, name: String, ref: ScriptReference, repeat: Boolean) =
        AndroidLuaTask(code, name, ref, repeat).also { tasks.add(it) }

    @LuaProvider(inherit = "me.ddayo.aris.gen.LuaGenerated")
    inner class AndroidLuaTask(
        val code: String,
        name: String,
        val ref: ScriptReference,
        repeat: Boolean = false
    ) : LuaTask(code, name, repeat), ILuaStaticDecl by AndroidLuaTask_LuaGenerated {
        override var isPaused: Boolean
            get() = super.isPaused
            set(value) { super.isPaused = value }
            /*
            set(value) {
                if (value != super.isPaused) {
                    CoroutineScope(Dispatchers.Main).launch {
                        LuaService.INSTANCE?.let {
                            Toast.makeText(
                                it,
                                if (value) "Task $name paused" else "Task $name resumed",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    super.isPaused = value
                }
            }
             */

        init {
            if (taskStatus == TaskStatus.LOAD_ERROR) {
                Log.e("LuaRuntime", "Complication error thrown: $errorMessage")
            }
        }

        var isRunning
            get() = !isPaused && (taskStatus == TaskStatus.YIELDED || taskStatus == TaskStatus.RUNNING)
            set(value) {
                if (value) {
                    isPaused = false
                    if (taskStatus == TaskStatus.FINISHED)
                        restart()
                } else isPaused = true
            }
    }
}
