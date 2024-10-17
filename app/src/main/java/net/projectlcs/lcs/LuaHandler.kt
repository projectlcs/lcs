package net.projectlcs.lcs

import android.widget.Toast
import me.ddayo.aris.LuaEngine
import net.projectlcs.lcs.lua.glue.LuaGenerated
import party.iroiro.luajava.Lua

open class AndroidLuaEngine(lua: Lua): LuaEngine(lua) {
    init {
        LuaGenerated.initLua(lua)
    }

    override fun createTask(code: String, name: String, repeat: Boolean) = tasks.add(AndroidLuaTask(code, name, repeat))

    inner class AndroidLuaTask(code: String, name: String, repeat: Boolean = false): LuaTask(code, name, repeat) {
        override var isPaused: Boolean
            get() = super.isPaused
            set(value) {
                Toast.makeText(LuaService.INSTANCE, if(value) "Task $name paused" else "Task $name resumed", Toast.LENGTH_LONG).show()
                super.isPaused = value
            }
    }
}
