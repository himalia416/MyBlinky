package com.example.myblinky.scanner.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myblinky.R

@Composable
fun LedView(
    onLedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isLedOn by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier,
            ) {
                Image(
                    imageVector= Icons.Default.Lightbulb,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 16.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                )
                Text(
                    text = stringResource(id = R.string.led_name),
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
            Text(
                text = stringResource(id = R.string.led_description),
                modifier = Modifier.padding(top = 4.dp),
            )
            ToggleLedSwitch(
                isLedOn = isLedOn,
                onLedChange = {
                    onLedChange(it)
                    isLedOn = it
                }
            )
        }
    }
}

@Composable
fun ToggleLedSwitch(
    isLedOn: Boolean,
    onLedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Text(
            text = when (isLedOn) {
                true -> stringResource(id = R.string.led_on)
                false -> stringResource(id = R.string.led_off)
            },
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = isLedOn,
            onCheckedChange = onLedChange,
        )
    }
}