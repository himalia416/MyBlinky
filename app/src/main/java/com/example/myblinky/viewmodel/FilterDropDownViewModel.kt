package com.example.myblinky.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.myblinky.callback.FilterOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class FilterDropDownViewModel @Inject constructor() : ViewModel() {
    private val _filterOptions = mutableStateListOf(
        FilterOption(
            filterName = "Only devices advertising LBS UUID",
            isSelected = false
        ),
        FilterOption(
            filterName = "All devices",
            isSelected = false
        )
    )
    val filterOptions = _filterOptions
    var isFilterByUuid = MutableStateFlow(false)
    fun setFilterSelectedAtIndex(index: Int, isSelected: Boolean) {
        _filterOptions[index] = _filterOptions[index].copy(isSelected = isSelected)
        isFilterByUuid.value =
            _filterOptions[0].isSelected == true && _filterOptions[1].isSelected == false
    }
}
