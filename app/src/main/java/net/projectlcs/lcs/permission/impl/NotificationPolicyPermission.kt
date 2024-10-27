package net.projectlcs.lcs.permission.impl

import android.content.Intent
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import net.projectlcs.lcs.functions.impl.DND
import net.projectlcs.lcs.permission.PermissionRequestActivity

object NotificationPolicyPermission : IPermission() {
    override fun init(activity: PermissionRequestActivity) =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(DND.verifyPermission())
                activity.finish()
        }

    override fun requestPermission(activity: PermissionRequestActivity) {
        val handler = activity.permissionHandler!! as ActivityResultLauncher<Intent>
        handler.launch(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
    }
}