package com.hardik.remember.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
class DateTimeUtil {
    companion object{
        private val dataFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")


        @Synchronized
        fun getCurrentDate():String{
            val currentDateTime = LocalDateTime.now()
            return currentDateTime.format(dataFormatter)
        }

        @Synchronized
        fun getCurrentTime():String{
            val currentDateTime = LocalDateTime.now()

            return currentDateTime.format(timeFormatter)
        }
    }

}