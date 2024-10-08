package net.projectlcs.lcs.functions.impl

import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import net.projectlcs.lcs.LuaService
import net.projectlcs.lcs.functions.PermissionProvider
import net.projectlcs.lcs.permission.PermissionRequestActivity
import java.io.File

@LuaProvider
object FileManagement: PermissionProvider {
    @LuaFunction(name = "create_file")
            /**
             * Create file inside application-data directory
             * @param name filename to create
             */
    fun createFile(name: String) {
        File(LuaService.INSTANCE!!.filesDir, name).createNewFile()
    }

    @LuaFunction(name = "delete_file")
            /**
             * Delete file inside application-data directory
             * @param name filename to delete
             */
    fun deleteFile(name: String) {
        File(LuaService.INSTANCE!!.filesDir, name).delete()
    }

    @LuaFunction(name = "write_file")
            /**
             * Write(overwrite) to file inside application-data directory
             * @param name filename to write
             * @param text text to write
             */
    fun writeFile(name: String, text: String) {
        File(LuaService.INSTANCE!!.filesDir, name).writeText(text)
    }

    @LuaFunction(name = "append_file")
            /**
             * Write(append) to file inside application-data directory
             * @param name filename to append
             * @param text text to write
             */
    fun appendFile(name: String, text: String) {
        File(LuaService.INSTANCE!!.filesDir, name).appendText(text)
    }

    @LuaFunction(name = "read_file")
            /**
             * Read the file inside application-data directory
             * @param name name of file to read
             * @return content of given file
             */
    fun readFile(name: String): String {
        return File(LuaService.INSTANCE!!.filesDir, name).readText()
    }

    @LuaFunction(name = "create_file_global")
            /**
             * Create file inside global(external) storage. This requires MANAGE_EXTERNAL_STORAGE permission
             * @param name filename to create
             */
    fun createFileGlobal(name: String) = coroutine<Unit> {
        requestPermission {
            File(name).createNewFile()
        }
    }

    @LuaFunction(name = "delete_file_global")
            /**
             * Delete file inside global(external) storage. This requires MANAGE_EXTERNAL_STORAGE permission
             * @param name filename to create
             */
    fun deleteFileGlobal(name: String) = coroutine<Unit> {
        requestPermission {
            File(name).delete()
        }
    }

    @LuaFunction(name = "write_file_global")
            /**
             * Write(overwrite) file inside global(external) storage. This requires MANAGE_EXTERNAL_STORAGE permission
             * @param name filename to create
             * @param text text to write
             */
    fun writeFileGlobal(name: String, text: String) = coroutine<Unit> {
        requestPermission {
            File(name).writeText(text)
        }
    }

    @LuaFunction(name = "append_file_global")
            /**
             * Write(append) file inside global(external) storage. This requires MANAGE_EXTERNAL_STORAGE permission
             * @param name filename to create
             * @param text text to write
             */
    fun appendFileGlobal(name: String, text: String) = coroutine<Unit> {
        requestPermission {
            File(name).appendText(text)
        }
    }

    @LuaFunction(name = "read_file_global")
            /**
             * Read file inside global(external) storage. This requires MANAGE_EXTERNAL_STORAGE permission
             * @param name filename to create
             * @return content of given file
             */
    fun readFileGlobal(name: String) = coroutine {
        requestPermission {
            breakTask(File(name).readText())
        }
    }

    override fun verifyPermission(): Boolean {
        return ContextCompat.checkSelfPermission(LuaService.INSTANCE!!, android.Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    override fun requestPermission() {
        startPermissionActivity(PermissionRequestActivity.REQUEST_FILE_MANAGE_PERMISSION)
    }
}