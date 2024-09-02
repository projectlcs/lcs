package net.projectlcs.lcs.functions.impl

import android.content.Intent
import android.provider.AlarmClock
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import net.projectlcs.lcs.LuaService

@LuaProvider
object Alarms {
    @LuaFunction(name = "create_alarm")
            /**
             * createAlarm(15, 12) // set new alarm at 3:12 PM
             *
             * @param hour time of hour to set alarm
             * @param minutes time of minutes to set alarm
             */
            /*https://developer.android.com/reference/kotlin/android/provider/AlarmClock
            https://developer.android.com/reference/kotlin/android/provider/AlarmClock#AlarmClock()
            https://developer.android.com/guide/components/intents-common?hl=ko
             */
    fun createAlarm(hour: Int, minutes: Int) {
        val service = LuaService.INSTANCE!!
        service.startActivity(Intent(AlarmClock.ACTION_SET_ALARM).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(AlarmClock.EXTRA_HOUR, hour)
            putExtra(AlarmClock.EXTRA_MINUTES, minutes)
            putExtra(AlarmClock.EXTRA_MESSAGE,"LCS")
        })
    }
}