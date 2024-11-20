package net.projectlcs.lcs.functions.impl

import android.icu.util.Calendar
import me.ddayo.aris.LuaMultiReturn
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider

@LuaProvider
object Datetime {
    @LuaFunction(name = "get_current_time")
            /**
             * Returns current time of day
             * @return hour_of_day(0..23), minute(0..59), seconds(0..59)
             */
    fun getCurrentTime(): LuaMultiReturn {
        val calendar = Calendar.getInstance()
        return LuaMultiReturn(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND))
    }

    @LuaFunction(name = "get_current_date")
            /**
             * Returns current date
             * @return day_of_month(1..31), month(0..11), year(int)
             */
    fun getCurrentDate(): LuaMultiReturn {
        val calendar = Calendar.getInstance()
        return LuaMultiReturn(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR))
    }

    @LuaFunction(name = "get_current_day_of_week")
            /**
             * Returns day of week
             * @return day of week(1..7). 1 is sunday and 7 is saturday
             */
    fun getDayOfWeek(): LuaMultiReturn {
        val calendar = Calendar.getInstance()
        return LuaMultiReturn(calendar.get(Calendar.DAY_OF_WEEK))
    }
}