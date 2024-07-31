package net.projectlcs.lcs.functions.impl

import android.util.Log
import net.projectlcs.lcs.ap.LuaFunction
import net.projectlcs.lcs.ap.LuaProvider
import net.projectlcs.lcs.functions.CoroutineProvider

// just for test
@LuaProvider
object Laboratory: CoroutineProvider {
    /*
-- may generated function
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

    if table_size >= 1 then -- check argument
        local task_score = 0
        if task_score >= score then
            score = task_score

            sel_fn = function(...)
                return kt_test_kt2(...) -- todo: check direct return possible
            end
        end
    end

    return sel_fn(...)
end
     */

    // @LuaFunction
    fun test() = coroutine {
        val current = System.currentTimeMillis()
        Log.d("lab", "1")
        yieldUntil { System.currentTimeMillis() - current > 10000 } // wait 10 sec
        Log.d("lab", "2")
        breakTask(10)
        Log.d("lab", "3") // expect not printed
    }
}