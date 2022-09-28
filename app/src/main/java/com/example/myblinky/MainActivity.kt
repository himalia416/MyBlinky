package com.example.myblinky
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myblinky.navigation.BleViewModel
import dagger.hilt.android.AndroidEntryPoint
import no.nordicsemi.android.common.navigation.DestinationId
import no.nordicsemi.android.common.navigation.NavigationView
import no.nordicsemi.android.common.permission.RequireBluetooth
import no.nordicsemi.android.common.theme.NordicActivity
import no.nordicsemi.android.common.theme.NordicTheme

@AndroidEntryPoint
class MainActivity : NordicActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NordicTheme {
                val blinkyControlDestination = hiltViewModel<BleViewModel>().blinkyControlDestination
                val scanningDestinations = hiltViewModel<BleViewModel>().scanningDestinations
                Surface {
                    RequireBluetooth {
                        NavigationView ( destinations = scanningDestinations
                                + blinkyControlDestination
                        )
                    }
                }
            }
        }
    }

    companion object {
        val Main = DestinationId(NavigationConst.HOME)
        val ConnectView = DestinationId(NavigationConst.CONTROL_BLINKY)
    }
}




