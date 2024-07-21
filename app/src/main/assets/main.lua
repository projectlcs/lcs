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

function _scheduler:yield()
    self.execute = true_fn
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
