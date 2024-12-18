package net.projectlcs.lcs.functions.impl

import android.util.Log
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider

@LuaProvider
object Logger {
    @LuaFunction(name = "debug_log")
    fun printDebug(message: String) = Log.d("Lua", message)

    @LuaFunction(name = "debug_log")
    fun printDebug(tag: String, message: String) = Log.d(tag, message)
}