package com.example.myblinky.adapter

import kotlinx.coroutines.flow.Flow

interface BlinkyAPI {

    fun disconnect()

    suspend fun turnLed(on: Boolean)

//    fun buttonState(): Flow<Boolean>
}