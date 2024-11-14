package net.projectlcs.lcs.functions.impl

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import net.projectlcs.lcs.AndroidLuaEngine
import net.projectlcs.lcs.LuaService
import net.projectlcs.lcs.R
import net.projectlcs.lcs.functions.PermissionProvider
import net.projectlcs.lcs.permission.PermissionRequestActivity

@LuaProvider
object Notification: PermissionProvider {
    /**
     * Send notification with existing task
     *
     * @param task the LuaTask. just use `task` which already passed to function.
     * @param message message to send
     */
    @LuaFunction("send_notification")
    fun sendNotification(task: AndroidLuaEngine.AndroidLuaTask, message: String)
            = sendNotification(task, task.name, message)

    @SuppressLint("MissingPermission") // requestPermission lambda asserts permission
    @LuaFunction(name = "send_notification")
            /**
             * Send notification with provided title and text
             *
             * @param task the LuaTask. just use `task` which already passed to function.
             * @param title title of notification
             * @param text inner text of notification
             */
    fun sendNotification(task: AndroidLuaEngine.AndroidLuaTask, title: String, text: String) = coroutine<Nothing> {
        requestPermission {
            val channelId = task.name
            val channel = NotificationChannel(channelId, "Notification from script ${task.name}", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Notifications from script ${task.name}"
            }
            val notificationManager = LuaService.INSTANCE!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            val notification = NotificationCompat.Builder(LuaService.INSTANCE!!, channelId)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(NotificationCompat.BigTextStyle().bigText(text))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build()

            NotificationManagerCompat.from(LuaService.INSTANCE!!).notify(2, notification)
        }
    }

    /**
     * Send long toast message
     *
     * @param text text to displayed by long toast message
     */
    @LuaFunction(name = "send_long_toast")
    fun sendToastLong(text: String) {
        mainThread {
            Toast.makeText(LuaService.INSTANCE!!, text, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Send toast message
     *
     * @param text text to displayed by toast message
     */
    @LuaFunction(name = "send_toast")
    fun sendToast(text: String) {
        mainThread {
            Toast.makeText(LuaService.INSTANCE!!, text, Toast.LENGTH_SHORT).show()
        }
    }

    override fun verifyPermission(): Boolean {
        val notManager = LuaService.INSTANCE!!.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        return notManager?.areNotificationsEnabled() == true
    }

    override fun requestPermission() = startPermissionActivity(PermissionRequestActivity.REQUEST_NOTIFICATION_PERMISSION)
}