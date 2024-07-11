package com.teammaso.lcs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.teammaso.lcs.ap.LuaFunction
import com.teammaso.lcs.ui.theme.LCSTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LCSTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        startForegroundService(Intent(this, LuaService::class.java))
    }

    override fun onStart() {
        super.onStart()

        // check for overlay permission
        if (!Settings.canDrawOverlays(this)) {
            val intent =  Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName"))
            startActivity(intent)
        }
    }

    object A {
        @LuaFunction
        fun test2() {
            Log.w("test", "from test2")
        }
    }

    companion object {
        @LuaFunction(name = "hello")
        fun test(x: Double, s: String): Int {
            Log.w("test", "from test")
            return 1
        }
        @LuaFunction(name = "test2")
        fun test2(x: Double): Double {
            return 1.0
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LCSTheme {
        Greeting("Android")
    }
}