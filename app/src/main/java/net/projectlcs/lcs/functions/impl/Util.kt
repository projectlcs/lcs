package net.projectlcs.lcs.functions.impl

import net.projectlcs.lcs.ap.LuaFunction
import net.projectlcs.lcs.ap.LuaProvider

@LuaProvider
object Util {
    @LuaFunction(name = "get_time")
    fun getTime(): Long {
        return System.currentTimeMillis()
    }
}