set_do_not_disturb(false)
send_notification("Hello, world", "text")

-- local a, b = send_web_request("https://naver.com")
-- send_notification("Web request", "Result: " .. a .. "\nBody: " .. b)


while true do
    task_sleep(1)
    -- task_yield()
    debug_log("Tick")
    local a, b, c = get_location()
    send_notification("Location", "Location: " .. a .. ", " .. b .. ", " .. c)
    debug_log("a")
    set_do_not_disturb(not get_do_not_disturb())
    debug_log("b")
end

