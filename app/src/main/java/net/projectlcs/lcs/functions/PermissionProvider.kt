package net.projectlcs.lcs.functions

import android.content.Intent
import android.widget.Toast
import me.ddayo.aris.CoroutineProvider
import me.ddayo.aris.CoroutineProvider.CoroutineReturn
import net.projectlcs.lcs.LuaService
import net.projectlcs.lcs.permission.PermissionRequestActivity
import kotlin.experimental.ExperimentalTypeInference

interface PermissionProvider: CoroutineProvider, AndroidCoroutineInterop {
    fun verifyPermission(): Boolean
    fun requestPermission()

    fun startPermissionActivity(tag: Int) {
        LuaService.INSTANCE!!.startActivity(
            Intent(LuaService.INSTANCE!!, PermissionRequestActivity::class.java)
            .putExtra(PermissionRequestActivity.REQUEST_PERMISSION, tag)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    @OptIn(ExperimentalTypeInference::class)
    suspend fun<T> SequenceScope<CoroutineReturn<T>>.requestPermission(@BuilderInference then: suspend SequenceScope<CoroutineReturn<T>>.() -> Unit) {
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