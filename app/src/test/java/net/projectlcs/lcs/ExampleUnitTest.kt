package net.projectlcs.lcs

import net.projectlcs.lcs.ap.LuaFunction
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

    private val lua = LuaHandler.createInstance()

    @Test
    fun test1() {
        lua.load("""
            test1()
        """.trimIndent())
        lua.pCall(0, 0)
    }

    @Test
    fun test2() {
        lua.getGlobal("test2")
        lua.pCall(0, 1)
        assertEquals(12.0, lua.toNumber(1), 0.001)
        lua.pop(1)
    }

    @Test
    fun test3() {
        lua.load("""
            return test3(3)
        """.trimIndent())
        lua.pCall(0, 1)
        assertEquals(1, lua.toInteger(1))
        lua.pop(1)
    }

    @Test
    fun test4() {
        lua.getGlobal("test4")
        lua.push(1)
        lua.push("test4 comp")
        lua.pCall(2, 1)
        assertEquals("1.0, test4 comp", lua.get().toString())
    }
}