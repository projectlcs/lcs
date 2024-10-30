package net.projectlcs.lcs

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import net.projectlcs.lcs.data.ScriptDataManager
import net.projectlcs.lcs.data.ScriptReference
import net.projectlcs.lcs.permission.PermissionRequestActivity
import net.projectlcs.lcs.theme.LCSTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


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
    val functionList = """
        package me.ddayo.aris.gen

        import me.ddayo.aris.luagen.LuaMultiReturn
        import party.iroiro.luajava.Lua
        import party.iroiro.luajava.LuaException
        import party.iroiro.luajava.luajit.LuaJit

        object LuaGenerated {
            fun <T> push(lua: Lua, it: T) {
                when(it) {
                    null -> lua.pushNil()
                    is Number -> lua.push(it)
                    is Boolean -> lua.push(it)
                    is String -> lua.push(it)
                    is Map<*, *> -> lua.push(it)
                    is Class<*> -> lua.pushJavaClass(it)
                    else -> lua.pushJavaObject(it as Any)
                }
            }
            
            fun initLua(lua: LuaJit) {
                lua.push { lua ->
                    val r = lua.get().toJavaObject() as? LuaMultiReturn
                    r?.luaFn(lua) ?: 0
                }
                lua.setGlobal("resolve_mrt")

        lua.push { lua ->
        val arg = (0 until lua.top).map { lua.get() }.reversed()
        net.projectlcs.lcs.functions.impl.Alarms.createAlarm(arg[0].toInteger().toInt(), arg[1].toInteger().toInt())
        0
        }
        lua.setGlobal("create_alarm_kt0")

        lua.push { lua ->
        val arg = (0 until lua.top).map { lua.get() }.reversed()
        val rt = listOf(net.projectlcs.lcs.functions.impl.DND.setDND(arg[0].toJavaObject() as Boolean))
        rt.forEach { push(lua, it) }
        rt.size
        }
        lua.setGlobal("set_do_not_disturb_kt0")

        lua.push { lua ->
        val rt = listOf(net.projectlcs.lcs.functions.impl.DND.getDND())
        rt.forEach { push(lua, it) }
        rt.size
        }
        lua.setGlobal("get_do_not_disturb_kt0")

        lua.push { lua ->
        val rt = listOf(net.projectlcs.lcs.functions.impl.Laboratory.test())
        rt.forEach { push(lua, it) }
        rt.size
        }
        lua.setGlobal("test_kt0")

        lua.push { lua ->
        val arg = (0 until lua.top).map { lua.get() }.reversed()
        val rt = listOf(net.projectlcs.lcs.functions.impl.Logger.printDebug(arg[0].toString()))
        rt.forEach { push(lua, it) }
        rt.size
        }
        lua.setGlobal("debug_log_kt0")
        lua.push { lua ->
        val arg = (0 until lua.top).map { lua.get() }.reversed()
        val rt = listOf(net.projectlcs.lcs.functions.impl.Logger.printDebug(arg[0].toString(), arg[1].toString()))
        rt.forEach { push(lua, it) }
        rt.size
        }
        lua.setGlobal("debug_log_kt1")

        lua.push { lua ->
        val arg = (0 until lua.top).map { lua.get() }.reversed()
        val rt = listOf(net.projectlcs.lcs.functions.impl.Network.sendGetRequest(arg[0].toString()))
        rt.forEach { push(lua, it) }
        rt.size
        }
        lua.setGlobal("send_web_request_kt0")

        lua.push { lua ->
        val arg = (0 until lua.top).map { lua.get() }.reversed()
        val rt = listOf(net.projectlcs.lcs.functions.impl.Network.downloadFile(arg[0].toString(), arg[1].toString()))
        rt.forEach { push(lua, it) }
        rt.size
        }
        lua.setGlobal("download_file_kt0")

        lua.push { lua ->
        val arg = (0 until lua.top).map { lua.get() }.reversed()
        val rt = listOf(net.projectlcs.lcs.functions.impl.Notification.sendNotification(arg[0].toString(), arg[1].toString()))
        rt.forEach { push(lua, it) }
        rt.size
        }
        lua.setGlobal("send_notification_kt0")

        lua.push { lua ->
        val arg = (0 until lua.top).map { lua.get() }.reversed()
        net.projectlcs.lcs.functions.impl.Notification.sendToastLong(arg[0].toString())
        0
        }
        lua.setGlobal("send_long_toast_kt0")

        lua.push { lua ->
        val arg = (0 until lua.top).map { lua.get() }.reversed()
        net.projectlcs.lcs.functions.impl.Notification.sendToast(arg[0].toString())
        0
        }
        lua.setGlobal("send_toast_kt0")

        lua.push { lua ->
        val arg = (0 until lua.top).map { lua.get() }.reversed()
        net.projectlcs.lcs.functions.impl.OpenApplication.openApp(arg[0].toString())
        0
        }
        lua.setGlobal("open_app_kt0")

        lua.push { lua ->
        val arg = (0 until lua.top).map { lua.get() }.reversed()
        net.projectlcs.lcs.functions.impl.OpenApplication.openUrl(arg[0].toString())
        0
        }
        lua.setGlobal("open_url_kt0")

        lua.push { lua ->
        val rt = listOf(net.projectlcs.lcs.functions.impl.Util.getTime())
        rt.forEach { push(lua, it) }
        rt.size
        }
        lua.setGlobal("get_time_kt0")

                lua.load(""${'"'}local _scheduler = {}
        _scheduler.__index = _scheduler

        local function true_fn() return true end

        -- register new coroutine
        function _scheduler:new(fn, opt)
            local o = {}
            o.opt = opt
            o.execute = true_fn
            setmetatable(o, self)
            o.task = coroutine.create(fn)
            return o
        end

        function _scheduler:resume(...)
            self.execute = nil
            return coroutine.resume(self.task, self, ...)
        end

        function _scheduler:sleep(time)
            local current_time = get_time()
            self.execute = function() return get_time() - current_time > time end
            coroutine.yield(self)
        end

        function _scheduler:yield(fn) -- the most of use case of parameter `fn` is for coroutine integration
            self.execute = fn or true_fn
            coroutine.yield(self)
        end

        Scheduler = _scheduler -- move it to global

        local registered_tasks = {}
        function register_task(script, name)
            if(name == nil) then name = "lua" end
            local fn = load("return function(task)\n" .. script .. "\nend", name, "t", _G)()
            registered_tasks[#registered_tasks+1] = Scheduler:new(fn, { name = name })
        end

        local current_task
        function get_current_task() return current_task end

        function loop()
            for i, v in ipairs(registered_tasks) do
                if v:execute() then
                    current_task = v
                    local t, s = v:resume()
                    if not t then debug_log(nil, "error inside coroutine " .. v.opt.name .. ": " .. s) end
                    if coroutine.status(v.task) == "dead" then registered_tasks[i] = nil end
                end
            end
        end

        function create_alarm(...)
            local as_table = { ... }
            local table_size = #as_table
            local score = -1
            local sel_fn = function() error("No matching argument") end
        if table_size >= 2 then
            local task_score = 0
            if task_score >= score then
                score = task_score
                sel_fn = function(...)
        return create_alarm_kt0(...)
                end
            end
        end

            return sel_fn(...)
        end


        function set_do_not_disturb(...)
            local as_table = { ... }
            local table_size = #as_table
            local score = -1
            local sel_fn = function() error("No matching argument") end
        if table_size >= 1 then
            local task_score = 0
            if task_score >= score then
                score = task_score
                sel_fn = function(...)
        local coroutine = set_do_not_disturb_kt0(...) -- get LuaCoroutine instance
        local cur_task = get_current_task()
        while true do
            local it = coroutine:next_iter()
            if it:is_break() then
                return it:value()
            end
            cur_task:yield(function() return it:finished() end)
        end
                end
            end
        end

            return sel_fn(...)
        end


        function get_do_not_disturb(...)
            local as_table = { ... }
            local table_size = #as_table
            local score = -1
            local sel_fn = function() error("No matching argument") end
        if table_size >= 0 then
            local task_score = 0
            if task_score >= score then
                score = task_score
                sel_fn = function(...)
        local coroutine = get_do_not_disturb_kt0(...) -- get LuaCoroutine instance
        local cur_task = get_current_task()
        while true do
            local it = coroutine:next_iter()
            if it:is_break() then
                return resolve_mrt(it:value())
            end
            cur_task:yield(function() return it:finished() end)
        end
                end
            end
        end

            return sel_fn(...)
        end


        function test(...)
            local as_table = { ... }
            local table_size = #as_table
            local score = -1
            local sel_fn = function() error("No matching argument") end
        if table_size >= 0 then
            local task_score = 0
            if task_score >= score then
                score = task_score
                sel_fn = function(...)
        local coroutine = test_kt0(...) -- get LuaCoroutine instance
        local cur_task = get_current_task()
        while true do
            local it = coroutine:next_iter()
            if it:is_break() then
                return resolve_mrt(it:value())
            end
            cur_task:yield(function() return it:finished() end)
        end
                end
            end
        end

            return sel_fn(...)
        end


        function debug_log(...)
            local as_table = { ... }
            local table_size = #as_table
            local score = -1
            local sel_fn = function() error("No matching argument") end
        if table_size >= 1 then
            local task_score = 0
            if task_score >= score then
                score = task_score
                sel_fn = function(...)
        return debug_log_kt0(...)
                end
            end
        end
        if table_size >= 2 then
            local task_score = 0
            if task_score >= score then
                score = task_score
                sel_fn = function(...)
        return debug_log_kt1(...)
                end
            end
        end

            return sel_fn(...)
        end


        function send_web_request(...)
            local as_table = { ... }
            local table_size = #as_table
            local score = -1
            local sel_fn = function() error("No matching argument") end
        if table_size >= 1 then
            local task_score = 0
            if task_score >= score then
                score = task_score
                sel_fn = function(...)
        local coroutine = send_web_request_kt0(...) -- get LuaCoroutine instance
        local cur_task = get_current_task()
        while true do
            local it = coroutine:next_iter()
            if it:is_break() then
                return resolve_mrt(it:value())
            end
            cur_task:yield(function() return it:finished() end)
        end
                end
            end
        end

            return sel_fn(...)
        end


        function download_file(...)
            local as_table = { ... }
            local table_size = #as_table
            local score = -1
            local sel_fn = function() error("No matching argument") end
        if table_size >= 2 then
            local task_score = 0
            if task_score >= score then
                score = task_score
                sel_fn = function(...)
        local coroutine = download_file_kt0(...) -- get LuaCoroutine instance
        local cur_task = get_current_task()
        while true do
            local it = coroutine:next_iter()
            if it:is_break() then
                return resolve_mrt(it:value())
            end
            cur_task:yield(function() return it:finished() end)
        end
                end
            end
        end

            return sel_fn(...)
        end


        function send_notification(...)
            local as_table = { ... }
            local table_size = #as_table
            local score = -1
            local sel_fn = function() error("No matching argument") end
        if table_size >= 2 then
            local task_score = 0
            if task_score >= score then
                score = task_score
                sel_fn = function(...)
        local coroutine = send_notification_kt0(...) -- get LuaCoroutine instance
        local cur_task = get_current_task()
        while true do
            local it = coroutine:next_iter()
            if it:is_break() then
                return it:value()
            end
            cur_task:yield(function() return it:finished() end)
        end
                end
            end
        end

            return sel_fn(...)
        end


        function send_long_toast(...)
            local as_table = { ... }
            local table_size = #as_table
            local score = -1
            local sel_fn = function() error("No matching argument") end
        if table_size >= 1 then
            local task_score = 0
            if task_score >= score then
                score = task_score
                sel_fn = function(...)
        return send_long_toast_kt0(...)
                end
            end
        end

            return sel_fn(...)
        end


        function send_toast(...)
            local as_table = { ... }
            local table_size = #as_table
            local score = -1
            local sel_fn = function() error("No matching argument") end
        if table_size >= 1 then
            local task_score = 0
            if task_score >= score then
                score = task_score
                sel_fn = function(...)
        return send_toast_kt0(...)
                end
            end
        end

            return sel_fn(...)
        end


        function open_app(...)
            local as_table = { ... }
            local table_size = #as_table
            local score = -1
            local sel_fn = function() error("No matching argument") end
        if table_size >= 1 then
            local task_score = 0
            if task_score >= score then
                score = task_score
                sel_fn = function(...)
        return open_app_kt0(...)
                end
            end
        end

            return sel_fn(...)
        end


        function open_url(...)
            local as_table = { ... }
            local table_size = #as_table
            local score = -1
            local sel_fn = function() error("No matching argument") end
        if table_size >= 1 then
            local task_score = 0
            if task_score >= score then
                score = task_score
                sel_fn = function(...)
        return open_url_kt0(...)
                end
            end
        end

            return sel_fn(...)
        end


        function get_time(...)
            local as_table = { ... }
            local table_size = #as_table
            local score = -1
            local sel_fn = function() error("No matching argument") end
        if table_size >= 0 then
            local task_score = 0
            if task_score >= score then
                score = task_score
                sel_fn = function(...)
        return get_time_kt0(...)
                end
            end
        end

            return sel_fn(...)
        end
        ""${'"'})
        lua.pCall(0, 0)
            }
        }


    """.trimIndent()
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
                            안드로이드 스튜디오에서 작업중입니다. 
                            맨 아래의 요구사항에는 안드로이드에서 사용가능한 기능을 자연어로 입력될것 입니다. 
                            요구사항에 맞는 루아 스크립트를 작성해주세요. 
                         
                            
                            작성되어 있는 함수 목록 : $functionList
                            
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

                                    LuaService.testScript = response
                                    MainActivity.context?.run {
                                        stopService(Intent(this, LuaService::class.java))
                                        startForegroundService(Intent(this, LuaService::class.java))
                                    }
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
        Column {
            IconButton(onClick = {
                navController.navigateUp()
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                    contentDescription = null,
                    modifier = Modifier
                )
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(80.dp)
                .fillMaxHeight()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Code Editor") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            ScriptDataManager.updateAllScript(task!!.copy(code = text))
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Save")
                }
            }
            item {
                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            task!!.isPaused = false
                            ScriptDataManager.updateAllScript(task!!, invalidateExisting = false)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Start")
                }
            }
            item {
                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            task!!.isPaused = true
                            ScriptDataManager.updateAllScript(task!!)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Pause")
                }
            }
            item {
                Button(
                    onClick = {
                        navController.navigateUp()
                        CoroutineScope(Dispatchers.IO).launch {
                            ScriptDataManager.deleteAllScript(task!!)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Delete")
                }
            }
        }
    }
}

@Composable
fun ViewItem(task: ScriptReference, navController: NavController) {
    val str = task.name
    var isToggle by remember { mutableStateOf(!task.isPaused) }
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
                CoroutineScope(Dispatchers.IO).launch {
                    task.isPaused = !task.isPaused
                    ScriptDataManager.updateAllScript(task, invalidateExisting = false)
                }

                isToggle = !isToggle
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
            }) {
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
