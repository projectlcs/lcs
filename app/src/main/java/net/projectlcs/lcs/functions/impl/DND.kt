package net.projectlcs.lcs.functions.impl

import android.app.NotificationManager
import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.Log
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import net.projectlcs.lcs.LuaService
import net.projectlcs.lcs.functions.PermissionProvider
import net.projectlcs.lcs.permission.PermissionRequestActivity


@LuaProvider
object DND : PermissionProvider {
    override fun verifyPermission(): Boolean {
        val notService = retrieveNotificationService()
        return notService?.isNotificationPolicyAccessGranted == true
    }

    override fun requestPermission() =
        startPermissionActivity(PermissionRequestActivity.REQUEST_NOTIFICATION_POLICY_PERMISSION)

    private fun retrieveNotificationService(): NotificationManager? {
        val service = LuaService.INSTANCE!!
        return service.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    }

    @LuaFunction(name = "set_do_not_disturb")
            /**
             * @param newValue new do not disturb state
             */
    fun setDND(newValue: Boolean) = coroutine<Unit> {
        requestPermission {
            val notService = retrieveNotificationService()
            /*
             TODO: Android 15(SDK 35) contains critical change at this API
             See also: https://developer.android.com/about/versions/15/behavior-changes-15#dnd-changes
             */
            notService?.let {
                /*
                    if (VERSION.SDK_INT > VERSION_CODES.UPSIDE_DOWN_CAKE)
                        Log.d("DND", "Android 15 can break some features")
                 */
                it.setInterruptionFilter(
                    if (newValue)
                        NotificationManager.INTERRUPTION_FILTER_NONE
                    else NotificationManager.INTERRUPTION_FILTER_ALL
                )
            }
        }
    }

    @LuaFunction(name = "get_do_not_disturb")
            /**
             * @return current do not disturb status
             */
    fun getDND() = coroutine {
        requestPermission {
            val notService = retrieveNotificationService()
            breakTask(notService!!.currentInterruptionFilter == NotificationManager.INTERRUPTION_FILTER_NONE)
        }
    }
}