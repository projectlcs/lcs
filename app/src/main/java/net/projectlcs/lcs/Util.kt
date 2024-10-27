package net.projectlcs.lcs

import android.os.Build

object Util {

    inline fun <T> requireSdk(version: Int, then: () -> T, not: () -> T): T {
        return if (Build.VERSION.SDK_INT >= version) then()
        else not()
    }

    inline fun <T> requireSdkOrNull(version: Int, then: () -> T) = requireSdk(version, then = then, not = { null })
}