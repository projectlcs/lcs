package net.projectlcs.lcs.permission.impl

import androidx.activity.result.ActivityResultLauncher
import net.projectlcs.lcs.permission.PermissionRequestActivity

abstract class IPermission(val useSettingsButton: Boolean = false) {
    abstract fun requestPermission(activity: PermissionRequestActivity)
    open fun init(activity: PermissionRequestActivity): ActivityResultLauncher<*>? = null
}