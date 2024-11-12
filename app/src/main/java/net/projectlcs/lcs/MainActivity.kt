package net.projectlcs.lcs

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.ddayo.aris.LuaEngine
import net.projectlcs.lcs.ai.PromptEngineering
import net.projectlcs.lcs.data.ScriptDataManager
import net.projectlcs.lcs.data.ScriptReference
import net.projectlcs.lcs.permission.PermissionRequestActivity
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.Call

class MainActivity : ComponentActivity() {
    companion object {
        var context: MainActivity? = null
            private set
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        context = this
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "main") {
                composable("main") { OpenAIApiTest(navController = navController) }
                composable("screen1") { Screen1(navController = navController) }
                composable("screen2") { Screen2(navController = navController) }
                composable("screen3") { Screen3(navController = navController) }
                composable("details/{itemId}") { navBackStackEntry ->
                    DetailsScreen(navController, navBackStackEntry.arguments?.getString("itemId"))
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        // check for overlay permission
        if (!Settings.canDrawOverlays(this)) {
            startActivity(
                Intent(this, PermissionRequestActivity::class.java)
                    .putExtra(
                        PermissionRequestActivity.REQUEST_PERMISSION,
                        PermissionRequestActivity.REQUEST_DRAW_OVERLAY_PERMISSION
                    )
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            finish()
        } else applicationContext.startForegroundService(Intent(this, LuaService::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        context = null
    }
}


//@Preview(showBackground = true)
@Composable
fun OpenAIApiTest(navController: NavController) {
    var isVisible by remember { mutableStateOf(true) }
    var apiResponse by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val functionList = PromptEngineering.functionList
    val keyboardController = LocalSoftwareKeyboardController.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    keyboardController?.hide() // 외부를 클릭하면 키보드를 숨김
                })
            }
    ) {
        Column(modifier = Modifier.padding(32.dp)) {
            //Button({ isVisible = !isVisible }) {}
            Row(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(modifier = Modifier.size(100.dp)) {
                    IconButton(onClick = {
                        //
                    }, modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher),
                            contentDescription = null,
                            modifier = Modifier
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(128.dp))
            Row(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = {
                    isVisible = !isVisible
                }, modifier = Modifier.wrapContentSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_add_circle_outline_24),
                        contentDescription = null,
                        modifier = Modifier
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            AnimatedVisibility(
                visible = isVisible,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.End
                ) {
                    Button(
                        onClick = { navController.navigate("screen1") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2C3E50),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.width(200.dp)
                    ) {
                        Text(text = "스크립트 관리")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { navController.navigate("screen2") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2C3E50),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.width(200.dp)
                    ) {
                        Text(text = "템플릿 더보기")
                    }
                    /*
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { navController.navigate("screen3") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2C3E50), // 딥 블루
                            contentColor = Color.White
                        ),
                        modifier = Modifier.width(200.dp)
                    ) {
                        Text(text = "권한 관리")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            LuaService.runQuery {
                                ScriptDataManager.createNewScript("Script")
                            }
                        }, colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2C3E50), // 딥 블루
                            contentColor = Color.White
                        ),
                        modifier = Modifier.width(200.dp)
                    ) {
                        Text(text = "새 스크립트 생성")
                    }
                     */
                }
            }

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Text(apiResponse)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Row(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("요구사항을 입력하세요") },
                    modifier = Modifier
                )

                //Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.align(Alignment.CenterVertically)) {
                    Button(
                        onClick = {
                            keyboardController?.hide()
                            coroutineScope.launch {
                                isLoading = true
                                apiResponse = try {
                                    var response = testOpenAIApi(
                                        """
당신은 개발의 전문가입니다.
입력된 자연어의 요구사항에 맞는 코드를 실행해야합니다.
작성되어 있는 함수 목록에서 적절한 함수를 조합하여 스크립트를 생성해주세요.
최대한 제공된 함수 목록에서 함수를 작성해주시고, 그 이외 기능은 루아 스크립트의 기본 함수들을 이용해주세요.

제공되는 함수 목록 및 함수 사용 예시: ${PromptEngineering.functionList}

요구사항 : $inputText

코드형식으로 반환해주세요.
주석이나 설명은 빼주세요.
                            """.trimIndent()
                                    )

                                    response = response.removePrefix("```lua")

                                    // Trim the "```" from both ends
                                    response = response.removePrefix("```").removeSuffix("```")
                                    response = response.removePrefix("\n").removeSuffix("\n")
                                    Log.d("OpenAIApiTest", "API 연동 성공: $response")

                                    CoroutineScope(Dispatchers.IO).launch {
                                        val ref = ScriptDataManager.createNewScript("Script")
                                        ref.code = response
                                        ScriptDataManager.updateAllScript(ref)
                                        LuaService.INSTANCE?.apply {
                                            (engine.tasks.firstOrNull { (it as? AndroidLuaEngine.AndroidLuaTask)?.ref?.id == ref.id } as? AndroidLuaEngine.AndroidLuaTask)?.isRunning = true
                                        }
                                    }
                                    // TODO("LuaService.testScript removed. please make full script instead.")
                                    "API 연동 성공: $response"
                                } catch (e: Exception) {
                                    "API 연동 실패: ${e.message}"
                                }
                                isLoading = false
                            }
                        },
                        modifier = Modifier.wrapContentHeight()
                    ) {
                        //Text("test")
                        Image(
                            painter = painterResource(id = R.drawable.baseline_send_24),
                            contentDescription = null,
                            modifier = Modifier
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DetailsScreen(navController: NavController, itemId: String?) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var isDialogOpen by remember { mutableStateOf(false) }
    val taskId = itemId?.toLongOrNull()

    if (taskId == null) {
        // If taskId is invalid, show an error message or navigate back
        Text("Invalid item selected")
        return
    }
    val viewModel: TaskDetailsViewModel =
        viewModel(factory = TaskDetailsViewModelFactory(taskId))

    // Observe task data from ViewModel
    val task by viewModel.task.collectAsState()

    if (task == null) {
        // Loading or invalid task case
        Text("Loading...")
    } else {
        var text by remember { mutableStateOf(task!!.code) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Spacer(modifier = Modifier.padding(top = 16.dp))
            Row(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = {
                        navController.navigateUp()
                    }, modifier = Modifier
                        .padding(top = 16.dp)
                        .zIndex(1f)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        contentDescription = null,
                        modifier = Modifier
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(80.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxHeight()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Code Editor") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    LuaService.runQuery {
                        ScriptDataManager.updateAllScript(task!!.copy(code = text))
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Save")
            }
            Button(
                onClick = {
                    LuaService.runQuery {
                        task!!.isPaused = false
                        ScriptDataManager.updateAllScript(task!!, invalidateExisting = false)
                        LuaService.INSTANCE?.let {
                            val task = it.engine.tasks.firstOrNull { (it as AndroidLuaEngine.AndroidLuaTask).ref.id == task?.id }
                            Log.d("a", task?.taskStatus.toString())
                            if(task?.taskStatus == LuaEngine.TaskStatus.FINISHED)
                                task.restart()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Start")
            }
            Button(
                onClick = {
                    LuaService.runQuery {
                        task!!.isPaused = true
                        ScriptDataManager.updateAllScript(task!!, invalidateExisting = false)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Pause")
            }
            Button(
                onClick = {
                    isDialogOpen = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Delete")
            }
            if (isDialogOpen) {
                AlertDialog(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_restore_from_trash_24),
                            contentDescription = null,
                            modifier = Modifier
                        )
                    },
                    title = {
                        Text(text = "Are you sure you want to delete this script?")
                    },
                    text = {
                        Text(text = "The script will be permanently deleted from the DB. If you want to stop, press the pause button.")
                    },
                    onDismissRequest = {
                        isDialogOpen = false
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                navController.navigateUp()
                                LuaService.runQuery {
                                    ScriptDataManager.deleteAllScript(task!!)
                                }
                                isDialogOpen = false
                            }
                        ) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                isDialogOpen = false
                            }
                        ) {
                            Text("Dismiss")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ViewItem(task: ScriptReference, navController: NavController) {
    val str = task.name
    val isValid = task.isValid
    var isToggle = !task.isPaused && isValid

    if (!task.isValid) isToggle = false
    val icon =
        if (isToggle) R.drawable.baseline_pause_circle_24 else R.drawable.baseline_play_arrow_24
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
    ) {
        Button(
            onClick = { navController.navigate("details/${task.id}") },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2C3E50), // 딥 블루
                contentColor = Color.White
            ),
            modifier = Modifier.width(300.dp),
            shape = RoundedCornerShape(0.dp)
        ) {
            Text(str)
        }
        IconButton(
            onClick = {
                LuaService.INSTANCE?.engine?.let { engine ->
                    for (x in engine.tasks) {
                        val t = x as AndroidLuaEngine.AndroidLuaTask
                        if (t.ref.id != task.id) continue
                        t.isRunning = !t.isRunning
                        task.isPaused = t.isPaused
                        break
                    }
                } ?: run {
                    task.isPaused = !task.isPaused
                }
                LuaService.runQuery {
                    ScriptDataManager.updateAllScript(task, invalidateExisting = false)
                }
            },
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier
            )
        }
        /*Button(
            onClick = {task.isPaused = !task.isPaused},
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFEF5350), // 딥 블루
                contentColor = Color.White
            ),
            ){

            Icon(
                painter = painterResource(id = R.drawable.baseline_pause_circle_outline_24),
                contentDescription = null,
                modifier = Modifier
            )
        }*/
    }
}

@Composable
fun Screen1(navController: NavController) {
    val vm: ScriptViewModel = viewModel()
    val tasks by vm.scripts.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        //verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.padding(top = 16.dp))
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            /*Button(onClick = { navController.navigate("main"){
                popUpTo("screen1") {inclusive=true}
            } }) {
                Text("메인 화면으로 돌아가기")
            }*/
            IconButton(onClick = {
                navController.navigate("main") {
                    popUpTo("screen1") { inclusive = true }
                }
            }, modifier = Modifier.padding(top = 16.dp).zIndex(1f)) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                    contentDescription = null,
                    modifier = Modifier
                )
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Add 5 items
            items(tasks.size) { index ->
                ViewItem(task = tasks[index], navController)
            }
        }
    }
}

@Composable
fun Screen2(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "이것은 화면 2입니다.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            navController.navigate("main") {
                popUpTo("screen2") { inclusive = true }
            }
        }) {
            Text("메인 화면으로 돌아가기")
        }
    }
}

@Composable
fun Screen3(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "이것은 화면 3입니다.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            navController.navigate("main") {
                popUpTo("screen3") { inclusive = true }
            }
        }) {
            Text("메인 화면으로 돌아가기")
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
        model = "gpt-4o",
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

class ScriptViewModel : ViewModel() {
    private val _scripts = MutableStateFlow<List<ScriptReference>>(emptyList())
    val scripts: StateFlow<List<ScriptReference>> = _scripts

    init {
        // Room DB에서 데이터 불러오기
        viewModelScope.launch {
            ScriptDataManager.getAllScripts().collect { scriptList ->
                _scripts.value = scriptList
            }
        }
    }
}

class TaskDetailsViewModel(private val taskId: Long) : ViewModel() {

    private val _task = MutableStateFlow<ScriptReference?>(null)
    val task: StateFlow<ScriptReference?> = _task

    init {
        viewModelScope.launch {
            // Collect task data from the DAO using ScriptDataManager
            ScriptDataManager.getTaskById(taskId).collect { taskData ->
                _task.value = taskData
            }
        }
    }
}

class TaskDetailsViewModelFactory(private val taskId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskDetailsViewModel(taskId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
