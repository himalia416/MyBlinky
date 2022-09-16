package com.example.myblinky.viewmodel

import android.annotation.SuppressLint
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myblinky.model.BLEManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
 class ScanningViewModel @Inject constructor(
    private val bleManager: BLEManager
) : ViewModel() {
    // Stops scanning after 10 seconds.
    private val SCAN_PERIOD: Long = 10000
    private var scanning = false
    private var viewModelJob: Job? = null

    @SuppressLint("StaticFieldLeak")
    fun startScanning() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (!scanning) { // Stops scanning after a pre-defined scan period.
            viewModelScope.launch (Dispatchers.Main) {
                delay(SCAN_PERIOD)
                scanning = false
                stopBleScan()
            }.also { viewModelJob = it }
            scanning = true
            bleManager.startScanning(leScanCallback)
        } else {
            scanning = false
            stopBleScan()
        }
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
                if (!mLeDevices.value.equals(result) && result != null) {
                    if (result.device?.name != null) {
                        if (checkDuplicateScanResult(mLeDevices.value, result)) {
                            mLeDevices.value += result
                        }
                    }
                }
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                Log.d("BLE Manager", "BLE Scan Failed with ErrorCode: $errorCode")
            }
        }
    }

    private fun checkDuplicateScanResult(value: List<ScanResult>, result: ScanResult): Boolean {
        val checkDevice = value.count { it.device == result.device }
        return checkDevice < 1
    }

    fun stopBleScan() {
        bleManager.bluetoothLeScanner.stopScan(leScanCallback)
    }

}






