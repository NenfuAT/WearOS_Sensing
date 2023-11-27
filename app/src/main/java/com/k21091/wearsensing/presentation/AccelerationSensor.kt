/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.k21091.wearsensing.presentation

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.wearable.Node
import com.k21091.wearsensing.presentation.theme.WearSensingTheme


class AccelerationSensor : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var AccSensor: Sensor? = null
    var nodeSet: MutableSet<Node> = mutableSetOf()
    var senddata=SendData(this,nodeSet)
    private var tag="acc"
    val globalvariable = GlobalVariable.getInstance()
    private lateinit var sensorDataArray: Array<MutableState<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            sensorDataArray = Array(3) { remember { mutableStateOf("データが取れませんでした") } }
            WearApp()
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        AccSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        globalvariable.mode="false"
        senddata.setupSendMessage()
    }

    //センサーに何かしらのイベントが発生したときに呼ばれる
    override fun onSensorChanged(event: SensorEvent) {
        val sensorX: Float
        val sensorY: Float
        val sensorZ: Float
        // Remove the gravity contribution with the high-pass filter.
        if (event.sensor.type === Sensor.TYPE_LINEAR_ACCELERATION) {
            sensorX = event.values[0]
            sensorY = event.values[1]
            sensorZ = event.values[2]
            sensorDataArray[0].value = "X:$sensorX"
            sensorDataArray[1].value = "Y:$sensorY"
            sensorDataArray[2].value = "Z:$sensorZ"
            if(globalvariable.mode=="true"){
                val log:String = sensorX.toString().plus(",").plus(sensorY).plus(",").plus(sensorZ)
                senddata.sendSensorData(log,tag)
            }
        }
    }
    //センサの精度が変更されたときに呼ばれる
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onResume() {
        super.onResume()
        //リスナーとセンサーオブジェクトを渡す
        //第一引数はインターフェースを継承したクラス、今回はthis
        //第二引数は取得したセンサーオブジェクト
        //第三引数は更新頻度 UIはUI表示向き、FASTはできるだけ早く、GAMEはゲーム向き
        sensorManager.registerListener(this, AccSensor, SensorManager.SENSOR_DELAY_UI)
    }

    //アクティビティが閉じられたときにリスナーを解除する
    override fun onPause() {
        super.onPause()
        //リスナーを解除しないとバックグラウンドにいるとき常にコールバックされ続ける
        sensorManager.unregisterListener(this)
    }

    @Composable
    fun WearApp() {
        WearSensingTheme {

            val contentModifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
            val iconModifier = Modifier
                .size(24.dp)
                .wrapContentSize(align = Alignment.Center)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(1f)
                    .padding(25.dp)
            ) {
                val reusableComponents = ReusableComponents()
                reusableComponents.SensingView(
                    "加速度センサ",
                    modifier = Modifier.fillMaxWidth(),
                    sensorDataArray
                )
            }

        }
    }


    @Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
    @Composable
    fun SmallRoundPreview() {
        WearApp()
    }

    @Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
    @Composable
    fun LargeRoundPreview() {
        WearApp()
    }
}
