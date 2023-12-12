package com.k21091.wearsensing.presentation

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.rememberScalingLazyListState
import com.google.android.gms.wearable.Node
import com.k21091.wearsensing.presentation.theme.WearSensingTheme

class MultiSensor: ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var AccSensor: Sensor? = null
    private var GyroSensor: Sensor? = null
    private var HeartRateSensor: Sensor? = null
    private var LightSensor: Sensor? = null
    var nodeSet: MutableSet<Node> = mutableSetOf()
    var senddata=SendData(this,nodeSet)
    val globalvariable = GlobalVariable.getInstance()
    private var tag="multi"
    private lateinit var accDataArray: Array<MutableState<String>>
    private lateinit var gyroDataArray: Array<MutableState<String>>
    private lateinit var heartrateDataArray: Array<MutableState<String>>
    private lateinit var lightDataArray: Array<MutableState<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            accDataArray = Array(3) { remember { mutableStateOf("データが取れませんでした") } }
            gyroDataArray = Array(3) { remember { mutableStateOf("データが取れませんでした") } }
            heartrateDataArray = Array(3) { remember { mutableStateOf("データが取れませんでした") } }
            heartrateDataArray[0].value =""
            heartrateDataArray[2].value =""
            lightDataArray = Array(3) { remember { mutableStateOf("データが取れませんでした") } }
            lightDataArray[0].value =""
            lightDataArray[2].value =""
            WearApp()
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        AccSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        GyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        HeartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        LightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        globalvariable.mode="false"
        senddata.setupSendMessage()
    }

    //センサーに何かしらのイベントが発生したときに呼ばれる
    override fun onSensorChanged(event: SensorEvent) {
        if(globalvariable.isAccSensorEnabled&&::accDataArray.isInitialized){
            sendthreedata("acc",accDataArray,event,Sensor.TYPE_LINEAR_ACCELERATION)
        }
        if(globalvariable.isGyroSensorEnabled&&::gyroDataArray.isInitialized){
            sendthreedata("gyro",gyroDataArray,event,Sensor.TYPE_GYROSCOPE)
        }
        if(globalvariable.isHeartRateSensorEnabled&&::heartrateDataArray.isInitialized){
            sendonedata("heart_rate",heartrateDataArray,event,Sensor.TYPE_HEART_RATE)
        }
        if(globalvariable.isLightSensorEnabled&&::lightDataArray.isInitialized){
            sendonedata("light",lightDataArray,event,Sensor.TYPE_LIGHT)
        }
        when (globalvariable.mode) {
            "start" -> {

                senddata.sendSensorData(tag,"start")
                globalvariable.mode="else"
            }
            "finish" -> {

                senddata.sendSensorData(tag,"finish")
                globalvariable.mode="else"
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
        if (globalvariable.isAccSensorEnabled) {
                sensorManager.registerListener(this, AccSensor, SensorManager.SENSOR_DELAY_UI)
        }
        if (globalvariable.isGyroSensorEnabled) {
                sensorManager.registerListener(this, GyroSensor, SensorManager.SENSOR_DELAY_UI)
        }
        if (globalvariable.isHeartRateSensorEnabled) {
                sensorManager.registerListener(this, HeartRateSensor, SensorManager.SENSOR_DELAY_UI)
        }
        if (globalvariable.isLightSensorEnabled) {
                sensorManager.registerListener(this, LightSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    //アクティビティが閉じられたときにリスナーを解除する
    override fun onPause() {
        super.onPause()
        //リスナーを解除しないとバックグラウンドにいるとき常にコールバックされ続ける
        sensorManager.unregisterListener(this)
    }

    fun sendonedata(sensortype: String,DataArray: Array<MutableState<String>>,event: SensorEvent,useSensor: Int?) {
        val sensor: Float
        if (event.sensor.type === useSensor) {
            sensor = event.values[0]
            DataArray[0].value = " "
            DataArray[1].value = "$sensor"
            DataArray[2].value = " "
            val log:String = System.currentTimeMillis().toString().plus(",").plus(sensor)
            senddata.sendSensorData(log,sensortype)
        }
    }
    fun sendthreedata(sensortype: String,DataArray: Array<MutableState<String>>,event: SensorEvent,useSensor: Int?){
        val sensorX: Float
        val sensorY: Float
        val sensorZ: Float
        if (event.sensor.type === useSensor) {
            sensorX = event.values[0]
            sensorY = event.values[1]
            sensorZ = event.values[2]
            DataArray[0].value = "X:$sensorX"
            DataArray[1].value = "Y:$sensorY"
            DataArray[2].value = "Z:$sensorZ"
            val log:String = System.currentTimeMillis().toString().plus(",").plus(sensorX).plus(",").plus(sensorY).plus(",").plus(sensorZ)
            senddata.sendSensorData(log,sensortype)
        }
    }

    @Composable
    fun WearApp() {
        val chipText: MutableState<String> = remember {
            mutableStateOf("記録開始")
        }
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
                    if (globalvariable.isAccSensorEnabled){
                        item { reusableComponents.MultiView(sensor = "加速度センサ", sensorDataArray = accDataArray, modifier = Modifier) }
                    }
                    if (globalvariable.isGyroSensorEnabled){
                        item { reusableComponents.MultiView(sensor = "ジャイロセンサ", sensorDataArray = gyroDataArray,modifier = Modifier) }
                    }
                    if (globalvariable.isHeartRateSensorEnabled){
                        item { reusableComponents.MultiView(sensor = "心拍センサ", sensorDataArray = heartrateDataArray, modifier = Modifier) }
                    }
                    if (globalvariable.isLightSensorEnabled){
                        item { reusableComponents.MultiView(sensor = "照度センサ", sensorDataArray = lightDataArray,modifier = Modifier) }
                    }
                    val chipSizeModifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()  // 幅を親に合わせる
                        .aspectRatio(3f)
                    item {  Chip(
                        modifier = chipSizeModifier,
                        onClick = {
                            if (chipText.value=="記録開始"){
                                globalvariable.mode="start"
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
                    )}

                }
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