package com.example.myblinky.viewmodel

import android.annotation.SuppressLint
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Build
import android.util.Log
import androidx.compose.runtime.remember
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myblinky.adapter.buttonState
import com.example.myblinky.model.BLEManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bleManager: BLEManager
) : ViewModel() {



    fun getBtnState(){
        val currentButtonState: MutableLiveData<Boolean> = MutableLiveData()
//        currentButtonState.value= buttonState.observe()
        }

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
                if (!mLeDevices.value.contains(result) && result != null) {
                    if (result.device?.name != null ) { //&& result.device?.uuids?.contains(ParcelUuid(LBS_UUID_SERVICE)) == true) {
                        mLeDevices.value += result
                    }
//                    Log.d("BLE Manager", "Device: $result")
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
}


