package net.projectlcs.lcs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
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
import net.projectlcs.lcs.permission.PermissionRequestActivity
import net.projectlcs.lcs.theme.LCSTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

class MainActivity : ComponentActivity() {
    companion object {
        var context: Context? = null
            private set
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        context = this
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
            startActivity(Intent(this, PermissionRequestActivity::class.java)
                .putExtra(PermissionRequestActivity.REQUEST_PERMISSION, PermissionRequestActivity.REQUEST_DRAW_OVERLAY_PERMISSION)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            finish()
        }
        else applicationContext.startForegroundService(Intent(this, LuaService::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        context = null
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
    val functionList="""
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
