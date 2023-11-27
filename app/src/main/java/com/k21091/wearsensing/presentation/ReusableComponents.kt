package com.k21091.wearsensing.presentation

import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalContext
import androidx.wear.compose.material.Chip
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.gms.wearable.Node

class ReusableComponents {

    @Composable
    fun SensingView(sensor:String,modifier: Modifier = Modifier, sensorDataArray:Array<MutableState<String>>) {
        var chipText: MutableState<String> = remember { mutableStateOf("記録開始") }
        val globalvariable = GlobalVariable.getInstance()
        Text(
            modifier = modifier,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.primary,
            text = sensor
        )
        Text(
            modifier = modifier,
            textAlign = TextAlign.Left,
            color = MaterialTheme.colors.primary,
            text = sensorDataArray[0].value
        )
        Text(
            modifier = modifier,
            textAlign = TextAlign.Left,
            color = MaterialTheme.colors.primary,
            text = sensorDataArray[1].value
        )
        Text(
            modifier = modifier,
            textAlign = TextAlign.Left,
            color = MaterialTheme.colors.primary,
            text = sensorDataArray[2].value
        )
        val chipSizeModifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()  // 幅を親に合わせる
            .aspectRatio(3f)
        Chip(
            modifier = chipSizeModifier,
            onClick = {
                if (chipText.value=="記録開始"){
                    globalvariable.mode="true"
                    chipText.value="記録終了"
                }
                else if (chipText.value=="記録終了"){
                    globalvariable.mode="finish"
                    chipText.value="記録開始"
                }
            },
            label = {
                Text(
                    text = chipText.value,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
        )
    }

    @Preview(
        apiLevel = 30,
        uiMode = Configuration.UI_MODE_TYPE_WATCH,
        device = Devices.WEAR_OS_SMALL_ROUND
    )

    @Composable
    fun AccelerometerChip(
        modifier: Modifier = Modifier,
        iconModifier: Modifier = Modifier
    ) {
        val context = LocalContext.current
        Chip(
            modifier = modifier,
            onClick = {
                val intent = Intent(context, AccelerationSensor::class.java)
                startActivity(context, intent, null)
            },
            label = {
                Text(
                    text = "加速度センサ",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
        )
    }

    @Composable
    fun GyroscopeChip(
        modifier: Modifier = Modifier,
        iconModifier: Modifier = Modifier
    ) {
        val context = LocalContext.current
        Chip(
            modifier = modifier,
            onClick = {
                val intent = Intent(context, GyroscopeSensor::class.java)
                startActivity(context, intent, null)
            },
            label = {
                Text(
                    text = "ジャイロセンサ",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
        )
    }

    @Composable
    fun HeartrateChip(
        modifier: Modifier = Modifier,
        iconModifier: Modifier = Modifier
    ) {
        val context = LocalContext.current
        Chip(
            modifier = modifier,
            onClick = {
                val intent = Intent(context, HeartrateSensor::class.java)
                startActivity(context, intent, null)
            },
            label = {
                Text(
                    text = "心拍センサ",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
        )
    }

    @Composable
    fun LightChip(
        modifier: Modifier = Modifier,
        iconModifier: Modifier = Modifier
    ) {
        val context = LocalContext.current
        Chip(
            modifier = modifier,
            onClick = {
                val intent = Intent(context, LightSensor::class.java)
                startActivity(context, intent, null)
            },
            label = {
                Text(
                    text = "照度センサ",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
        )
    }
}