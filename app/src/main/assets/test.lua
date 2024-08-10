set_do_not_disturb(false)
send_notification("Hello, world", "text")

local a, b = send_web_request("https://naver.com")
send_notification("Web request", "Result: " .. a .. "\nBody: " .. b)

while true do
    get_current_task():sleep(1000)
    debug_log("Tick")
    set_do_not_disturb(not get_do_not_disturb())
end

