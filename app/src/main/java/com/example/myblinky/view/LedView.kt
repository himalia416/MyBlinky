package com.example.myblinky.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myblinky.R

@Composable
fun LedView(
    ledName: String,
    ledDescription: String,
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
                Icon(
                    imageVector= Icons.Default.Lightbulb,
                    contentDescription = null,
                    modifier = Modifier.padding(8.dp)
                )
                Text(
                    text = ledName,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = ledDescription,
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