package com.example.myblinky.viewmodel

import android.annotation.SuppressLint
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.myblinky.adapter.BluetoothLeService
import com.example.myblinky.model.BLEManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bleManager: BLEManager
) : ViewModel() {
    @SuppressLint("StaticFieldLeak")
    fun startScanning() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        bleManager.startScanning(leScanCallback)
    } else {
        TODO("VERSION.SDK_INT < M")
    }

    val mLeDevices: MutableStateFlow<List<ScanResult>> = MutableStateFlow(emptyList())
    private val leScanCallback: ScanCallback by lazy {
        object : ScanCallback() {
            @SuppressLint("MissingPermission")
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)
                if (!mLeDevices.value.contains(result)) {
                    if (result?.device?.name != null) {
                        mLeDevices.value += result
                    }
                    Log.d("BLE Manager", "Device: $result")
                }
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                Log.d("BLE Manager", "BLE Scan Failed with ErrorCode: $errorCode")
            }
        }
    }

    fun stopBleScan() {
        bleManager.bluetoothLeScanner.stopScan(leScanCallback)
    }

    @SuppressLint("StaticFieldLeak")
    private var bluetoothService: BluetoothLeService? = null
    private val TAG = "BluetoothLeService"

    // Code to manage Service lifecycle.
    fun serviceConnection(deviceAddress: String) {
        val serviceConnection: ServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(
                componentName: ComponentName,
                service: IBinder
            ) {
                bluetoothService = (service as BluetoothLeService.LocalBinder).getService()
                bluetoothService?.let { bluetooth ->
                    // call functions on service to check connection and connect to devices
                    if (!bluetooth.initialize()) {
                        Log.e(TAG, "Unable to initialize Bluetooth")
//                    finish()
                    }
                    // perform device connection

                    bluetoothService!!.connectDeviceService(deviceAddress)
                    Log.e(TAG, "Connected to Device Service $deviceAddress")
                }
            }

            override fun onServiceDisconnected(componentName: ComponentName) {
                bluetoothService = null
            }

        }
//        val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
//        bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)


    }
}


