package dev.akash.customcamarax.utils

import dev.akash.customcamarax.utils.Constants.DEFAULT_DATE_FORMAT_FOR_IMAGE_NAME
import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {

    fun getDateForImageName(
        milliSeconds: Long,
        dateFormat: String? = DEFAULT_DATE_FORMAT_FOR_IMAGE_NAME
    ): String? {
        val formatter = SimpleDateFormat(dateFormat, Locale.ENGLISH)

        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

}
