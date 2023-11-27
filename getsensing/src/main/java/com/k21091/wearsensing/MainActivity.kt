package com.k21091.wearsensing


import com.google.android.gms.wearable.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.android.gms.wearable.MessageClient

class MainActivity : AppCompatActivity(), MessageClient.OnMessageReceivedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Wearable.getMessageClient(applicationContext).addListener(this)
    }

    // 受信したメッセージからデータを整理し処理
    override fun onMessageReceived(messageEvent: MessageEvent) {
        when(messageEvent.path) {
            // tag別に処理を変える
            "acc" -> {
                // 加速度センサ
                val data = messageEvent.data.toString(Charsets.UTF_8) //文字列に変換
                //受け取ったデータmsgはコンマ区切りのcsv形式なので、value[]にそれぞれ格納します。
                val value = data.split(",").dropLastWhile { it.isEmpty() }.toTypedArray()
                val x = value[0].toFloat()
                val y = value[1].toFloat()
                val z = value[2].toFloat()
                val strTmp = """加速度センサー
                         X: $x
                         Y: $y
                         Z: $z"""
                val sensorText: TextView = findViewById(R.id.textView)
                sensorText.text = strTmp
            }
            "gyro" -> {
                // 加速度センサ
                val data = messageEvent.data.toString(Charsets.UTF_8) //文字列に変換
                //受け取ったデータmsgはコンマ区切りのcsv形式なので、value[]にそれぞれ格納します。
                val value = data.split(",").dropLastWhile { it.isEmpty() }.toTypedArray()
                val x = value[0].toFloat()
                val y = value[1].toFloat()
                val z = value[2].toFloat()
                val strTmp = """ジャイロセンサー
                         X: $x
                         Y: $y
                         Z: $z"""
                val sensorText: TextView = findViewById(R.id.textView)
                sensorText.text = strTmp
            }
            "heart_rate" ->{
                // 心拍センサ
                val data = messageEvent.data.toString(Charsets.UTF_8) //文字列に変換

                val x = ""
                val heartRateData = data.toFloat()
                val z = ""
                val strTmp = """心拍センサー
                         $x
                         $heartRateData
                         $z"""
                val sensorText: TextView = findViewById(R.id.textView)
                sensorText.text = strTmp
            }
            "light" ->{
                // 心拍センサ
                val data = messageEvent.data.toString(Charsets.UTF_8) //文字列に変換

                val x = ""
                val heartRateData = data.toFloat()
                val z = ""
                val strTmp = """照度センサー
                         $x
                         $heartRateData
                         $z"""
                val sensorText: TextView = findViewById(R.id.textView)
                sensorText.text = strTmp
            }
        }
    }
}