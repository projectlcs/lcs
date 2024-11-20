package net.projectlcs.lcs.functions.impl

import android.content.Context.WINDOW_SERVICE
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
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
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(0.8f)
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
                // Content
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // OK Button
                    Button(
                        onClick = {
                            confirm(1)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("OK")
                    }
                    // Cancel Button
                    Button(
                        onClick = {
                            confirm(0)
                        }, // Dismiss the dialog
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}