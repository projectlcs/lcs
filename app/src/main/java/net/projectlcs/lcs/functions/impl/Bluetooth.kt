package net.projectlcs.lcs.functions.impl
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import net.projectlcs.lcs.Manifest

@LuaProvider
object Bluetooth {
    @LuaFunction(name = "find_machin")
    fun find_machin(context: Context) {
        Log.d("func","called")
        val bluetoothManager: BluetoothManager=context.getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter?=bluetoothManager.adapter
        ActivityCompat.checkSelfPermission(this,Manifest.permission.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION)
        if(bluetoothAdapter!=null && ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED){
            Log.d("Adapter","not none")
            val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
            pairedDevices?.forEach { device ->
                val deviceName = device.name
                val deviceHardwareAddress = device.address // MAC 주소
                Log.d("Paired Bluetooth Device", "Name: $deviceName, MAC Address: $deviceHardwareAddress")
            }
        }
        else{
            Log.d("Adapter","is nil")
        }
        //val bluetoothAdapter = BluetoothAdapter.getR
        val device: BluetoothDevice?=bluetoothAdapter?.getRemoteDevice("12:34:56:78:9A:BC")
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        if(device?.bondState==BluetoothDevice.BOND_BONDED){
            Log.w("LUA_B","AC")
        }
        else{
            Log.w("LUA_B","fail")
        }
    }
}