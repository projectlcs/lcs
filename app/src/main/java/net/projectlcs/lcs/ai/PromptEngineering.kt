package net.projectlcs.lcs.ai

object PromptEngineering {
    val functionList = """
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
while true do
    local it = coroutine:next_iter()
    if it:is_break() then
        return it:value()
    end
    task_yield(function() return it:finished() end)
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
while true do
    local it = coroutine:next_iter()
    if it:is_break() then
        return it:value()
    end
    task_yield(function() return it:finished() end)
end
        end
    end
end

    return sel_fn(...)
end

function show_yes_no_dialog(...)
    local as_table = { ... }
    local table_size = #as_table
    local score = -1
    local sel_fn = function() error("No matching argument") end
if table_size >= 2 then
    local task_score = 0
    if task_score >= score then
        score = task_score
        sel_fn = function(...)
local coroutine = show_yes_no_dialog_kt0(...) -- get LuaCoroutine instance
while true do
    local it = coroutine:next_iter()
    if it:is_break() then
        return it:value()
    end
    task_yield(function() return it:finished() end)
end
        end
    end
end

    return sel_fn(...)
end

function create_file(...)
    local as_table = { ... }
    local table_size = #as_table
    local score = -1
    local sel_fn = function() error("No matching argument") end
if table_size >= 1 then
    local task_score = 0
    if task_score >= score then
        score = task_score
        sel_fn = function(...)
return create_file_kt0(...)
        end
    end
end

    return sel_fn(...)
end

function delete_file(...)
    local as_table = { ... }
    local table_size = #as_table
    local score = -1
    local sel_fn = function() error("No matching argument") end
if table_size >= 1 then
    local task_score = 0
    if task_score >= score then
        score = task_score
        sel_fn = function(...)
return delete_file_kt0(...)
        end
    end
end

    return sel_fn(...)
end

function write_file(...)
    local as_table = { ... }
    local table_size = #as_table
    local score = -1
    local sel_fn = function() error("No matching argument") end
if table_size >= 2 then
    local task_score = 0
    if task_score >= score then
        score = task_score
        sel_fn = function(...)
return write_file_kt0(...)
        end
    end
end

    return sel_fn(...)
end

function append_file(...)
    local as_table = { ... }
    local table_size = #as_table
    local score = -1
    local sel_fn = function() error("No matching argument") end
if table_size >= 2 then
    local task_score = 0
    if task_score >= score then
        score = task_score
        sel_fn = function(...)
return append_file_kt0(...)
        end
    end
end

    return sel_fn(...)
end

function read_file(...)
    local as_table = { ... }
    local table_size = #as_table
    local score = -1
    local sel_fn = function() error("No matching argument") end
if table_size >= 1 then
    local task_score = 0
    if task_score >= score then
        score = task_score
        sel_fn = function(...)
return read_file_kt0(...)
        end
    end
end

    return sel_fn(...)
end

function create_file_global(...)
    local as_table = { ... }
    local table_size = #as_table
    local score = -1
    local sel_fn = function() error("No matching argument") end
if table_size >= 1 then
    local task_score = 0
    if task_score >= score then
        score = task_score
        sel_fn = function(...)
local coroutine = create_file_global_kt0(...) -- get LuaCoroutine instance
while true do
    local it = coroutine:next_iter()
    if it:is_break() then
        return it:value()
    end
    task_yield(function() return it:finished() end)
end
        end
    end
end

    return sel_fn(...)
end

function delete_file_global(...)
    local as_table = { ... }
    local table_size = #as_table
    local score = -1
    local sel_fn = function() error("No matching argument") end
if table_size >= 1 then
    local task_score = 0
    if task_score >= score then
        score = task_score
        sel_fn = function(...)
local coroutine = delete_file_global_kt0(...) -- get LuaCoroutine instance
while true do
    local it = coroutine:next_iter()
    if it:is_break() then
        return it:value()
    end
    task_yield(function() return it:finished() end)
end
        end
    end
end

    return sel_fn(...)
end

function write_file_global(...)
    local as_table = { ... }
    local table_size = #as_table
    local score = -1
    local sel_fn = function() error("No matching argument") end
if table_size >= 2 then
    local task_score = 0
    if task_score >= score then
        score = task_score
        sel_fn = function(...)
local coroutine = write_file_global_kt0(...) -- get LuaCoroutine instance
while true do
    local it = coroutine:next_iter()
    if it:is_break() then
        return it:value()
    end
    task_yield(function() return it:finished() end)
end
        end
    end
end

    return sel_fn(...)
end

function append_file_global(...)
    local as_table = { ... }
    local table_size = #as_table
    local score = -1
    local sel_fn = function() error("No matching argument") end
if table_size >= 2 then
    local task_score = 0
    if task_score >= score then
        score = task_score
        sel_fn = function(...)
local coroutine = append_file_global_kt0(...) -- get LuaCoroutine instance
while true do
    local it = coroutine:next_iter()
    if it:is_break() then
        return it:value()
    end
    task_yield(function() return it:finished() end)
end
        end
    end
end

    return sel_fn(...)
end

function read_file_global(...)
    local as_table = { ... }
    local table_size = #as_table
    local score = -1
    local sel_fn = function() error("No matching argument") end
if table_size >= 1 then
    local task_score = 0
    if task_score >= score then
        score = task_score
        sel_fn = function(...)
local coroutine = read_file_global_kt0(...) -- get LuaCoroutine instance
while true do
    local it = coroutine:next_iter()
    if it:is_break() then
        return it:value()
    end
    task_yield(function() return it:finished() end)
end
        end
    end
end

    return sel_fn(...)
end

function get_location(...)
    local as_table = { ... }
    local table_size = #as_table
    local score = -1
    local sel_fn = function() error("No matching argument") end
if table_size >= 0 then
    local task_score = 0
    if task_score >= score then
        score = task_score
        sel_fn = function(...)
local coroutine = get_location_kt0(...) -- get LuaCoroutine instance
while true do
    local it = coroutine:next_iter()
    if it:is_break() then
        return it:value()
    end
    task_yield(function() return it:finished() end)
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
while true do
    local it = coroutine:next_iter()
    if it:is_break() then
        return it:value()
    end
    task_yield(function() return it:finished() end)
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
while true do
    local it = coroutine:next_iter()
    if it:is_break() then
        return it:value()
    end
    task_yield(function() return it:finished() end)
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
while true do
    local it = coroutine:next_iter()
    if it:is_break() then
        return it:value()
    end
    task_yield(function() return it:finished() end)
end
        end
    end
end
if table_size >= 3 then
    local task_score = 0
    if task_score >= score then
        score = task_score
        sel_fn = function(...)
local coroutine = send_notification_kt1(...) -- get LuaCoroutine instance
while true do
    local it = coroutine:next_iter()
    if it:is_break() then
        return it:value()
    end
    task_yield(function() return it:finished() end)
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

function SubwayApiTest(...)
    local as_table = { ... }
    local table_size = #as_table
    local score = -1
    local sel_fn = function() error("No matching argument") end
if table_size >= 0 then
    local task_score = 0
    if task_score >= score then
        score = task_score
        sel_fn = function(...)
local coroutine = SubwayApiTest_kt0(...) -- get LuaCoroutine instance
while true do
    local it = coroutine:next_iter()
    if it:is_break() then
        return it:value()
    end
    task_yield(function() return it:finished() end)
end
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



함수 사용 예시:
## create_alarm(hour: Int, minutes: Int)
 createAlarm(15, 12) // set new alarm at 3:12 PM

 @param hour time of hour to set alarm
 @param minutes time of minutes to set alarm



## set_do_not_disturb(newValue: Boolean)
 @param newValue new do not disturb state



## get_do_not_disturb()
 @return current do not disturb status


## create_file(name: String)
 Create file inside application-data directory
 @param name filename to create



## delete_file(name: String)
 Delete file inside application-data directory
 @param name filename to delete



## write_file(name: String, text: String)
 Write(overwrite) to file inside application-data directory
 @param name filename to write
 @param text text to write



## append_file(name: String, text: String)
 Write(append) to file inside application-data directory
 @param name filename to append
 @param text text to write



## read_file(name: String)
 Read the file inside application-data directory
 @param name name of file to read
 @return content of given file



## create_file_global(name: String)
 Create file inside global(external) storage. This requires MANAGE_EXTERNAL_STORAGE permission
 @param name filename to create



## delete_file_global(name: String)
 Delete file inside global(external) storage. This requires MANAGE_EXTERNAL_STORAGE permission
 @param name filename to create


## write_file_global(name: String, text: String)
 Write(overwrite) file inside global(external) storage. This requires MANAGE_EXTERNAL_STORAGE permission
 @param name filename to create
 @param text text to write



## append_file_global(name: String, text: String)
 Write(append) file inside global(external) storage. This requires MANAGE_EXTERNAL_STORAGE permission
 @param name filename to create
 @param text text to write



## read_file_global(name: String)
 Read file inside global(external) storage. This requires MANAGE_EXTERNAL_STORAGE permission
 @param name filename to create
 @return content of given file



## get_location()
 @return This function returns three values: latitude, longitude, accuracy

## location_delta_to_meter(latitude1, longitude1, latitude2, longitude2)
 This function accepts two location value and calculates the delta of two locations in meter.
 @param latitude1 latitude of first location
 @param longitude1 longitude of first location
 @param latitude2 latitude of second location
 @param longitude2 longitude of second location
 @return delta of two location in meter value


## debug_log(message: String)
## debug_log(tag: String, message: String)


## send_web_request(url: String)
 Send HTTP Get request then retrieve response code and its data

 @param url target url
 @return response_code: Int, response_data: String



## download_file(url: String, name: String)
 Download file via HTTP Get request at provided name.
 If body is empty then it just creates empty file

 @param url target url
 @param name file name to save
 @return response_code: Int


## send_notification(task: AndroidLuaTask, message: String)
 Send notification with existing task

 @param task the LuaTask. just use `task` which already passed to function.
 @param message message to send

## send_notification(task: AndroidLuaTask, title: String, text: String)
 Send notification with provided title and text

 @param task the LuaTask. just use `task` which already passed to function.
 @param title title of notification
 @param text inner text of notification


## send_long_toast(text: String)
 Send long toast message

 @param text text to displayed by long toast message



## send_toast(text: String)
Send toast message

 @param text text to displayed by toast message



## open_app(pkg: String)
 Open application by package name

 @param pkg Application package name



## open_url(url: String)
 Open webpage by default browser

 @param url url to open



## get_time()
 System.currentTimeMillis()

 @return current datetime from 1/1/1970
 
## SubwayApiTest(stationName)
some station name must drop suffix 역 in korea. i.e. 선릉역 -> 선릉
@return next train info of provided station.

## getStationCoordinates(stationName)
@return This function returns two values: latitude, longitude. if station does not exists, returns -1000,-1000

## task_yield()
 yield current loop. this must invoked frequently on infinity loop
 
## task_sleep(time)
 sleep current task for specified time in seconds. 
    """.trimIndent()
}