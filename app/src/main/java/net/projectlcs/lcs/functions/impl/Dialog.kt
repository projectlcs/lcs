package net.projectlcs.lcs.functions.impl

import android.content.Context.WINDOW_SERVICE
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import me.ddayo.aris.CoroutineProvider
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import net.projectlcs.lcs.LuaService
import net.projectlcs.lcs.functions.AndroidCoroutineInterop


@LuaProvider
object Dialog : AndroidCoroutineInterop, CoroutineProvider {
    suspend fun <T> SequenceScope<CoroutineProvider.CoroutineReturn<T>>.showYesNoDialog(
        title: String,
        message: String
    ): Int {
        var result = -1

        val context = LuaService.INSTANCE!!
        mainThread {
            val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager

            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
            )

            val overlayView = ComposeView(context).apply {
                setContent {
                    ComposableTest(title, message) { r ->
                        result = r
                        windowManager.removeView(this)
                    }
                }

                setViewTreeLifecycleOwner(context)
                setViewTreeViewModelStoreOwner(context)
                setViewTreeSavedStateRegistryOwner(context)
            }

            params.gravity = Gravity.CENTER
            windowManager.addView(overlayView, params)
        }

        yieldUntil { result != -1 }
        return result
    }

    @LuaFunction("show_yes_no_dialog")
    fun _showYesNoDialog(title: String, message: String) = coroutine {
        breakTask(showYesNoDialog(title, message))
    }
}

@Composable
fun ComposableTest(title: String, message: String, confirm: (Int) -> Unit) {
    Button({
        confirm(1)
    }) {
        Text(title)
    }
    return
    AlertDialog(
        title = {
            Text(text = title)
        },
        text = {
            Text(text = message)
        },
        onDismissRequest = {
            confirm(0)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    confirm(1)
                }
            ) {
                Text("Yes")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { confirm(0) }
            ) {
                Text("No")
            }
        }
    )
}