package com.example.myblinky.scanner.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myblinky.R
import com.example.myblinky.scanner.data.FilterOption


@Composable
fun FilterDropDownView(
    filterOptions: List<FilterOption>,
    onFilterChanged: ( Boolean) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = { expanded = !expanded }
        ) {
            Icon(
                imageVector = Icons.Filled.FilterList,
                contentDescription = stringResource(id = R.string.menu_filter)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            filterOptions.forEach { filterOption ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable {
                            expanded = false
                            onFilterChanged(!filterOption.isSelected)
                        }
                        .padding(horizontal = 8.dp),
                ) {
                    Text( text = filterOption.filterName )
                    Checkbox(
                        checked = filterOption.isSelected,
                        onCheckedChange =
                        {
                            expanded = false
                            onFilterChanged(it)
                        }
                    )
                }
            }
        }
    }
}