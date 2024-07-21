package net.projectlcs.lcs.functions

import android.util.Log
import net.projectlcs.lcs.ap.LuaFunction
import net.projectlcs.lcs.ap.LuaProvider

@LuaProvider
object Util {
    @LuaFunction(name = "get_time")
    fun getTime(): Long {
        return System.currentTimeMillis()
    }
}