package net.projectlcs.lcs

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import me.ddayo.aris.LuaEngine
import net.projectlcs.lcs.data.ScriptReference
import net.projectlcs.lcs.lua.glue.LuaGenerated
import party.iroiro.luajava.Lua

open class AndroidLuaEngine(lua: Lua) : LuaEngine(lua) {
    init {
        LuaGenerated.initLua(lua)
    }

    fun createTask(code: String, name: String, ref: ScriptReference, repeat: Boolean) =
        AndroidLuaTask(code, name, ref, repeat).also { tasks.add(it) }

    inner class AndroidLuaTask(
        val code: String,
        name: String,
        val ref: ScriptReference,
        repeat: Boolean = false
    ) : LuaTask(code, name, repeat) {
        override var isPaused: Boolean
            get() = super.isPaused
            set(value) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        LuaService.INSTANCE,
                        if (value) "Task $name paused" else "Task $name resumed",
                        Toast.LENGTH_LONG
                    ).show()
                }
                super.isPaused = value
            }
    }
}
