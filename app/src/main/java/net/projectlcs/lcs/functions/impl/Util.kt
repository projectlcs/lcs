package net.projectlcs.lcs.functions.impl

import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider

@LuaProvider
object Util {
    @LuaFunction(name = "get_time")
    fun getTime(): Long {
        return System.currentTimeMillis()
    }
}