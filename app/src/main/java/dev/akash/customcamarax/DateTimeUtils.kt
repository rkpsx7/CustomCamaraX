package dev.akash.customcamarax

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {

    fun getDateForImageName(
        milliSeconds: Long,
        dateFormat: String? = "dd-MM-yyyy hh:mm:ss.SSS"
    ): String? {
        val formatter = SimpleDateFormat(dateFormat, Locale.ENGLISH)

        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

}
