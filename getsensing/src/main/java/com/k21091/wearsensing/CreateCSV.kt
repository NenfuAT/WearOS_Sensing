package com.k21091.wearsensing

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.BufferedWriter
import java.io.FileWriter
import java.io.PrintWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CreateCSV(context: Context) {

    private val TAG = "CreateCSV"
    private val fileAppend: Boolean = true // true=追記, false=上書き
    private val context: Context = context
    private val extension: String = ".csv"

    fun writeText(type: String, dataList: MutableList<String>) {
        // 現在の日時を取得
        val currentDateTime = LocalDateTime.now()
        // フォーマットを指定して日時を文字列に変換
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formattedDateTime = currentDateTime.format(formatter)
        val fileName = type + formattedDateTime
        val filePath: String =
            context.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                .toString().plus("/").plus(fileName).plus(extension) // 内部ストレージのDocumentのURL

        try {
            val fil = FileWriter(filePath, fileAppend)
            val pw = PrintWriter(BufferedWriter(fil))
            dataList.forEach { row ->
                pw.println(row)
            }
            pw.close()

            // Log the success
            Log.i(TAG, "CSV file successfully created at $filePath")
        } catch (e: Exception) {
            // Log any errors that occurred during file creation
            Log.e(TAG, "Error creating CSV file: ${e.message}")
        }
    }
}
