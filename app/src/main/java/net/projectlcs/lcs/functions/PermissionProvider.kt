package net.projectlcs.lcs.functions

interface PermissionProvider {
    /**
     * @param tryRequest true if to open permission request window
     */
    fun verifyPermission(tryRequest: Boolean): Boolean = true
}