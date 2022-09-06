package com.example.myblinky.adapter

import kotlinx.coroutines.flow.Flow

interface BlinkyAPI {
    fun initialize(): Boolean
    fun connectDeviceService(address: String)
    fun disconnect()
    fun turnLed(on: Boolean)
    fun getButtonState(): Flow<Boolean>
    fun turn(on: Boolean): ByteArray
    fun turnOn(): ByteArray
    fun turnOff(): ByteArray
}