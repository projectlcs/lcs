local _scheduler = {}
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

function loop()
    for i, v in ipairs(registered_tasks) do
        if v:execute() then
            local t, s = v:resume()
            if not t then debug_log("error inside coroutine " .. v.opt.name .. ": " .. s) end
            if coroutine.status(v.task) == "dead" then registered_tasks[i] = nil end
        end
    end
end



-- test
function kt_test(task, ...)
    local as_table = { ... }
    local table_size = #as_table
    local score = -1
    local sel_fn = function() error("No matching argument") end

    if table_size >= 0 then -- check argument
        -- TODO: score check
        local task_score = 0

        if task_score >= score then
            score = task_score

            sel_fn = function(...)
                local coroutine = kt_test_kt1(...) -- get LuaCoroutine instance
                while true do
                    local it = coroutine:next_iter()
                    if it:is_break() then
                        return it:value()
                    end
                    task:yield(function() return it:finished() end)
                end
            end
        end
    end

    return sel_fn(...)
end
