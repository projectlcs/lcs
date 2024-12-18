package net.projectlcs.lcs.functions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.projectlcs.lcs.LuaService

interface AndroidCoroutineInterop {
    fun mainThread(f: () -> Unit) = GlobalScope.launch(Dispatchers.Main) { f() }
    fun ioThread(f: () -> Unit) = GlobalScope.launch(Dispatchers.IO) { f() }
    fun luaThread(f: () -> Unit) = GlobalScope.launch(LuaService.INSTANCE!!.luaDispatcher) { f() }
}