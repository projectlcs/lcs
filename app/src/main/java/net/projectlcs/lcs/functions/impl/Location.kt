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
}