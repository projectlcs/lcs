package net.projectlcs.lcs.functions

import com.google.android.gms.tasks.Task
import me.ddayo.aris.CoroutineProvider
import me.ddayo.aris.CoroutineProvider.CoroutineReturn

interface GMSHelper: CoroutineProvider {
    suspend fun <T> SequenceScope<CoroutineReturn<T>>.awaitUnit(task: Task<Void>) {
        yieldUntil { task.isComplete == true }
        if(task.isCanceled) throw Exception("Task operation canceled")
    }

    suspend fun <T, R> SequenceScope<CoroutineReturn<T>>.await(task: Task<R>): R {
        yieldUntil { task.isComplete == true }
        if(task.isCanceled) throw Exception("Task operation canceled")
        return task.result
    }
}