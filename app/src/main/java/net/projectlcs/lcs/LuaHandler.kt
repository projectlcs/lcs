package net.projectlcs.lcs

import net.projectlcs.lcs.functions.impl.Laboratory
import net.projectlcs.lcs.lua.LuaGenerated
import party.iroiro.luajava.AbstractLua
import party.iroiro.luajava.luajit.LuaJit

object LuaHandler {
    fun createInstance(): AbstractLua {
        val lua = LuaJit()
        if(lua.top != 0) lua.pop(lua.top) // caused by internal startup

        lua.openLibraries()

        LuaGenerated.initLua(lua)

        // TODO: This is only for testing purpose. remove on future commit
        lua.push { lua ->
            lua.pushJavaObject(Laboratory.test())
            1
        }
        lua.setGlobal("kt_test_kt1")

        return lua
    }
}