package net.projectlcs.lcs.permission.impl

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import net.projectlcs.lcs.permission.PermissionRequestActivity

class DangerousPermission(val permission: String): IPermission(true) {
    override fun requestPermission(activity: PermissionRequestActivity) {
        val handler = activity.permissionHandler!! as ActivityResultLauncher<String>
        when {
            ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED -> {
                Toast.makeText(activity, "Permission already granted", Toast.LENGTH_SHORT).show()
                activity.finish()
            }
            // lets assume ui already contains the information of permission to request.
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) -> {
                handler.launch(permission)
            }
            else -> handler.launch(permission)
        }
    }

    override fun init(activity: PermissionRequestActivity) = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            activity.finish()
        }
    }
}
