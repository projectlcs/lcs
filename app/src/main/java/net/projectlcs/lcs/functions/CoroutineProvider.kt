package net.projectlcs.lcs.functions

import kotlin.experimental.ExperimentalTypeInference

interface CoroutineProvider {
    @OptIn(ExperimentalTypeInference::class)
    fun <T> coroutine(@BuilderInference block: suspend SequenceScope<CoroutineReturn<T>>.() -> Unit)
        = LuaCoroutineIntegration(sequence(block))

    suspend fun <T> SequenceScope<CoroutineReturn<T>>.breakTask() {
        yield(CoroutineReturn.breakTask())
    }

    suspend fun <T> SequenceScope<CoroutineReturn<T>>.breakTask(value: T) {
        yield(CoroutineReturn.breakTask(value))
    }

    suspend fun <T> SequenceScope<CoroutineReturn<T>>.yieldUntil(until: () -> Boolean) {
        yield(CoroutineReturn.yield(until))
    }

    class LuaCoroutineIntegration<T>(sequence: Sequence<CoroutineReturn<T>>) {
        private val iter = sequence.iterator()

        // following functions are called from lua(via reflection)
        @JvmName(name = "next_iter")
        fun nextIter(): CoroutineReturn<T> {
            if(!iter.hasNext()) return CoroutineReturn.breakTask()
            return iter.next()
        }
    }

    class CoroutineReturn<T>(val isBreak: Boolean) {
        var returnValue: T? = null
        var until: (() -> Boolean)? = null

        // following functions are called from lua(via reflection)
        @JvmName(name = "finished")
        fun finished() = until?.invoke() == true
        @JvmName(name = "value")
        fun value() = returnValue
        @JvmName(name = "is_break")
        fun isBreak() = isBreak

        companion object {
            fun <T> breakTask(value: T) = CoroutineReturn<T>(true).apply { returnValue = value }
            fun <T> breakTask() = CoroutineReturn<T>(true)
            fun <T> yield(until: () -> Boolean) = CoroutineReturn<T>(false).apply { this.until = until }
        }
    }
}