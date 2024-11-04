package net.projectlcs.lcs.functions.impl

import android.content.Context.WINDOW_SERVICE
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import me.ddayo.aris.CoroutineProvider
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import net.projectlcs.lcs.LuaService
import net.projectlcs.lcs.functions.AndroidCoroutineInterop

/*

local a = show_yes_no_dialog()
local b = show_selector_dialog("yes", "no") == "yes"
debug_log(a)
 */
@LuaProvider
object Dialog : AndroidCoroutineInterop, CoroutineProvider {
    @LuaFunction("show_yes_no_dialog")
    fun showYesNoDialog() = coroutine {
        var result = -1

        val context = LuaService.INSTANCE!!
        mainThread {
            val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager

            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )

            val overlayView = ComposeView(LuaService.INSTANCE!!).apply {
                setContent {
                    ComposableTest()
                }
            }
/*
            view.findViewById<Button>(R.id.dialog_yes).setOnClickListener {
                result = 1
                Toast.makeText(LuaService.INSTANCE!!, "Approved", Toast.LENGTH_LONG).show()
                windowManager.removeView(view)
            }
            view.findViewById<Button>(R.id.dialog_no).setOnClickListener {
                result = 0
                windowManager.removeView(view)
            }
*/
            params.gravity = Gravity.CENTER or Gravity.CENTER
            params.x = 0
            params.y = 0
            windowManager.addView(overlayView, params)
        }

        yieldUntil { result != -1 }
        breakTask(result)
    }
}

@Composable
fun ComposableTest() {

}