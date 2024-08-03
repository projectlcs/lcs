while true do
    task:sleep(1000)
    debug_log(task, "Tick")
    set_do_not_disturb(task, not get_do_not_disturb(task))
end

