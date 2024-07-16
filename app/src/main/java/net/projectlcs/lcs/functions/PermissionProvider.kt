package net.projectlcs.lcs.functions

// DO NOT RENAME OR MOVE PACKAGE OF THIS INTERFACE.
// THIS IS USED INTERNALLY AT ANNOTATION PROCESSOR.
interface PermissionProvider {
    /**
     * @param tryRequest true if to open permission request window
     */
    fun verifyPermission(tryRequest: Boolean): Boolean = true
}