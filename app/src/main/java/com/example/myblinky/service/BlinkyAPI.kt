package com.example.myblinky.service

import kotlinx.coroutines.flow.StateFlow

interface BlinkyAPI {
    fun initialize(): Boolean
    fun connectDeviceService(address: String)
    fun turnLed(on: Boolean)
    fun getButtonState(): StateFlow<Boolean>
    fun turn(on: Boolean): ByteArray
    fun turnOn(): ByteArray
    fun turnOff(): ByteArray
}