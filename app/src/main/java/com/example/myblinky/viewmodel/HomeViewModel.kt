package com.example.myblinky.viewmodel

import android.annotation.SuppressLint
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
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

    val mLeDevices: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    private val leScanCallback: ScanCallback by lazy {
        object : ScanCallback() {
            @SuppressLint("MissingPermission")
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)
                if (!mLeDevices.value.contains(result?.device?.name))
                    if (result?.device?.name != null) {
                        mLeDevices.value += result.device?.name ?: ""
                    }
                Log.d("BLE Manager", "Device: $result")
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                Log.d("BLE Manager", "BLE Scan Failed with ErrorCode: $errorCode")
            }
        }
    }
}


