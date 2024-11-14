package net.projectlcs.lcs.functions.impl

import me.ddayo.aris.CoroutineProvider
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import android.util.Log
import me.ddayo.aris.LuaMultiReturn
import net.projectlcs.lcs.functions.AndroidCoroutineInterop

@LuaProvider
object Subway_Api_Call: CoroutineProvider, AndroidCoroutineInterop {
    data class SubwayArrival(
        val trainLineNm: String,  // 열차 노선 이름
        val arvlMsg2: String,     // 도착 메시지
        val arvlMsg3: String,      // 추가 메시지
        val barvlDt: String        // 도착 예정 시간
    )

    data class SubwayArrivalResponse(
        val realtimeArrivalList: List<SubwayArrival>
    )

    interface SubwayService {
        @GET("{apiKey}/json/realtimeStationArrival/0/5/{stationName}")
        fun getRealtimeArrival(
            @Path("apiKey") apiKey: String,
            @Path("stationName") stationName: String
        ): Call<SubwayArrivalResponse>
    }
    @LuaFunction(name = "SubwayApiTest")
            /**
             * some station name must drop suffix 역 in korea. i.e. 선릉역 -> 선릉
             *
             * @return next train info of provided station.
             */
    fun SubwayApiTest(stationName: String) = coroutine {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://swopenapi.seoul.go.kr/api/subway/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiKey = "6950666467736a683730556375524a"
        val stationName = stationName
        val service = retrofit.create(SubwayService::class.java)
        val call = service.getRealtimeArrival(apiKey,stationName)
        var flag = false
        var ret = ""
        ioThread {
            try {
                Log.d("apiCall",call.request().url.toString())
                val response = call.execute()
                if (response.isSuccessful) {
                    val arrivalList = response.body()?.realtimeArrivalList
                    arrivalList?.forEach { arrival ->
                        //println("노선: ${arrival.trainLineNm}, 도착 메시지: ${arrival.arvlMsg2}, 추가 메시지: ${arrival.arvlMsg3}, 도착 시간 : ${arrival.barvlDt}")
                        Log.d("apiCall", arrival.barvlDt.toString())
                        if(ret.isNotEmpty()) ret = ret + "/"
                        ret += arrival.trainLineNm
                        if(ret.isNotEmpty()) ret+="/"
                        ret += arrival.barvlDt
                        Log.d("apiCall",ret)
                    }
                } else {
                    Log.e("apiCall", "Not found")
                }
            } catch (e: Exception) {
                Log.e("apiCall","error")
                e.printStackTrace()
            }
            finally { flag = true }
        }

        yieldUntil { flag }
        // 유니코드 문자열을 한글로 변환하는 함수
        val k_ret = Regex("""\\u([0-9A-Fa-f]{4})""").replace(ret) { matchResult ->
            val unicodeValue = matchResult.groupValues[1].toInt(16)  // 16진수로 변환
            unicodeValue.toChar().toString()  // 문자로 변환하여 반환
        }
        Log.d("apiCall","newCode")
        Log.d("apiCall", k_ret)
        println(k_ret)
        breakTask(ret)
    }
}