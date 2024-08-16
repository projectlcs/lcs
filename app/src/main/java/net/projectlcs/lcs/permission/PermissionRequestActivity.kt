package net.projectlcs.lcs.permission

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import net.projectlcs.lcs.permission.ui.theme.LCSTheme


class PermissionRequestActivity : ComponentActivity() {
    companion object {
        const val REQUEST_PERMISSION = "request_permission"
        const val REQUEST_NOTIFICATION_PERMISSION = 1
        const val REQUEST_BLUETOOTH_PERMISSION = 2
    }

    private val permissionToRequest by lazy { intent.getIntExtra(REQUEST_PERMISSION, -1) }
    private val targetPermission by lazy { when(permissionToRequest) {
        REQUEST_NOTIFICATION_PERMISSION -> requireSdkOrNull(Build.VERSION_CODES.TIRAMISU) { android.Manifest.permission.POST_NOTIFICATIONS }
        REQUEST_BLUETOOTH_PERMISSION -> requireSdkOrNull(Build.VERSION_CODES.S) { android.Manifest.permission.BLUETOOTH_CONNECT }
        else -> null
    } }

    private val requestPermissionLauncher by lazy {
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(targetPermission == null) {
            Toast.makeText(applicationContext, "Internal error on permission request", Toast.LENGTH_LONG).show()
            finish()
        }

        requestPermissionLauncher // load lazy
        
        enableEdgeToEdge()
        setContent {
            ui()
        }
    }

    private inline fun<T> requireSdkOrNull(version: Int, then: () -> T): T? {
        return if(Build.VERSION.SDK_INT >= version) then()
        else null
    }

    private fun requestPermission() {
        when {
            ContextCompat.checkSelfPermission(this, targetPermission!!) == PackageManager.PERMISSION_GRANTED -> {
                Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show()
                finish()
            }
            // lets assume ui already contains the information of permission to request.
            ActivityCompat.shouldShowRequestPermissionRationale(this, targetPermission!!) -> {
                requestPermissionLauncher.launch(targetPermission!!)
            }
            else -> requestPermissionLauncher.launch(targetPermission!!)
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
                requestPermission()
            }) {
                Text(text = "Request permission")
            }
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
