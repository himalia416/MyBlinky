package com.example.myblinky.view

import com.example.myblinky.utils.EMPTY

data class HomeViewState(
    val displayedText: String = String.EMPTY,
    val error: FieldError? = null
)

enum class FieldError {
    EMPTY, ALREADY_EXIST
}