package net.projectlcs.lcs.functions.impl

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import net.projectlcs.lcs.LuaService
import net.projectlcs.lcs.functions.GMSHelper
import net.projectlcs.lcs.functions.PermissionProvider
import net.projectlcs.lcs.permission.PermissionRequestActivity
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@LuaProvider
object Location: PermissionProvider, GMSHelper {
    override fun verifyPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(LuaService.INSTANCE!!, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(LuaService.INSTANCE!!, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                ContextCompat.checkSelfPermission(LuaService.INSTANCE!!, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    override fun requestPermission() {
        startPermissionActivity(PermissionRequestActivity.REQUEST_LOCATION_PERMISSION)
    }

    /**
     * This function returns current location regarding to GPS.
     * This is optimized on battery so free to call it.
     * You must take care about error because GPS is not 100% accurate.
     * @return This function returns three values: latitude, longitude, error(in meters)
     */
    @SuppressLint("MissingPermission")
    @LuaFunction(name = "get_location")
    fun getLastLocation() = coroutine {
        requestPermission {
            try {
                val client = LocationServices.getFusedLocationProviderClient(LuaService.INSTANCE!!)
                val result = await(client.lastLocation)

                if (result != null) {
                    breakTask(result.latitude, result.longitude, result.accuracy * 2) // force increase error
                } else {
                    Log.w("Location", "Cannot retrieve current location")
                    breakTask(0, 0, 0)
                }
            } catch(e: Exception) {
                Log.e("Location", "Exception caught while processing getLastLocation function: $e")
                breakTask(0, 0, 0)
            }
        }
    }

    @LuaFunction("location_delta_to_meter")
            /**
             * This function accepts two location value and calculates the delta of two locations in meter.
             * @param latitude1 latitude of first location
             * @param longitude1 longitude of first location
             * @param latitude2 latitude of second location
             * @param longitude2 longitude of second location
             * @return delta of two location in meter value
             */
    fun locationDeltaToMeter(latitude1: Double, longitude1: Double, latitude2: Double, longitude2: Double): Double {
        var R = 6378.137; // Radius of earth in KM
        var dLat = latitude2 * Math.PI / 180 - latitude1 * Math.PI / 180;
        var dLon = longitude2 * Math.PI / 180 - longitude1 * Math.PI / 180;
        var a = sin(dLat / 2) * sin(dLat / 2) +
                cos(latitude1 * Math.PI / 180) * cos(latitude2 * Math.PI / 180) *
                sin(dLon / 2) * sin(dLon / 2);
        var c = 2 * atan2(sqrt(a), sqrt(1 - a));
        var d = R * c;
        return d * 1000; // meters
    }
}