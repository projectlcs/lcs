package net.projectlcs.lcs.functions.impl

import android.os.Environment
import me.ddayo.aris.CoroutineProvider.CoroutineReturn
import me.ddayo.aris.LuaMultiReturn
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import net.projectlcs.lcs.LuaService
import net.projectlcs.lcs.functions.PermissionProvider
import net.projectlcs.lcs.functions.impl.Dialog.showYesNoDialog
import net.projectlcs.lcs.permission.PermissionRequestActivity
import java.io.File
import kotlin.experimental.ExperimentalTypeInference

@LuaProvider
object FileManagement : PermissionProvider {
    @LuaFunction(name = "is_file")
            /**
             * @param name file to verify. this both can be global or local
             * @return is file then true. if not(like directory) false
             */
    fun isFile(name: String) = File(name).isFile

    @LuaFunction(name = "files_in_dir")
            /**
             * Get files inside directory.
             *
             * ```lua
             * local files = { files_in_dir("folder") }
             * for x=1,#files do
             *     print(files[x])
             * end
             * ```
             * This code prints file inside folder.
             *
             * @param name the directory to iterate
             * @return files inside specified directory. you may use { files_in_dir("something") } to convert return value into list.
             */
    fun iterateDirectory(name: String) = LuaMultiReturn(
        *File(LuaService.INSTANCE!!.filesDir, name).listFiles()!!.map { it.absolutePath }
            .toTypedArray()
    )

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
    fun deleteFile(name: String) = coroutine {
        if (showYesNoDialog(
                "Are you really want to delete file $name?",
                "This action can not be reverted."
            ) == 1
        ) {
            File(LuaService.INSTANCE!!.filesDir, name).delete()
            breakTask(true)
        } else breakTask(false)
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

    @LuaFunction(name = "files_in_dir_global")
            /**
             * Get files inside global(external) directory.
             *
             * ```lua
             * local files = { files_in_dir_global("folder") }
             * for x=1,#files do
             *     print(files[x])
             * end
             * ```
             * This code prints file inside folder.
             *
             * @param name the directory to iterate
             * @return files inside specified directory. you may use { files_in_dir_global("something") } to convert return value into list.
             */
    fun getFileInDirectoryGlobal(name: String) = coroutine {
        requestPermission {
            breakTask(*File(name).listFiles()!!.map { it.absolutePath }.toTypedArray())
        }
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
    fun deleteFileGlobal(name: String) = coroutine {
        requestPermission {
            if (showYesNoDialog(
                    "Are you really want to delete global file $name?",
                    "This action can not be reverted."
                ) == 1
            ) {
                File(name).delete()
                breakTask(true)
            } else breakTask(false)
        }
    }

    @OptIn(ExperimentalTypeInference::class)
    suspend fun <T> SequenceScope<CoroutineReturn<T>>.suspendTest(@BuilderInference then: suspend SequenceScope<CoroutineReturn<T>>.(result: Int) -> Unit) {
        yieldUntil { true }
        then(1)
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

    @LuaFunction(name = "get_download_folder")
            /**
             * @return get download folder directory. The result must used on global file management function
             */
    fun getDownloadFolder() =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)!!.absolutePath

    override fun verifyPermission(): Boolean {
        return Environment.isExternalStorageManager()
    }

    override fun requestPermission() {
        startPermissionActivity(PermissionRequestActivity.REQUEST_FILE_MANAGE_PERMISSION)
    }
}