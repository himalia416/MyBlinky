package com.example.myblinky.adapter

import android.app.Service
import android.bluetooth.*
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

val UUID_SERVICE_DEVICE = UUID.fromString("00001523-1212-efde-1523-785feabcd123")

class BluetoothLeService : Service() {
    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val binder = LocalBinder()
    private val TAG = "BluetoothLeService"
    private var bluetoothGatt: BluetoothGatt? = null
    private var connectionState = STATE_DISCONNECTED
    private var address: String? = null
    private val ledState = MutableLiveData<Boolean>()
    private var buttonState = MutableStateFlow(false)

    private val ledCharacteristic: BluetoothGattCharacteristic? = null
    var ledOn: Boolean? = null
    private val STATE_RELEASED = byteArrayOf(0x00)
    private val STATE_PRESSED = byteArrayOf(0x01)

    private val UUID_LED_CHAR = UUID.fromString("00001525-1212-efde-1523-785feabcd123")
    private val UUID_BUTTON_CHAR = UUID.fromString("00001524-1212-efde-1523-785feabcd123")
    private val UUID_UPDATE_NOTIFICATION_DESCRIPTOR_CHAR =
        UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    private fun onLedStateChanged(
        on: Boolean
    ) {
        ledOn = on
        Log.d(TAG, "LED " + if (on) "ON" else "OFF")
        ledState.value = on
    }

    override fun onBind(intent: Intent): IBinder {
        address = intent.getStringExtra("ADDRESS")
        return binder
    }

    //    To access to the service for the activity
    inner class LocalBinder : Binder(), BlinkyAPI {

        override fun initialize(): Boolean {
            if (bluetoothAdapter == null) {
                Log.e(TAG, "Unable to obtain a BluetoothAdapter.")
                return false
            }
            return true
        }

        override fun disconnect() {

        }

        override fun turnLed(on: Boolean) {

        }

        override fun getButtonState(): Flow<Boolean> {
            return buttonState
        }

        override fun connectDeviceService(address: String) {
            bluetoothAdapter?.let { adapter ->
                try {
                    val device = adapter.getRemoteDevice(address)
                    bluetoothGatt =
                        device.connectGatt(this@BluetoothLeService, true, bluetoothGattCallback)
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
        if (status.contentEquals(STATE_PRESSED)) {
            buttonState.value = true
            Log.e(
                "button data callback:",
                "button data callback Pressed  ${status?.toHexString()}, buttonState ${buttonState.value}"
            )
        } else if (status.contentEquals(STATE_RELEASED)) {
            buttonState.value = false
            Log.e(
                "button data callback:",
                "button data callback Released ${status?.toHexString()} buttonState ${buttonState.value}"
            )
        } else Log.e(
            "button data callback:",
            "button data callback ${status?.toHexString()} and STATE_Pressed $STATE_PRESSED" +
                    "buttonState ${buttonState.value}"
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.extras?.getString("ADDRESS")?.let { address ->
            // connect
            binder.connectDeviceService(address)
        }
        return START_NOT_STICKY
    }


    private fun broadcastUpdate(action: String) {
        val intent = Intent(action)
        sendBroadcast(intent)
    }

    private fun broadcastUpdate(action: String, characteristic: BluetoothGattCharacteristic?) {
        val intent = Intent(action)
        sendBroadcast(intent)
    }


    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            val deviceAddress = gatt?.device?.address
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // successfully connected to the GATT Server
                connectionState = STATE_CONNECTED
                Log.e("Bluetooth Gatt Callback:", "Successfully connected to $deviceAddress")
                broadcastUpdate(ACTION_GATT_CONNECTED)
                gatt?.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.e("Bluetooth Gatt Callback", "Successfully disconnected from  $deviceAddress")
                broadcastUpdate(ACTION_GATT_DISCONNECTED)
                connectionState = STATE_DISCONNECTED
                gatt?.close()// disconnected from the GATT Server
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            Log.e("Services Discovered on", "Services Discovered on ${gatt?.device}")

            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED)
                var gattServices: List<BluetoothGattService> =
                    gatt?.services as List<BluetoothGattService>
                for (gattService in gattServices) {
                    var serviceUUID = gattService.uuid.toString()
                    readCharacteristic(gattService)
                    Log.e(
                        "Read characteristics on:",
                        "Read characteristics on:  $serviceUUID of $gattService"
                    )
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
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
            Log.d(TAG, "characteristics changed")
            buttonDataCallback(value)

        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
                Log.d(TAG, "characteristics read")
                val data = characteristic.value
                Log.d(TAG, value.first().toString())
//                Log.e("onCharacteristicRead", "Datos:")
                Log.e("onCharacteristicRead", data.toHexString())
                setCharacteristicNotification(characteristic = characteristic, true)

            } else {
                Log.i(TAG, "ACTION_DATA_READ: Error$status")
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i("BluetoothGattCallback", "Wrote to characteristic ${characteristic?.uuid} ")
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

    fun getSupportedGattServices(): List<BluetoothGattService?>? {
        return bluetoothGatt?.services
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
    }

    fun readCharacteristic(service: BluetoothGattService?) {
        if (bluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized")
            return
        }
        val gattCharacteristic: List<BluetoothGattCharacteristic> = service!!.characteristics
        val readButtonChar =
            bluetoothGatt!!.getService(UUID_SERVICE_DEVICE)?.getCharacteristic(UUID_BUTTON_CHAR)
        for (characteristic in gattCharacteristic) {
            Log.e("readCharacteristic", "Service characteristic: ${characteristic.uuid}")
            if (characteristic.uuid == UUID_BUTTON_CHAR) {
                Log.e(
                    "Yee readCharacteristic",
                    "Button Characteristics found: ${characteristic.uuid}"
                )
                bluetoothGatt!!.readCharacteristic(readButtonChar)
                setCharacteristicNotification(characteristic = readButtonChar, true)
            }
        }
        Log.e("OnServicesDiscovered", "-----------------------------")
    }


    fun BluetoothGattCharacteristic.isWritable(): Boolean =
        containsProperty(BluetoothGattCharacteristic.PROPERTY_WRITE)

    fun BluetoothGattCharacteristic.isWritableWithoutResponse(): Boolean =
        containsProperty(BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)

    fun BluetoothGattCharacteristic.containsProperty(property: Int): Boolean {
        return properties and property != 0
    }

    fun writeCharacteristic(characteristic: BluetoothGattCharacteristic?, payload: ByteArray) {
        val writeType = when {
            characteristic?.isWritable() == true -> BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            characteristic?.isWritableWithoutResponse() == true -> {
                BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
            }
            else -> error("Characteristic ${characteristic?.uuid} cannot be written to")
        }
        if (characteristic.uuid == UUID_LED_CHAR) {
            bluetoothGatt?.let { gatt ->
                characteristic.writeType = writeType
                characteristic.value = payload
                gatt.writeCharacteristic(characteristic)
            } ?: error("Not connected to a BLE device!")
        }

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
        if (this.UUID_BUTTON_CHAR == characteristic?.uuid) {
            val descriptor =
                characteristic?.getDescriptor(UUID_UPDATE_NOTIFICATION_DESCRIPTOR_CHAR)
            descriptor?.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
            bluetoothGatt?.writeDescriptor(descriptor!!)
        }
    }

    fun ByteArray.toHexString(): String =
        joinToString(separator = " ", prefix = "0x") { String.format("%02X", it) }
}


