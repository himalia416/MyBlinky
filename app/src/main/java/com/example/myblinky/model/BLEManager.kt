package com.example.myblinky.model

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.*
import android.os.Build
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.myblinky.adapter.BluetoothLeService.Companion.UUID_SERVICE_DEVICE
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject


@SuppressLint("NewApi")
@RequiresApi(Build.VERSION_CODES.M)
class BLEManager @Inject constructor(
) {
    private val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val bluetoothLeScanner: BluetoothLeScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    val devices: MutableStateFlow<List<ScanResult>> = MutableStateFlow(emptyList())

    private val leScanCallback: ScanCallback by lazy {
        object : ScanCallback() {
            @SuppressLint("MissingPermission")
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)
                if (!devices.value.equals(result) && result != null) {
                    if (result.device?.name != null) {
                        if (checkDuplicateScanResult(devices.value, result)) {
                            devices.value += result
                        }
                    }
                }
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                Log.e("BLE Manager", "BLE Scan Failed with ErrorCode: $errorCode")
            }
        }
    }

    private val scanSettings: ScanSettings by lazy {
        ScanSettings.Builder()
            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
            .setLegacy(false)
            .setPhy(ScanSettings.PHY_LE_ALL_SUPPORTED)
            .setReportDelay(0)
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
    }

    private fun scanFilters(filterByUuid: Boolean): MutableList<ScanFilter> {
        val list: MutableList<ScanFilter> = ArrayList()
        devices.value = emptyList()
        val scanFilterName =
            if (filterByUuid){
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




