package net.projectlcs.lcs.functions

import kotlinx.coroutines.delay
import net.projectlcs.lcs.LuaService
import net.projectlcs.lcs.ap.LuaFunction
import net.projectlcs.lcs.ap.LuaProvider
import kotlin.math.roundToLong

@LuaProvider
object Wait {
    @LuaFunction(name = "wait_for_seconds")
    fun waitForSeconds(second: Double) {
        LuaService.INSTANCE!!.withSuspend {
            delay((second * 1000).roundToLong())
        }
    }
}