package com.teammaso.lcs

import android.os.Bundle
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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

        GlobalScope.launch (Dispatchers.Default) {
            LuaHandler.createInstance()
            Log.d("test", "Finished!!")
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