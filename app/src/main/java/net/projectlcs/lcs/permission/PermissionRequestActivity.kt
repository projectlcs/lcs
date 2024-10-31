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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import net.projectlcs.lcs.Util.requireSdk
import net.projectlcs.lcs.Util.requireSdkOrNull
import net.projectlcs.lcs.data.ScriptReference
import net.projectlcs.lcs.permission.impl.DangerousPermission
import net.projectlcs.lcs.permission.impl.DrawOverlayPermission
import net.projectlcs.lcs.permission.impl.IPermission
import net.projectlcs.lcs.permission.impl.LocationPermission
import net.projectlcs.lcs.permission.impl.NotificationPolicyPermission
import net.projectlcs.lcs.permission.ui.theme.LCSTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class PermissionRequestActivity : ComponentActivity() {
    companion object {
        const val REQUEST_PERMISSION = "request_permission"
        const val TARGET_REFERENCE = "target_reference"
        const val REQUEST_NOTIFICATION_PERMISSION = 1
        const val REQUEST_NOTIFICATION_POLICY_PERMISSION = 3
        const val REQUEST_DRAW_OVERLAY_PERMISSION = 4
        const val REQUEST_FILE_MANAGE_PERMISSION = 5
        const val REQUEST_LOCATION_PERMISSION = 6

        private val permissionMap = mapOf(
            REQUEST_NOTIFICATION_PERMISSION to requireSdkOrNull(Build.VERSION_CODES.TIRAMISU) {
                DangerousPermission(
                    android.Manifest.permission.POST_NOTIFICATIONS
                )
            },
            REQUEST_NOTIFICATION_POLICY_PERMISSION to NotificationPolicyPermission,
            REQUEST_DRAW_OVERLAY_PERMISSION to DrawOverlayPermission,
            REQUEST_FILE_MANAGE_PERMISSION to DangerousPermission(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE),
            REQUEST_LOCATION_PERMISSION to LocationPermission,
        )
    }

    private val permissionToRequestId by lazy {
        intent.getIntExtra(
            REQUEST_PERMISSION,
            -1
        )
    }

    private val permissionToRequest: IPermission? by lazy {
        permissionMap[permissionToRequestId]
    }

    var permissionHandler: ActivityResultLauncher<*>? = null
        private set

    val ref by lazy {
        requireSdk(
            Build.VERSION_CODES.TIRAMISU,
            then = { intent.getParcelableExtra(TARGET_REFERENCE, ScriptReference::class.java) },
            not = { intent.getParcelableExtra<ScriptReference>(TARGET_REFERENCE) })
    }

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
            if(permissionToRequestId == REQUEST_LOCATION_PERMISSION){
                PermissionButton(
                    title = "Notification Policy Access",
                    description = "Needed to manage Do Not Disturb settings"
                )
            }
            if(permissionToRequestId==REQUEST_NOTIFICATION_POLICY_PERMISSION){
                PermissionButton(
                    title = "Notification Policy Access",
                    description = "Needed to manage Do Not Disturb settings."
                )
            }
            if(permissionToRequestId == REQUEST_DRAW_OVERLAY_PERMISSION){
                PermissionButton(
                    title = "Draw Over Other Apps",
                    description = "Allows the app to display overlays on screen",
                )
            }
            if(permissionToRequestId == REQUEST_FILE_MANAGE_PERMISSION){
                PermissionButton(
                    title = "File Management Access",
                    description = "Needed to manage files on your device",
                )
            }
            if(permissionToRequestId == REQUEST_LOCATION_PERMISSION){
                PermissionButton(
                    title = "Location Access",
                    description = "Allows the app to access your location",
                )
            }

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
                    Text(text = "Open app details")
                }
        }
    }
}

@Composable
fun PermissionButton(title: String, description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineSmall)
        Text(text = description, style = MaterialTheme.typography.bodySmall)
        /*Button(onClick = onClick, modifier = Modifier.padding(top = 8.dp)) {
            Text("Request $title")
        }*/
        Box(modifier = Modifier.padding(top = 8.dp)) {
            Text("Request $title")
        }
    }
}
