package com.peanut.whut.smart

import android.os.Build
import java.io.File
import java.time.LocalDateTime
import kotlin.concurrent.thread

object LogService {
    private var logFile: String = ""

    fun __init__(logFile: String){
        this.logFile = logFile
    }

    fun log(log: String){
        thread {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    File(logFile).appendText(LocalDateTime.now().toString()+" "+log+"\n")
                }else{
                    File(logFile).appendText(log+"\n")
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
}