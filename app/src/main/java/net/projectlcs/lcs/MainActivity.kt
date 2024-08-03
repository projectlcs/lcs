package net.projectlcs.lcs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.projectlcs.lcs.theme.LCSTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

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
            OpenAIApiTest()
        }
    }

    override fun onStart() {
        super.onStart()

        // check for overlay permission
        if (!Settings.canDrawOverlays(this)) {
            val intent =  Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName"))
            startActivity(intent)
            Toast.makeText(this, "Restart application after enabling setting", Toast.LENGTH_LONG).show()
        }
        else applicationContext.startForegroundService(Intent(this, LuaService::class.java))
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



@Composable
fun OpenAIApiTest() {
    var apiResponse by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("요구사항을 입력하세요") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    isLoading = true
                    apiResponse = try {
                        val response = testOpenAIApi(
                            """
                            안드로이드 스튜디오에서 작업중입니다. 
                            맨 아래의 요구사항에는 안드로이드에서 사용가능한 기능을 자연어로 입력될것 입니다. 
                            요구사항에 맞는 루아 스크립트를 작성해주세요. 
                            또한 다른 설명없이 코드만 대답하면 됩니다.
                            요구사항 : $inputText
                            """.trimIndent()
                        )
                        Log.d("OpenAIApiTest", "API 연동 성공: $response")
                        "API 연동 성공: $response"
                    } catch (e: Exception) {
                        "API 연동 실패: ${e.message}"
                    }
                    isLoading = false
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("OpenAI API 테스트")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Text(apiResponse)
        }
    }
}

suspend fun testOpenAIApi(prompt: String): String {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openai.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(OpenAIService::class.java)
    val request = ChatCompletionRequest(
        model = "gpt-4o-mini",
        messages = listOf(ChatMessage(role = "user", content = prompt))
    )
    // 여기에 자신의 API 키를 입력하세요
    val apiKey = "sk-proj-aPmdKj1RPjf0adKxcHoAT3BlbkFJR8cSXBgsLOtz3dgQ0HGB"
    val response = service.createChatCompletion("Bearer $apiKey", request)
    return response.choices.firstOrNull()?.message?.content ?: "No response"
}

interface OpenAIService {
    @POST("chat/completions")
    suspend fun createChatCompletion(
        @Header("Authorization") authorization: String,
        @Body request: ChatCompletionRequest
    ): ChatCompletionResponse
}

data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatMessage>
)

data class ChatCompletionResponse(
    val choices: List<ChatChoice>
)

data class ChatChoice(
    val message: ChatMessage
)

data class ChatMessage(
    val role: String,
    val content: String
)
