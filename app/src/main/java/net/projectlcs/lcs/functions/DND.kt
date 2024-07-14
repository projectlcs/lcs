package net.projectlcs.lcs.functions

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.provider.Settings
import android.util.Log
import net.projectlcs.lcs.LuaService
import net.projectlcs.lcs.ap.LuaFunction


object DND {
    private fun grantNotificationService(): NotificationManager? {
        val service = LuaService.INSTANCE!!
        val notService = service.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

        // request permission
        if (notService?.isNotificationPolicyAccessGranted != true) {
            service.startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }

        return notService
    }

    @LuaFunction(name = "set_do_not_disturb")
    fun setDND(newValue: Boolean) {
        val notService = grantNotificationService()
        /*
         TODO: Android 15(SDK 35) contains critical change at this API
         See also: https://developer.android.com/about/versions/15/behavior-changes-15#dnd-changes
         */
        notService?.let {
            if (VERSION.SDK_INT > VERSION_CODES.UPSIDE_DOWN_CAKE)
                Log.d("DND", "Android 15 can break some features")
            it.setInterruptionFilter(
                if (newValue)
                    NotificationManager.INTERRUPTION_FILTER_NONE
                else NotificationManager.INTERRUPTION_FILTER_ALL
            )
        }
    }

    @LuaFunction(name = "get_do_not_disturb")
    fun getDND(): Boolean {
        val notService = grantNotificationService()
        return notService!!.currentInterruptionFilter == NotificationManager.INTERRUPTION_FILTER_NONE
    }
}