package com.k21091.wearsensing


import android.annotation.SuppressLint
import com.google.android.gms.wearable.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.wearable.MessageClient

class MainActivity : AppCompatActivity(), MessageClient.OnMessageReceivedListener {
    val dataList: MutableList<String> = mutableListOf()
    val accdataList: MutableList<String> = mutableListOf()
    val gyrodataList: MutableList<String> = mutableListOf()
    val heartratedataList: MutableList<String> = mutableListOf()
    val lightdataList: MutableList<String> = mutableListOf()
    lateinit var createCSV: CreateCSV
    var send = false
    var accstr = ""
    var gyrostr = ""
    var heartratestr=""
    var lightstr=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Wearable.getMessageClient(applicationContext).addListener(this)
        createCSV=CreateCSV(this)
        val Yes_Button: Button = findViewById(R.id.yes_button)
        val No_Button: Button = findViewById(R.id.no_button)
        val save_Text: TextView = findViewById(R.id.saveView)
        Yes_Button.visibility = View.GONE
        No_Button.visibility = View.GONE
        save_Text.visibility = View.GONE
    }

    // 受信したメッセージからデータを整理し処理

    @SuppressLint("SetTextI18n")
    override fun onMessageReceived(messageEvent: MessageEvent) {
        val sensorText: TextView = findViewById(R.id.textView)
        val strtmp="""
            $accstr
            $gyrostr
            $heartratestr
            $lightstr
        """
        sensorText.text = strtmp
        when(messageEvent.path) {

            // tag別に処理を変える
            "acc" -> {
                if (accdataList.isEmpty()) {
                    // リストが空の場合の処理
                    accdataList.add("time,x,y,z")
                }
                accstr=getThreeData("加速度センサ",messageEvent,accdataList)
            }
            "gyro" -> {
                if (gyrodataList.isEmpty()) {
                    // リストが空の場合の処理
                    gyrodataList.add("time,x,y,z")
                }
                gyrostr=getThreeData("ジャイロセンサ",messageEvent,gyrodataList)
            }
            "heart_rate" ->{
                if (heartratedataList.isEmpty()) {
                    // リストが空の場合の処理
                    heartratedataList.add("time,heart_rate")
                }
                heartratestr=getOneData("心拍センサ",messageEvent,heartratedataList)
            }
            "light" ->{
                if (lightdataList.isEmpty()) {
                    // リストが空の場合の処理
                    lightdataList.add("time,light")
                }
                lightstr=getOneData("照度センサ",messageEvent,lightdataList)
            }
            "start" -> {
                send=true
                val rec_Text: TextView = findViewById(R.id.recView)
                rec_Text.text="記録中"
            }
            "finish" -> {
                Log.d("getdata","sendfin")
                val data = messageEvent.data.toString(Charsets.UTF_8) //文字列に変換
                val rec_Text: TextView = findViewById(R.id.recView)
                val save_Text: TextView = findViewById(R.id.saveView)
                val Yes_Button: Button = findViewById(R.id.yes_button)
                val No_Button: Button = findViewById(R.id.no_button)
                Yes_Button.visibility = View.VISIBLE
                No_Button.visibility = View.VISIBLE
                save_Text.visibility = View.VISIBLE
                Yes_Button.setOnClickListener {
                    if(!accdataList.isEmpty()){
                        createCSV.writeText("acc",accdataList)
                        accdataList.clear()
                    }
                    if(!gyrodataList.isEmpty()){
                        createCSV.writeText("gyro",gyrodataList)
                        gyrodataList.clear()
                    }
                    if(!heartratedataList.isEmpty()){
                        createCSV.writeText("heartrate",heartratedataList)
                        heartratedataList.clear()
                    }
                    if(!lightdataList.isEmpty()){
                        createCSV.writeText("light",lightdataList)
                        lightdataList.clear()
                    }
                    Yes_Button.visibility = View.GONE
                    No_Button.visibility = View.GONE
                    save_Text.visibility = View.GONE
                }
                No_Button.setOnClickListener {
                    dataList.clear()
                    Yes_Button.visibility = View.GONE
                    No_Button.visibility = View.GONE
                    save_Text.visibility = View.GONE
                }
                send=false
                rec_Text.text=""
            }
        }
    }

    fun getOneData(
        sensorname: String,
        messageEvent: MessageEvent,
        dataList: MutableList<String>
    ): String {
        // 心拍センサ
        val data = messageEvent.data.toString(Charsets.UTF_8) //文字列に変換
        if(send) {
            dataList.add(data)
        }
        val value = data.split(",").dropLastWhile { it.isEmpty() }.toTypedArray()
        val x = ""
        val Data = value[1].toFloat()
        val z = ""
        return """$sensorname
                         $x
                         $Data
                         $z"""
    }
    fun getThreeData(
        sensorname: String,
        messageEvent: MessageEvent,
        dataList: MutableList<String>
    ): String {
        // 加速度センサ
        val data = messageEvent.data.toString(Charsets.UTF_8) //文字列に変換
        if (send) {
            dataList.add(data)
        }
        //受け取ったデータmsgはコンマ区切りのcsv形式なので、value[]にそれぞれ格納します。
        val value = data.split(",").dropLastWhile { it.isEmpty() }.toTypedArray()
        val x = value[1].toFloat()
        val y = value[2].toFloat()
        val z = value[3].toFloat()
        return """$sensorname
                         X: $x
                         Y: $y
                         Z: $z"""
    }

}