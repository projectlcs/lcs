package net.projectlcs.lcs

import me.ddayo.aris.LuaMain
import net.projectlcs.lcs.lua.glue.LuaGenerated
import party.iroiro.luajava.AbstractLua
import party.iroiro.luajava.luajit.LuaJit

object LuaHandler {
    fun createInstance(): AbstractLua {
        val lua = LuaJit()

        if(lua.top != 0) lua.pop(lua.top) // caused by internal startup

        lua.openLibraries()

        LuaMain.initLua(lua)
        LuaGenerated.initLua(lua)

        return lua
    }
}