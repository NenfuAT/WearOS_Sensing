package com.k21091.wearsensing.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.rememberScalingLazyListState
import androidx.wear.compose.material.scrollAway
import com.k21091.wearsensing.presentation.theme.WearSensingTheme
private var globalvariable = GlobalVariable.getInstance()
class MainActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp()
        }
    }

    override fun onResume() {
        super.onResume()
        globalvariable.isAccSensorEnabled = false
        globalvariable.isGyroSensorEnabled = false
        globalvariable.isHeartRateSensorEnabled = false
        globalvariable.isLightSensorEnabled = false
        setContent {
            WearApp()
        }
    }
    @Composable
    fun WearApp() {
        WearSensingTheme {
            // TODO: Swap to ScalingLazyListState
            val listState = rememberScalingLazyListState()

            val contentModifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
            val iconModifier = Modifier
                .size(24.dp)
                .wrapContentSize(align = Alignment.Center)

            /* *************************** Part 4: Wear OS Scaffold *************************** */
            // TODO (Start): Create a Scaffold (Wear Version)
            Scaffold(
                timeText = {
                    TimeText(modifier = Modifier.scrollAway(listState))
                },
                vignette = {
                    // Only show a Vignette for scrollable screens. This code lab only has one screen,
                    // which is scrollable, so we show it all the time.
                    Vignette(vignettePosition = VignettePosition.TopAndBottom)
                },
                positionIndicator = {
                    PositionIndicator(
                        scalingLazyListState = listState
                    )
                }
            ) {

                // Modifiers used by our Wear composables.
                val contentModifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                val iconModifier = Modifier
                    .size(24.dp)
                    .wrapContentSize(align = Alignment.Center)

                /* *************************** Part 3: ScalingLazyColumn *************************** */
                ScalingLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    autoCentering = AutoCenteringParams(itemIndex = 0),
                    state = listState
                ) {

                    val reusableComponents = ReusableComponents()
                    item {
                        Text("使うセンサーを",fontSize = 20.sp)
                    }
                    item {
                        Text("選んでネ",fontSize = 20.sp)
                    }
                    item { reusableComponents.AccToggle(contentModifier) }
                    item { reusableComponents.GyroToggle(contentModifier) }
                    item { reusableComponents.HeartRateToggle(contentModifier) }
                    item { reusableComponents.LightToggle(contentModifier) }
                    item { reusableComponents.SetMultiChip(contentModifier) }
                }
            }
        }
    }
}

