package net.projectlcs.lcs.permission.impl

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import net.projectlcs.lcs.permission.PermissionRequestActivity

object LocationPermission: IPermission() {
    override fun requestPermission(activity: PermissionRequestActivity) {
        val handler = activity.permissionHandler!! as ActivityResultLauncher<Intent>
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", activity.packageName, null),
        )
        handler.launch(intent)
    }

    override fun init(activity: PermissionRequestActivity) =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Settings.canDrawOverlays(activity))
                activity.finish()
        }
}