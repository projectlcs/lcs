package net.projectlcs.lcs.ai

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.projectlcs.lcs.AndroidLuaEngine
import net.projectlcs.lcs.LuaService
import net.projectlcs.lcs.data.ScriptDataManager
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

object PromptEngineering {
    val functionList = """
## task_yield()
 yield current loop. this must invoked frequently on infinity loop
 
## task_sleep(time)
 sleep current task for specified time in milliseconds. 

## create_alarm(hour: Int, minutes: Int)
```
 createAlarm(15, 12) // set new alarm at 3:12 PM

 @param hour time of hour to set alarm
 @param minutes time of minutes to set alarm
```


## set_do_not_disturb(newValue: Boolean)
```
 @param newValue new do not disturb state
```


## get_do_not_disturb()
```
 @return current do not disturb status
```


## get_current_time()
```
 Returns current time of day
 @return hour_of_day(0..23), minute(0..59), seconds(0..59)
```


## get_current_date()
```
 Returns current date
 @return day_of_month(1..31), month(0..11), year(int)
```


## get_current_day_of_week()
```
 Returns day of week
 @return day of week(1..7). 1 is sunday and 7 is saturday
```


## show_yes_no_dialog(title: String, message: String)


## is_file(name: String)
```
 @param name file to verify. this both can be global or local
 @return is file then true. if not(like directory) false
```


## files_in_dir(name: String)
```
 Get absolute path of files inside directory.

 ```lua
 local files = { files_in_dir("folder") }
 for x=1,#files do
     print(files[x])
 end
 ```
 This code prints file inside folder.

 @param name the directory to iterate
 @return absolute path of files inside specified directory. you may use { files_in_dir("something") } to convert return value into list.
```


## create_file(name: String)
```
 Create file inside application-data directory
 @param name filename to create
```


## delete_file(name: String)
```
 Delete file inside application-data directory
 @param name filename to delete
```


## write_file(name: String, text: String)
```
 Write(overwrite) to file inside application-data directory
 @param name filename to write
 @param text text to write
```


## append_file(name: String, text: String)
```
 Write(append) to file inside application-data directory
 @param name filename to append
 @param text text to write
```


## read_file(name: String)
```
 Read the file inside application-data directory
 @param name name of file to read
 @return content of given file
```


## files_in_dir_global(name: String)
```
 Get absolute path of files inside global(external) directory.

 ```lua
 local files = { files_in_dir_global("folder") }
 for x=1,#files do
     print(files[x])
 end
 ```
 This code prints file inside folder.

 @param name the directory to iterate
 @return absolute path of files inside specified directory. you may use { files_in_dir_global("something") } to convert return value into list.
```


## create_file_global(name: String)
```
 Create file inside global(external) storage. This requires MANAGE_EXTERNAL_STORAGE permission
 @param name filename to create
```


## delete_file_global(name: String)
```
 Delete file inside global(external) storage. This requires MANAGE_EXTERNAL_STORAGE permission
 @param name filename to create
```


## write_file_global(name: String, text: String)
```
 Write(overwrite) file inside global(external) storage. This requires MANAGE_EXTERNAL_STORAGE permission
 @param name filename to create
 @param text text to write
```


## append_file_global(name: String, text: String)
```
 Write(append) file inside global(external) storage. This requires MANAGE_EXTERNAL_STORAGE permission
 @param name filename to create
 @param text text to write
```


## read_file_global(name: String)
```
 Read file inside global(external) storage. This requires MANAGE_EXTERNAL_STORAGE permission
 @param name filename to create
 @return content of given file
```


## get_download_folder()
```
 @return get download folder directory. The result must used on global file management function
```


## get_location()
```
 This function returns current location regarding to GPS.
 This is optimized on battery so free to call it.
 You must take care about error because GPS is not 100% accurate.
 @return This function returns three values: latitude, longitude, error(in meters)
```


## location_delta_to_meter(latitude1: Double, longitude1: Double, latitude2: Double, longitude2: Double)
```
 This function accepts two location value and calculates the delta of two locations in meter.
 @param latitude1 latitude of first location
 @param longitude1 longitude of first location
 @param latitude2 latitude of second location
 @param longitude2 longitude of second location
 @return delta of two location in meter value
```


## debug_log(message: String)
## debug_log(tag: String, message: String)


## send_web_request(url: String)
```
 Send HTTP Get request then retrieve response code and its data

 @param url target url
 @return response_code: Int, response_data: String
```


## download_file(url: String, name: String)
```
 Download file via HTTP Get request at provided name.
 If body is empty then it just creates empty file

 @param url target url
 @param name file name to save
 @return response_code: Int
```


## send_notification(task: AndroidLuaTask, message: String)
```
 Send notification with existing task

 @param task the LuaTask. just use `task` which already passed to function.
 @param message message to send
```
## send_notification(task: AndroidLuaTask, title: String, text: String)
```
 Send notification with provided title and text

 @param task the LuaTask. just use `task` which already passed to function.
 @param title title of notification
 @param text inner text of notification
```


## send_long_toast(text: String)
```
 Send long toast message

 @param text text to displayed by long toast message
```


## send_toast(text: String)
```
 Send toast message

 @param text text to displayed by toast message
```


## open_app(pkg: String)
```
 Open application by package name

 @param pkg Application package name
```


## open_url(url: String)
```
 Open webpage by default browser

 @param url url to open
```


## SubwayApiTest(stationName: String)
```
 some station name must drop suffix 역 in korea. i.e. 선릉역 -> 선릉

 @return next train info of provided station.
```


## getStationCoordinates(stationName: String)
```
 @return This function returns two values: latitude, longitude. if station does not exists, returns -1000,-1000
```


## get_time()
```
 System.currentTimeMillis()

 @return current datetime from 1/1/1970
```
    """.trimIndent()


