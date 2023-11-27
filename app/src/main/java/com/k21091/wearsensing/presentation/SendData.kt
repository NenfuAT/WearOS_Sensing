package com.k21091.wearsensing.presentation

import android.content.Context
import com.google.android.gms.wearable.*
import android.util.Log
import com.google.android.gms.tasks.Task

class SendData(context: Context, nodeSet:MutableSet<Node>){
    val context:Context = context
    var nodeSet:MutableSet<Node> = nodeSet
    fun setupSendMessage() {
        val capabilityInfo: Task<CapabilityInfo> =
            Wearable.getCapabilityClient(context)
                .getCapability(
                    "sensorCapabilities",// 中身は自由（後程使います）
                    CapabilityClient.FILTER_REACHABLE
                )

        capabilityInfo.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                nodeSet = task.result.nodes
                Log.d("setupSendMessage","successSetup, ".plus(nodeSet))
            } else {
                Log.d("setupSendMessage","defeatSetup")
            }
        }
    }

    // データを送信
   fun sendSensorData(dataText: String, tag: String){
        nodeSet.let {
            pickBestNodeId(it)?.let { nodeId ->
                val data = dataText.toByteArray(Charsets.UTF_8) //バイナリ変換
                Wearable.getMessageClient(context)
                    .sendMessage(nodeId, tag, data)
                    .apply {
                        addOnSuccessListener {
                            // 送信成功
                            Log.d("sendSensorData","success")
                        }
                        addOnFailureListener {
                            // 送信失敗
                            Log.d("sendSensorData","defeat")
                        }
                    }
            }
        }
    }


    // 送信先として最適なノードを選択
    private fun pickBestNodeId(nodes: Set<Node>): String? {
        return nodes.firstOrNull { it.isNearby }?.id ?: nodes.firstOrNull()?.id
    }
}