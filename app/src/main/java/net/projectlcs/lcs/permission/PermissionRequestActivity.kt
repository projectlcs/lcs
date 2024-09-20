package net.projectlcs.lcs.permission

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import net.projectlcs.lcs.permission.impl.DangerousPermission
import net.projectlcs.lcs.permission.impl.DrawOverlayPermission
import net.projectlcs.lcs.permission.impl.IPermission
import net.projectlcs.lcs.permission.impl.NotificationPolicyPermission
import net.projectlcs.lcs.permission.ui.theme.LCSTheme


class PermissionRequestActivity : ComponentActivity() {
    companion object {
        const val REQUEST_PERMISSION = "request_permission"
        const val REQUEST_NOTIFICATION_PERMISSION = 1
        const val REQUEST_NOTIFICATION_POLICY_PERMISSION = 3
        const val REQUEST_DRAW_OVERLAY_PERMISSION = 4

        private val permissionMap = mapOf(
            REQUEST_NOTIFICATION_PERMISSION to requireSdkOrNull(Build.VERSION_CODES.TIRAMISU) {
                DangerousPermission(
                    android.Manifest.permission.POST_NOTIFICATIONS
                )
            },
            REQUEST_NOTIFICATION_POLICY_PERMISSION to NotificationPolicyPermission(),
            REQUEST_DRAW_OVERLAY_PERMISSION to DrawOverlayPermission()
        )

        private inline fun <T> requireSdkOrNull(version: Int, then: () -> T): T? {
            return if (Build.VERSION.SDK_INT >= version) then()
            else null
        }
    }

    private val permissionToRequest: IPermission? by lazy {
        permissionMap[intent.getIntExtra(
            REQUEST_PERMISSION,
            -1
        )]
    }

    var permissionHandler: ActivityResultLauncher<*>? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (permissionToRequest == null) {
            Toast.makeText(
                applicationContext,
                "Internal error on permission request",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }

        permissionHandler = permissionToRequest!!.init(this)

        enableEdgeToEdge()
        setContent {
            ui()
        }
    }

    private fun startInstalledAppDetailsActivity() {
        val i = Intent()
            .setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .addCategory(Intent.CATEGORY_DEFAULT)
            .setData(Uri.parse("package:$packageName"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            .addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        startActivity(i)
    }

    @Composable
    @Preview
    fun ui() = LCSTheme {
        Column {
            Button(onClick = {
                permissionToRequest?.requestPermission(this@PermissionRequestActivity)
            }) {
                Text(text = "Request permission")
            }
            if (permissionToRequest?.useSettingsButton == true)
                Button(
                    onClick = {
                        startInstalledAppDetailsActivity()
                    },
                ) {
                    Text(text = "Open settings")
                }
        }
    }
}