    /**
     * Get code from GPT
     * @param request the user request
     */
    suspend fun retrieveCode(request: String) = try {
        var response = testOpenAIApi(
            """
You are a master of Lua scripting.
Your task is to write Lua scripts for the given requests.
Your script will be executed immediately on the device.
Do not include anything other than the code; avoid any additional output or formatting.
 

You can use the following Lua functions: 
$functionList

Request: 
$request
  """.trimIndent()
        )

        response = response.removePrefix("```lua")

        // Trim the "```" from both ends
        response = response.removePrefix("```").removeSuffix("```")
        response = response.removePrefix("\n").removeSuffix("\n")
        Log.d("OpenAIApiTest", "API 연동 성공: $response")
        val summary = testOpenAIApi(
            """
Regarding the code, please summarize the user's request in approximately 15 letters.

Request: $request

Generated code: $response
                                    """.trimIndent()
        )
        CoroutineScope(Dispatchers.IO).launch {
            val ref = ScriptDataManager.createNewScript(summary)
            ref.code = response
            ScriptDataManager.updateAllScript(ref)
            LuaService.INSTANCE?.apply {
                (engine.tasks.firstOrNull { (it as? AndroidLuaEngine.AndroidLuaTask)?.ref?.id == ref.id } as? AndroidLuaEngine.AndroidLuaTask)?.isRunning =
                    true
            }
        }
        "API 연동 성공: $response"
    } catch (e: Exception) {
        "API 연동 실패: ${e.message}"
    }

    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openai.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(OpenAIService::class.java)

    suspend fun testOpenAIApi(prompt: String): String {
        val request = ChatCompletionRequest(
            model = GPTConfig.model,
            messages = listOf(ChatMessage(role = "user", content = prompt))
        )

        val response = service.createChatCompletion("Bearer ${GPTConfig.apiKey}", request)
        return response.choices.firstOrNull()?.message?.content ?: "No response"
    }


    interface OpenAIService {
        @POST("chat/completions")
        suspend fun createChatCompletion(
            @Header("Authorization") authorization: String,
            @Body request: ChatCompletionRequest
        ): ChatCompletionResponse
    }

    data class ChatCompletionRequest(
        val model: String,
        val messages: List<ChatMessage>
    )

    data class ChatCompletionResponse(
        val choices: List<ChatChoice>
    )

    data class ChatChoice(
        val message: ChatMessage
    )

    data class ChatMessage(
        val role: String,
        val content: String
    )
}