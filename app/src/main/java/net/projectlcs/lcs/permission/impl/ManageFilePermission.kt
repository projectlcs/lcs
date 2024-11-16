package net.projectlcs.lcs.permission.impl

import android.content.Intent
import android.os.Environment
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import net.projectlcs.lcs.permission.PermissionRequestActivity

object ManageFilePermission: IPermission() {
    override fun requestPermission(activity: PermissionRequestActivity) {
        val handler = activity.permissionHandler!! as ActivityResultLauncher<Intent>
        val intent = Intent(
            Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
        )
        handler.launch(intent)
    }

    override fun init(activity: PermissionRequestActivity) =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Environment.isExternalStorageManager())
                activity.finish()
        }
}