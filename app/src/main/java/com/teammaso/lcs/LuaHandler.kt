package com.teammaso.lcs

import com.teammaso.lcs.lua.LuaGenerated
import party.iroiro.luajava.AbstractLua
import party.iroiro.luajava.luajit.LuaJit

object LuaHandler {
    fun createInstance(): AbstractLua {
        val lua = LuaJit()
        if(lua.top != 0) lua.pop(lua.top) // caused by internal startup

        lua.openLibraries()

        LuaGenerated.initLua(lua)

        return lua
    }
}