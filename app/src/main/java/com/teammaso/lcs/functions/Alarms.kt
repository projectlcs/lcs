package com.teammaso.lcs.functions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Message
import android.provider.AlarmClock
import android.provider.AlarmClock.ACTION_SET_ALARM
import androidx.core.content.ContextCompat.startActivity
import com.teammaso.lcs.LuaService
import com.teammaso.lcs.ap.LuaFunction
import android.util.Log

object Alarms {
    @LuaFunction(name = "create_alarm")
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