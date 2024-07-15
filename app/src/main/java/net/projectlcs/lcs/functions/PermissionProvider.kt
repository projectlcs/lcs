package net.projectlcs.lcs.functions

interface PermissionProvider {
    fun verifyPermission(): Boolean = true
}