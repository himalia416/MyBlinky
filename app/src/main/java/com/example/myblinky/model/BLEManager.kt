package com.example.myblinky.model

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import com.example.myblinky.adapter.BluetoothLeService.Companion.UUID_SERVICE_DEVICE
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject


class BLEManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val bluetoothManager: BluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter = bluetoothManager.adapter
    private val bluetoothLeScanner: BluetoothLeScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    val devices: MutableStateFlow<List<ScanResult>> = MutableStateFlow(emptyList())
    private val leScanCallback: ScanCallback by lazy {
        object : ScanCallback() {
            @SuppressLint("MissingPermission")
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                result
                    ?.takeIf { checkDuplicateScanResult(devices.value, it) }
                    ?.also { devices.value += it }
            }

            override fun onScanFailed(errorCode: Int) {
                Log.e("BLE Manager", "BLE Scan Failed with ErrorCode: $errorCode")
            }
        }
    }

    private val scanSettings: ScanSettings by lazy {
        ScanSettings.Builder()
//            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
            .setLegacy(false)
//            .setPhy(ScanSettings.PHY_LE_ALL_SUPPORTED)
            .setReportDelay(0)
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
    }

    private fun scanFilters(filterByUuid: Boolean): MutableList<ScanFilter> {
        val list: MutableList<ScanFilter> = ArrayList()
        val scanFilterName =
            if (filterByUuid){
                devices.value = emptyList()
                ScanFilter.Builder().setServiceUuid(ParcelUuid(UUID_SERVICE_DEVICE)).build()
            } else {
                ScanFilter.Builder().setDeviceName(null).build()
            }
        list.add(scanFilterName)
        return list
    }

    private fun checkDuplicateScanResult(value: List<ScanResult>, result: ScanResult): Boolean {
        return !value.any { it.device == result.device }
    }

    fun startScanning(filterByUuid: Boolean) {
        bluetoothLeScanner
            .startScan(
                scanFilters(filterByUuid),
                scanSettings,
                leScanCallback
            )
    }

    fun stopScan() {
        bluetoothLeScanner.stopScan(leScanCallback)
    }
}




