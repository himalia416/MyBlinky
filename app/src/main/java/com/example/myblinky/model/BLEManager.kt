package com.example.myblinky.model

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.bluetooth.le.ScanSettings.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.example.myblinky.permissions.PermissionManager

@SuppressLint("MissingPermission")
class BLEManager(val context: Context) {
    private val TAG = "BLEManager"
    private val REQUEST_ENABLE_BT = 3
    private val bluetoothManager =
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager)

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        bluetoothManager.adapter
    }

    private val bluetoothLeScanner: BluetoothLeScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    init {
        setupBluetoothAdapterStateHandler()
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            (context as Activity).startActivityForResult(
                enableBtIntent,
                REQUEST_ENABLE_BT
            )
        }
    }


    private fun setupBluetoothAdapterStateHandler() {
        val bluetoothAdapterStateReceiver: BroadcastReceiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {
                // Verify the action matches what we are looking for
                if (intent.action == BluetoothAdapter.ACTION_STATE_CHANGED) {

                    val previousState = intent.getIntExtra(
                        BluetoothAdapter.EXTRA_PREVIOUS_STATE,
                        BluetoothAdapter.ERROR
                    )

                    val currentState = intent.getIntExtra(
                        BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR
                    )

                    when (currentState) {
                        BluetoothAdapter.STATE_OFF ->
                            Log.d(TAG, "BluetoothAdapter State: Off")
                        BluetoothAdapter.STATE_TURNING_OFF ->
                            Log.d(TAG, "BluetoothAdapter State: Turning off")
                        BluetoothAdapter.STATE_ON ->
                            Log.d(TAG, "BluetoothAdapter State: On")
                        BluetoothAdapter.STATE_TURNING_ON ->
                            Log.d(TAG, "BluetoothAdapter State: Turning on")
                    }
                }
            }
        }

        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        (context as Activity).registerReceiver(bluetoothAdapterStateReceiver, filter)

    }


    private val leScanCallback: ScanCallback by lazy {
        object : ScanCallback() {

            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)
                Log.d(TAG, "BLE Scan Result")
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                Log.d(TAG, "BLE Scan Failed with ErrorCode: $errorCode")
            }
        }
    }
}