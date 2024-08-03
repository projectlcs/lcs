package net.projectlcs.lcs.functions.impl

import android.content.Intent
import android.net.Uri
import android.util.Log
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import net.projectlcs.lcs.LuaService

@LuaProvider
object OpenApplication {
    @LuaFunction(name = "open_app")
    fun openApp(pkg: String) {
        val service = LuaService.INSTANCE!!
        service.packageManager?.getLaunchIntentForPackage(pkg)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            service.startActivity(this)
        } ?: Log.w("OpenApplication", "Application $pkg not found")
    }

    @LuaFunction(name = "open_url")
    fun openUrl(url: String) {
        val service = LuaService.INSTANCE!!
        service.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}