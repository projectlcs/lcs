package net.projectlcs.lcs.permission.impl

import net.projectlcs.lcs.data.ScriptDataManager
import net.projectlcs.lcs.permission.PermissionRequestActivity

/**
 * This class is not thread-safe
 */
class LocalFileAccessPermission : IPermission() {
    companion object {
        const val TARGET_DIR = "target_directory"
    }

    private lateinit var targetDir: String

    override fun requestPermission(activity: PermissionRequestActivity) {
        val ref = activity.ref!!
        activity.intent.getStringExtra(TARGET_DIR)
        ref.storageAccess.add(targetDir)
        ScriptDataManager.updateAllScript(ref)
        // TODO
    }
}