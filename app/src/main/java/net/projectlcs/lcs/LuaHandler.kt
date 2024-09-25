package net.projectlcs.lcs

import me.ddayo.aris.LuaEngine
import net.projectlcs.lcs.lua.glue.LuaGenerated

open class AndroidLuaEngine: LuaEngine() {
    init {
        LuaGenerated.initLua(lua)
    }
}
