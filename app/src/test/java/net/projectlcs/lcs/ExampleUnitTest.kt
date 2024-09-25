package net.projectlcs.lcs

import me.ddayo.aris.luagen.LuaFunction
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    companion object {
        @LuaFunction
        fun test1() {
            println("test1")
        }

        @LuaFunction
        fun test2(): Double {
            return 12.0
        }

        @LuaFunction(name = "test3")
        fun test1(x: Double): Int {
            return 1
        }

        @LuaFunction(name = "test4")
        fun test1(x: Double, y: String): String {
            return "$x, $y"
        }
    }


}