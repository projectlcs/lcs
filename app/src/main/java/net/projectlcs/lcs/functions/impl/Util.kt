package net.projectlcs.lcs.functions.impl

import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider

@LuaProvider
object Util {
    @LuaFunction(name = "get_time")
            /**
             * System.currentTimeMillis()
             *
             * @return current datetime from 1/1/1970
             */
    fun getTime(): Long {
        return System.currentTimeMillis()
    }
}