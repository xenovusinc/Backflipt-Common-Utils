package com.backflipt.commons

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*


//const val DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'"
//
//const val DATE_FORMAT_PATTERN_v2 = "yyyy-MM-dd'T'HH:mm:ss-SS:00"
//
//
//val returnFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN)


/**
 * gets the current UTC time Using Epoch Time
 *
 * @param timeStamp Epoch Time
 * @returns the Formatted String
 */

fun getCurrentUtcTimeUsingEpoch(timeStamp: Long, dateFormatPattern: String): String {
    val sdf = SimpleDateFormat(dateFormatPattern).also {
        it.timeZone = TimeZone.getTimeZone("UTC")
    }
    return sdf.format(Date(timeStamp))
}

/**
 * This function will return the Date string with respect to the given timezone
 *
 * @param dateString : Date object in UTC as string
 * @param timeZone : represents the timeZone
 * @returns formatted date as string
 */

fun getDateStringWithRespectToZoneId(
        dateString: String,
        timeZone: String,
        dateFormatPattern: String,
        returnFormatPattern: String,
        zoneId: String
): String {
    val formatter = DateTimeFormatter.ofPattern(dateFormatPattern)
    val returnFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(returnFormatPattern)
    return LocalDateTime.parse(dateString, formatter)
            .atZone(ZoneId.of(zoneId))
            .withZoneSameInstant(ZoneId.of(timeZone))
            .format(returnFormatter)
}
/**
 * Date Time Formatter object
 */

/**
 * Gives string representation of yesterday(current date time - 24 hours)
 *
 * @return [String]
 */
fun getYesterdayDateTime(dateFormatPattern: String): String {
    val formatter = DateTimeFormatter.ofPattern(dateFormatPattern)
    val current = LocalDateTime
            .now()
            .minusDays(1)
    return current.format(formatter)
}

/**
 * Gives current date rounded by 30 minutes.
 * For example:
 * if current date is 09/Sep/2019 05:53:23, it will be rounded to 09/Sep/2019 06:00:00
 * if current date is 09/Sep/2019 05:59:59, it will be rounded to 09/Sep/2019 06:00:00
 * if current date is 09/Sep/2019 05:12:59, it will be rounded to 09/Sep/2019 05:30:00
 *
 * @return [LocalDateTime]
 */
fun getCurrentDateRoundedBy30Mins(): LocalDateTime {
    return LocalDateTime
            .now()
            .withSecond(0)
            .let {
                val minsToAdd = if (it.minute < 30) 30 - it.minute else 60 - it.minute
                it.plusMinutes(minsToAdd.toLong())
            }
}

/**
 * Format given date to UTC date time string
 *
 * @param date: Input [LocalDateTime] object
 * @return [String]
 */
fun formatDate(date: LocalDateTime, dateFormatPattern: String): String {
    val formatter = DateTimeFormatter.ofPattern(dateFormatPattern)
    return date
            .withNano(0)
            .atZone(ZoneId.systemDefault())
            .withZoneSameInstant(ZoneOffset.UTC)
            .format(formatter)
}

//private val leadsFormatter= DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssz")

/**
 * Gives [LocalDateTime] of current date time - [hours](current date time - 24 hours)
 *
 * @param hours No of Hours
 * @return [String]
 */
fun getDateTimeMinusHours(hours: Long, dateFormatPattern: String): String {
    val leadsFormatter = DateTimeFormatter.ofPattern(dateFormatPattern)
    return LocalDateTime
            .now()
            .minusHours(hours)
            .withNano(0)
            .atZone(ZoneId.systemDefault())
            .withZoneSameInstant(ZoneOffset.UTC)
            .format(leadsFormatter)
}


