package net.projectlcs.lcs.functions

import android.widget.Toast
import me.ddayo.aris.CoroutineProvider
import me.ddayo.aris.CoroutineProvider.CoroutineReturn
import net.projectlcs.lcs.LuaService

interface PermissionProvider: CoroutineProvider, AndroidCoroutineInterop {
    fun verifyPermission(): Boolean
    fun requestPermission()

    suspend fun<T> SequenceScope<CoroutineReturn<T>>.requestPermission(then: suspend SequenceScope<CoroutineReturn<T>>.() -> Unit) {
        val beginTime = System.currentTimeMillis()
        var isTimeout = false

        if(!verifyPermission()) {
            requestPermission()
            yieldUntil {
                if (System.currentTimeMillis() - beginTime > 60000) {
                    mainThread {
                        Toast.makeText(
                            LuaService.INSTANCE!!.applicationContext,
                            "Permission request timeout!!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    isTimeout = true
                    true
                } else verifyPermission()
            }
        }

        if(!isTimeout)
            then()
    }
}