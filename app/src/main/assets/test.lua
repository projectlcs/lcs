set_do_not_disturb(false)
send_notification("Hello, world", "text")
local a, b = send_web_request("https://naver.com")
send_notification("Web request", "Result: " .. a .. "\nBody: " .. b)

find_machin()
