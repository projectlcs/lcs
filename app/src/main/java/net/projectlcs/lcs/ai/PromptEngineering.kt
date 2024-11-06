package net.projectlcs.lcs.ai

object PromptEngineering {
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
}