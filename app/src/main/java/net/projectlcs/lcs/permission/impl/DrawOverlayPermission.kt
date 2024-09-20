package net.projectlcs.lcs.permission.impl

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import net.projectlcs.lcs.permission.PermissionRequestActivity

class DrawOverlayPermission : IPermission() {
    override fun requestPermission(activity: PermissionRequestActivity) {
        val handler = activity.permissionHandler!! as ActivityResultLauncher<Intent>
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${activity.packageName}")
        )
        handler.launch(intent)
        Toast.makeText(activity, "Restart application after enabling setting", Toast.LENGTH_LONG)
            .show()
    }

    override fun init(activity: PermissionRequestActivity) =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Settings.canDrawOverlays(activity))
                activity.finish()
        }
}