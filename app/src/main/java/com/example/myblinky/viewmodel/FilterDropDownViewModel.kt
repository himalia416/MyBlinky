package com.example.myblinky.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.myblinky.view.FilterOption
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FilterDropDownViewModel @Inject constructor() : ViewModel() {
    private val _filterOptions = mutableStateOf(
        FilterOption(
            filterName = "Only devices advertising LBS UUID",
            isSelected = false
        ),
    )
    val filterOptions: State<FilterOption> = _filterOptions

    fun setFilterSelected(isSelected: Boolean) {
        _filterOptions.value = _filterOptions.value.copy(isSelected = isSelected)
    }
}
