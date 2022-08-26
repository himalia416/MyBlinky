package com.example.myblinky.view

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myblinky.NavigationConst
import com.example.myblinky.R


@Composable
fun ConnectDeviceView(navController: NavController, devices: String) {
    Column {
        TopAppBar(

            title = {
                Text(text = devices)
            }

        )
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)

                ) {
                    Text(
                        text = "LED",
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 15.dp, horizontal = 10.dp)
                    )

                }


            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(8.dp)

                ) {
                    Text(
                        text = "BUTTON",
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 15.dp, horizontal = 10.dp)
                    )
                }
            }
        }
    }

}