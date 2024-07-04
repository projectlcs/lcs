@file:OptIn(KspExperimental::class)

package com.teammaso.lcs.ap

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.validate
import kotlin.reflect.KFunction


class LuaFunctionProcessorProvider : SymbolProcessorProvider {
    companion object {
        private val luaFunctionAnnotationName = LuaFunction::class.java.canonicalName
    }

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return object : SymbolProcessor {
            val files = mutableSetOf<KSFile>()
            val functions = mutableMapOf<String, MutableList<String>>()
            val logger = environment.logger

            override fun process(resolver: Resolver): List<KSAnnotated> {
                val sym = resolver.getSymbolsWithAnnotation(luaFunctionAnnotationName)
                val ret = sym.filter { !it.validate() }

                val numberResolved by lazy {
                    resolver.getClassDeclarationByName<Number>()!!.asStarProjectedType()
                }
                val longResolved by lazy {
                    resolver.getClassDeclarationByName<Long>()!!.asStarProjectedType()
                }
                val intResolved by lazy {
                    resolver.getClassDeclarationByName<Int>()!!.asStarProjectedType()
                }
                val shortResolved by lazy {
                    resolver.getClassDeclarationByName<Short>()!!.asStarProjectedType()
                }
                val byteResolved by lazy {
                    resolver.getClassDeclarationByName<Byte>()!!.asStarProjectedType()
                }
                val charResolved by lazy {
                    resolver.getClassDeclarationByName<Char>()!!.asStarProjectedType()
                }
                val doubleResolved by lazy {
                    resolver.getClassDeclarationByName<Double>()!!.asStarProjectedType()
                }
                val floatResolved by lazy {
                    resolver.getClassDeclarationByName<Float>()!!.asStarProjectedType()
                }

                val stringResolved by lazy {
                    resolver.getClassDeclarationByName<String>()!!.asStarProjectedType()
                }
                val booleanResolved by lazy {
                    resolver.getClassDeclarationByName<Boolean>()!!.asStarProjectedType()
                }
                val mapResolved by lazy {
                    resolver.getClassDeclarationByName<Map<Any, Any>>()!!.asStarProjectedType()
                }
                val classResolved by lazy {
                    resolver.getClassDeclarationByName<Class<*>>()!!.asStarProjectedType()
                }
                val unitResolved by lazy {
                    resolver.getClassDeclarationByName<Unit>()!!.asStarProjectedType()
                }

                sym.filter { it is KSFunctionDeclaration && it.validate() }
                    .forEach {
                        it.accept(object : KSVisitorVoid() {
                            override fun visitFunctionDeclaration(
                                function: KSFunctionDeclaration,
                                data: Unit
                            ) {
                                super.visitFunctionDeclaration(function, data)

                                val sb = StringBuilder()

                                val annot =
                                    function.getAnnotationsByType(LuaFunction::class).first()
                                val fnName =
                                    if (annot.name == "!") function.simpleName.asString() else annot.name

                                if ((function.parentDeclaration as? KSClassDeclaration)?.classKind != ClassKind.OBJECT)
                                    throw Exception("Lua function must inside single tone object")
                                logger.warn("Visit ${function.qualifiedName?.asString()} -> lua::${fnName}")

                                val ptResolved = function.parameters.map { it.type.resolve() }
                                val minimumRequiredParameters =
                                    ptResolved.indexOfLast { !it.isMarkedNullable } + 1
                                logger.warn("Min arg: $minimumRequiredParameters")

                                val invStr = ptResolved.mapIndexed { ix, it ->
                                    val sb = StringBuilder()
                                        .append("arg[")
                                        .append(ix)
                                        .append(']')
                                    if (stringResolved.isAssignableFrom(it))
                                        sb.append(".toString()")
                                    else if (numberResolved.isAssignableFrom(it))
                                        sb.append(
                                            when {
                                                longResolved.isAssignableFrom(it) -> ".toInteger()"
                                                intResolved.isAssignableFrom(it) -> ".toInteger().toInt()"
                                                shortResolved.isAssignableFrom(it) -> ".toInteger().toShort()"
                                                byteResolved.isAssignableFrom(it) -> ".toInteger().toByte()"
                                                doubleResolved.isAssignableFrom(it) -> ".toNumber()"
                                                floatResolved.isAssignableFrom(it) -> ".toNumber().toFloat()"
                                                else -> throw Exception("Not supported type")
                                            }
                                        )
                                    else if (booleanResolved.isAssignableFrom(it))
                                        sb.append(".toBoolean()")
                                    else sb.append(".toJavaObject()")
                                    sb.toString()
                                }
                                    .joinToString(", ")

                                sb.appendLine("""if(arg.size >= ${minimumRequiredParameters}) {
                                |   var score = 0
                                |   // TODO: score calculation
                                |   
                                |   if(score > sel) {
                                |       sel = score
                                """.trimMargin())
                                    .appendLine(
                                    if (unitResolved.isAssignableFrom(function.returnType!!.resolve()))
                                        """
                                |       selFn = {
                                |          ${function.qualifiedName!!.asString()}($invStr)
                                |          emptyList<Nothing>()
                                |       }""".trimMargin()
                                    else """
                                |       selFn = {
                                |           listOf(${function.qualifiedName!!.asString()}($invStr))
                                |       }""".trimMargin()
                                )
                                    .appendLine("    }")
                                    .appendLine("}")

                                val fn = functions.getOrPut(fnName) { mutableListOf() }
                                fn.add(sb.toString())
                                files.add(function.containingFile!!)
                            }
                        }, Unit)
                    }

                return ret.toList()
            }

            override fun finish() {
                super.finish()
                val file = environment.codeGenerator.createNewFile(
                    dependencies = Dependencies(true, *(files).toTypedArray()),
                    packageName = "com.teammaso.lcs.lua",
                    fileName = "LuaGenerated"
                ).writer()

                file.write("""
package com.teammaso.lcs.lua

import party.iroiro.luajava.Lua
import party.iroiro.luajava.LuaException
import party.iroiro.luajava.luajit.LuaJit

object LuaGenerated {
    fun <T> push(lua: Lua, it: T) {
        when(it) {
            is Number -> lua.push(it)
            is Boolean -> lua.push(it)
            is String -> lua.push(it)
            is Map<*, *> -> lua.push(it)
            is Class<*> -> lua.pushJavaClass(it)
            else -> lua.pushJavaObject(it as Any)
        }
    }
    
    fun initLua(lua: LuaJit) {
        val th = { 
            throw LuaException(LuaException.LuaError.RUNTIME, "No function can handle the call")
        }
        
${
                    functions.entries.joinToString("\n\n") {
                        """        lua.push { lua ->
            val arg = (0 until lua.top).map { lua.get() }.reversed()
            val tys = arg.map { it.type() }
            
            var sel = -1
            var selFn: () -> List<*> = th
            
            ${it.value.joinToString("\n") { it }}
            
            val opt = selFn()
            opt.forEach {
                push(lua, it)
            }
            opt.size
        }
        lua.setGlobal("${it.key}")"""
                    }
                }
    }
}
""".trimIndent())

                file.close()
            }
        }
    }
}
