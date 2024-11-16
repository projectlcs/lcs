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
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.projectlcs.lcs.permission.impl.ManageFilePermission

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
            REQUEST_FILE_MANAGE_PERMISSION to ManageFilePermission,
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
    fun ui() = LCSTheme {
        Column (
            modifier = Modifier.fillMaxSize()
                .padding(vertical = 24.dp)
                //.scrollable(orientation = Orientation.Vertical)
        ){
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
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally, // 텍스트 가운데 정렬
        verticalArrangement = Arrangement.spacedBy(16.dp) // 텍스트 사이 간격
    ) {
        //Text(text = title, fontSize = 30.sp, style = MaterialTheme.typography.headlineSmall)
        Text(
            text = title,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Red,
        )
        Text(
            text = description,
            fontSize = 24.sp,
            color = Color.DarkGray,
            modifier = Modifier
                .padding(8.dp) // 배경색 및 여백 추가
        )
    }
}
