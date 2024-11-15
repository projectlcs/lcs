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
object Subway_Api_Call : CoroutineProvider, AndroidCoroutineInterop {
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
        val service = retrofit.create(SubwayService::class.java)
        val call = service.getRealtimeArrival(apiKey, stationName)
        var flag = false
        var ret = StringBuilder()
        ioThread {
            try {
                Log.d("apiCall", call.request().url.toString())
                val response = call.execute()
                if (response.isSuccessful) {
                    val arrivalList = response.body()?.realtimeArrivalList
                    arrivalList?.forEach { arrival ->
                        ret.append(arrival.trainLineNm)
                            .append("/")
                            .appendLine(
                                if ((arrival.barvlDt.toIntOrNull() ?: 0) == 0) arrival.arvlMsg2
                                else (arrival.barvlDt.toInt()).let {
                                    if (it >= 60) "${it / 60}분 ${it % 60}초 뒤 도착"
                                    else "${it}초 뒤 도착"
                                })
                    }
                } else {
                    Log.e("apiCall", "Not found")
                }
            } catch (e: Exception) {
                Log.e("apiCall", "error")
                e.printStackTrace()
            } finally {
                flag = true
            }
        }

        yieldUntil { flag }
        breakTask(ret.toString())
    }
}