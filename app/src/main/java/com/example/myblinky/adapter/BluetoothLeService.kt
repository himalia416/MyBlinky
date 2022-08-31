package com.example.myblinky.adapter

import android.app.Service
import android.bluetooth.*
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log


abstract class BluetoothLeService : Service() {
    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val binder = LocalBinder()
    private val TAG = "BluetoothLeService"
    private var bluetoothGatt: BluetoothGatt? = null
    private var connectionState = STATE_DISCONNECTED

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    //    To access to the service for the activity
    inner class LocalBinder : Binder() {
        fun getService(): BluetoothLeService {
            return this@BluetoothLeService
        }
    }

    fun initialize(): Boolean {
        if (bluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.")
            return false
        }
        return true
    }

    private fun broadcastUpdate(action: String) {
        val intent = Intent(action)
        sendBroadcast(intent)
    }

    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                val intentAction: String = ACTION_GATT_CONNECTED
                // successfully connected to the GATT Server
                connectionState = STATE_CONNECTED
                broadcastUpdate(ACTION_GATT_CONNECTED)
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                val device =
                    Log.w("BluetoothGattCallback", "Successfully disconnected from")
                gatt?.close()// disconnected from the GATT Server
            }
        }
    }

    fun connectDeviceService(address: String) {
        bluetoothAdapter?.let { adapter ->
            try {
                val device = adapter.getRemoteDevice(address)
                bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback)
            } catch (exception: IllegalArgumentException) {
                Log.w(TAG, "Device not found with provided address.")

            }
            // connect to the GATT server on the device
        } ?: run {
            Log.w(TAG, "BluetoothAdapter not initialized")
        }
    }

    companion object {
        const val ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED"

        const val STATE_DISCONNECTED = 0
        const val STATE_CONNECTED = 2

    }

    fun getSupportedGattServices(): List<BluetoothGattService?>? {
        return bluetoothGatt?.services
    }


}