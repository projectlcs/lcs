@file:OptIn(KspExperimental::class)

package net.projectlcs.lcs.ap

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.Dependencies
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
import net.projectlcs.lcs.ap.LuaFunction


class LuaBindingException(message: String): Exception(message)

class LuaFunctionProcessorProvider : SymbolProcessorProvider {
    companion object {
        private val luaFunctionAnnotationName = LuaFunction::class.java.canonicalName
        private val luaProviderAnnotationName = LuaProvider::class.java.canonicalName
    }

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return object : SymbolProcessor {
            val files = mutableSetOf<KSFile>()
            val functions = mutableMapOf<String, MutableList<String>>()
            val logger = environment.logger

            override fun process(resolver: Resolver): List<KSAnnotated> {
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
                val permissionProviderResolved by lazy {
                    resolver.getClassDeclarationByName("net.projectlcs.lcs.functions.PermissionProvider")!!.asStarProjectedType()
                }

                resolver.getSymbolsWithAnnotation(luaProviderAnnotationName).let { providers ->
                    val ret = providers.filter { !it.validate() }

                    providers.filter { it is KSClassDeclaration && it.validate() }
                        .forEach { classDecl ->
                            classDecl.accept(object: KSVisitorVoid() {
                                override fun visitClassDeclaration(
                                    classDeclaration: KSClassDeclaration,
                                    data: Unit
                                ) {
                                    if(classDeclaration.classKind != ClassKind.OBJECT)
                                        throw LuaBindingException("Cannot process ${classDeclaration.qualifiedName}: Provider must be singletone object")

                                    val isPermissionProvider = permissionProviderResolved.isAssignableFrom(classDeclaration.asStarProjectedType())

                                    classDeclaration.getDeclaredFunctions().mapNotNull { it.getAnnotationsByType(LuaFunction::class).firstOrNull()?.let { annot -> it to annot } }
                                        .forEach { (fn, annot) ->
                                            val sb = StringBuilder()

                                            val fnName = if(annot.name == "!") fn.simpleName.asString() else annot.name

                                            val ptResolved = fn.parameters.map { it.type.resolve() }
                                            val minimumRequiredParameters =
                                                ptResolved.indexOfLast { !it.isMarkedNullable } + 1
                                            logger.info("Min arg: $minimumRequiredParameters")

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
                                                    sb.append(".toJavaObject() as Boolean")
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
                                            |       selFn = {
                                            """.trimMargin())
                                            if(unitResolved.isAssignableFrom(fn.returnType!!.resolve())) {
                                                if(isPermissionProvider)
                                                    sb.appendLine("if(${classDeclaration.qualifiedName!!.asString()}.verifyPermission(true))")
                                                sb.appendLine("${fn.qualifiedName!!.asString()}($invStr)")
                                                    .appendLine("emptyList<Nothing>()")
                                                    .appendLine("}")
                                            }
                                            else {
                                                if(isPermissionProvider)
                                                    sb.appendLine("if(${classDeclaration.qualifiedName!!.asString()}.verifyPermission(true))")
                                                sb.appendLine("listOf(${fn.qualifiedName!!.asString()}($invStr))")
                                                if(isPermissionProvider)
                                                    sb.appendLine("else emptyList<Nothing>()")
                                                sb.appendLine("}")
                                            }
                                            sb.appendLine("    }")
                                                .appendLine("}")

                                            val fn = functions.getOrPut(fnName) { mutableListOf() }
                                            fn.add(sb.toString())
                                            files.add(classDecl.containingFile!!)
                                        }
                                }
                            }, Unit)
                        }
                    return ret.toList()
                }
            }

            override fun finish() {
                super.finish()
                val file = environment.codeGenerator.createNewFile(
                    dependencies = Dependencies(true, *(files).toTypedArray()),
                    packageName = "net.projectlcs.lcs.lua",
                    fileName = "LuaGenerated"
                ).writer()

                file.write("""
package net.projectlcs.lcs.lua

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
        
${functions.entries.joinToString("\n\n") {
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
                    } }
    }
}
""".trimIndent())

                file.close()
            }
        }
    }
}
