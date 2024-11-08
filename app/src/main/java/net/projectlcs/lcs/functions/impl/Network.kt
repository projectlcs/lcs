package net.projectlcs.lcs.functions.impl

import me.ddayo.aris.CoroutineProvider
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import net.projectlcs.lcs.functions.AndroidCoroutineInterop
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

@LuaProvider
object Network : AndroidCoroutineInterop, CoroutineProvider {
    private val client = OkHttpClient()

    @LuaFunction(name = "send_web_request")
            /**
             * Send HTTP Get request then retrieve response code and its data
             *
             * @param url target url
             * @return response_code: Int, response_data: String
             */
    fun sendGetRequest(url: String) = coroutine {
        var text: String? = null
        var responseCode: Int? = null
        ioThread {
            val request = Request.Builder()
                .url(url)
                .build()
            val response = client.newCall(request).execute()
            responseCode = response.code
            text = response.body?.string()
        }
        yieldUntil { text != null }
        breakTask(responseCode!!, text!!)
    }

    @LuaFunction(name = "download_file")
            /**
             * Download file via HTTP Get request at provided name.
             * If body is empty then it just creates empty file
             *
             * @param url target url
             * @param name file name to save
             * @return response_code: Int
             */
    fun downloadFile(url: String, name: String) = coroutine {
        var responseCode: Int? = null
        var fileWriteResult: Boolean? = null
        ioThread {
            val request = Request.Builder()
                .url(url)
                .build()
            val response = client.newCall(request).execute()
            responseCode = response.code
            File(name).writeBytes(response.body?.bytes() ?: ByteArray(0))
            fileWriteResult = response.body != null
        }
        yieldUntil { fileWriteResult != null }
        breakTask(responseCode!!)
    }
}