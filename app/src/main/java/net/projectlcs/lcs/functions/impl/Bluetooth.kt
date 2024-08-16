package net.projectlcs.lcs.functions.impl
import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import net.projectlcs.lcs.LuaService
import net.projectlcs.lcs.functions.PermissionProvider
import net.projectlcs.lcs.permission.PermissionRequestActivity

@LuaProvider
object Bluetooth: PermissionProvider {
    @SuppressLint("MissingPermission")
    @LuaFunction(name = "find_machin")
    fun findMachin() = coroutine<Unit> { // using coroutine here for request dangerous permission
        requestPermission { // this block will and only executed on user approved permission
            Log.d("func", "called")
            val context = LuaService.INSTANCE!! // using LuaService context. If LuaService is on execution, this variable always exists
            val bluetoothManager: BluetoothManager =
                context.getSystemService(BluetoothManager::class.java)
            val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

            // Nah? I have no idea what is this
            // ActivityCompat.checkSelfPermission(context, Manifest.permission.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION)
            if (bluetoothAdapter != null) {
                Log.d("Adapter", "not none")
                val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
                pairedDevices?.forEach { device ->
                    val deviceName = device.name
                    val deviceHardwareAddress = device.address // MAC 주소
                    Log.d(
                        "Paired Bluetooth Device",
                        "Name: $deviceName, MAC Address: $deviceHardwareAddress"
                    )
                }
            } else {
                Log.d("Adapter", "is nil")
            }
            val device: BluetoothDevice? = bluetoothAdapter?.getRemoteDevice("12:34:56:78:9A:BC")
            if (device?.bondState == BluetoothDevice.BOND_BONDED) {
                Log.w("LUA_B", "AC")
            } else {
                Log.w("LUA_B", "fail")
            }
        }
    }

    override fun verifyPermission(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(LuaService.INSTANCE!!, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
    }

    override fun requestPermission() {
        // this open PermissionRequestActivity if permission needed
        LuaService.INSTANCE!!.startActivity(Intent(LuaService.INSTANCE!!, PermissionRequestActivity::class.java)
            .putExtra(PermissionRequestActivity.REQUEST_PERMISSION, PermissionRequestActivity.REQUEST_BLUETOOTH_PERMISSION)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}