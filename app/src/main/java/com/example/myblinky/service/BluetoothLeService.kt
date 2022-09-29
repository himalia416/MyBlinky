package com.example.myblinky.service

import android.app.Service
import android.bluetooth.*
import android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.example.myblinky.blinky.spec.BlinkySpecifications.Companion.UUID_BUTTON_CHAR
import com.example.myblinky.blinky.spec.BlinkySpecifications.Companion.UUID_LED_CHAR
import com.example.myblinky.blinky.spec.BlinkySpecifications.Companion.UUID_SERVICE_DEVICE
import com.example.myblinky.blinky.spec.BlinkySpecifications.Companion.UUID_UPDATE_NOTIFICATION_DESCRIPTOR_CHAR
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class BluetoothLeService : Service() {
    private val _bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private var binder: LocalBinder? = LocalBinder()
    private val TAG = "BluetoothLeService"
    private var bluetoothGatt: BluetoothGatt? = null
    private var connectionState = STATE_DISCONNECTED
    private var address: String? = null

    private var buttonState = MutableStateFlow(false)
    private var ledCharacteristic: BluetoothGattCharacteristic? = null

    private val STATE_OFF = byteArrayOf(0x00)
    private val STATE_ON = byteArrayOf(0x01)

    override fun onBind(intent: Intent): IBinder {
        address = intent.getStringExtra("ADDRESS")
        return binder as LocalBinder
    }

    //    To access to the service for the activity
    inner class LocalBinder : Binder(), BlinkyAPI {

        override fun initialize(): Boolean {
            if (_bluetoothAdapter == null) {
                Log.e(TAG, "Unable to obtain a BluetoothAdapter.")
                return false
            }
            return true
        }

        override fun turnLed(on: Boolean) {
            Log.w("AAA", "turnLed")
            writeCharacteristic(
                ledCharacteristic,
                turn(on),
                WRITE_TYPE_DEFAULT
            )
        }

        override fun turn(on: Boolean): ByteArray = if (on) {
            turnOn()
        } else turnOff()

        override fun turnOn(): ByteArray {
            return STATE_ON
        }

        override fun turnOff(): ByteArray {
            return STATE_OFF
        }

        override fun getButtonState(): StateFlow<Boolean> {
            return buttonState
        }

        override fun connectDeviceService(address: String) {
            _bluetoothAdapter?.let { adapter ->
                try {
                    val device = adapter.getRemoteDevice(address)
                    bluetoothGatt =
                        device.connectGatt(
                            this@BluetoothLeService,
                            true,
                            bluetoothGattCallback)
                } catch (exception: IllegalArgumentException) {
                    Log.w(TAG, "Device not found with provided address.")

                }
                // connect to the GATT server on the device
            } ?: run {
                Log.w(TAG, "BluetoothAdapter not initialized")
            }
        }

    }

    fun buttonDataCallback(status: ByteArray?) {
        if (status.contentEquals(STATE_ON)) {
            buttonState.value = true
        } else if (status.contentEquals(STATE_OFF)) {
            buttonState.value = false
        } else Log.e(
            "button data callback error:",
            "button data callback ${status?.toHexString()} and buttonState ${buttonState.value}"
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("AAA", "onStartCommand")
        intent?.extras?.getString("ADDRESS")?.let { address ->
            // connect
            binder?.connectDeviceService(address)
        }
        return START_NOT_STICKY
    }


    private fun broadcastUpdate(action: String) {
        val intent = Intent(action)
        sendBroadcast(intent)
    }

    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                connectionState = STATE_CONNECTED   // successfully connected to the GATT Server
                broadcastUpdate(ACTION_GATT_CONNECTED)
                gatt?.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                broadcastUpdate(ACTION_GATT_DISCONNECTED)
                connectionState = STATE_DISCONNECTED
                gatt?.close()   // disconnected from the GATT Server
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            Log.i("Services Discovered on", "Services Discovered on ${gatt?.device}")

            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED)
                val gattServices = gatt?.services ?: emptyList()
                for (gattService in gattServices) {
                    readCharacteristic()
                    ledCharacteristic = gattService.getCharacteristic(UUID_LED_CHAR)
                }
            } else {
                Log.w(TAG, "onServicesDiscovered received: $status")
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            Log.i(TAG, "characteristics changed")
            buttonDataCallback(value)
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE)
                setCharacteristicNotification(characteristic = characteristic, true)
            } else {
                Log.e(TAG, "ACTION_DATA_READ: Error$status")
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("BluetoothGattCallback", "Write to characteristic ${characteristic?.uuid} ")
            } else {
                Log.e(TAG, "ACTION_DATA_WRITE: Error$status")
            }
        }
    }


    companion object {
        const val ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED"
        const val ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE"
        const val ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED"

        const val STATE_DISCONNECTED = 0
        const val STATE_CONNECTED = 2



    }

    override fun onUnbind(intent: Intent?): Boolean {
        close()
        return super.onUnbind(intent)
    }
    private fun close() {
        bluetoothGatt?.let { gatt ->
            gatt.close()
            bluetoothGatt = null
        }
        binder = null
        stopSelf()
    }

    fun readCharacteristic() {
        bluetoothGatt?.let { gatt ->
            gatt.getService(UUID_SERVICE_DEVICE)
                ?.getCharacteristic(UUID_BUTTON_CHAR)
                ?.let { gatt.readCharacteristic(it) }
                .also {
                    Log.i("OnServicesDiscovered", "-----------------------------")
                }
        } ?: run {
            Log.w(TAG, "BluetoothAdapter not initialized")
        }
    }

    fun writeCharacteristic(
        characteristic: BluetoothGattCharacteristic?,
        payload: ByteArray,
        writeTypeDefault: Int
    ) {
        bluetoothGatt?.let { gatt ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                gatt.writeCharacteristic(characteristic!!, payload, writeTypeDefault)
            } else {
                characteristic?.writeType = writeTypeDefault
                characteristic?.value = payload
                gatt.writeCharacteristic(characteristic!!)
            }
        } ?: error("Not connected to a BLE device!")
    }

    fun setCharacteristicNotification(
        characteristic: BluetoothGattCharacteristic?,
        enabled: Boolean
    ) {
        if (bluetoothGatt == null) {
            Log.w(TAG, "BluetoothGatt not initialized")
            return
        }
        bluetoothGatt!!.setCharacteristicNotification(characteristic, enabled)
        if (UUID_BUTTON_CHAR == characteristic?.uuid) {
            val descriptor =
                characteristic.getDescriptor(UUID_UPDATE_NOTIFICATION_DESCRIPTOR_CHAR)
            descriptor?.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            bluetoothGatt?.writeDescriptor(descriptor!!)
        }
    }

    private fun ByteArray.toHexString(): String =
        joinToString(separator = " ", prefix = "0x") { String.format("%02X", it) }
}


