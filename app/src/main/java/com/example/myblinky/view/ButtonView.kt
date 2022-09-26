package com.example.myblinky.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myblinky.R


@Composable
fun ButtonView(
    buttonName: String,
    buttonDescription: String,
    buttonsState: Boolean,
    modifier: Modifier,
) {
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
                    imageVector = Icons.Default.RadioButtonChecked,
                    contentDescription = null,
                    modifier = Modifier.padding(8.dp)
                )
                Text(
                    text = buttonName,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = buttonDescription,
                modifier = Modifier.padding(top = 4.dp),
                textAlign = TextAlign.Center
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Text(text = stringResource(id = R.string.button_state))
                ButtonState(buttonsState)
            }
        }
    }
}

@Composable
fun ButtonState(
    buttonsState: Boolean,
    modifier: Modifier = Modifier,
) {
    Text(
        text = when (buttonsState) {
            true -> stringResource(id = R.string.button_pressed)
            false -> stringResource(id = R.string.button_released)
        },
        modifier = modifier,
    )
}