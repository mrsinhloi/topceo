package com.workchat.core.utils

import androidx.work.Data
import org.threeten.bp.Instant
import org.threeten.bp.YearMonth
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

fun inputData(block: Data.Builder.() -> Unit): Data = Data.Builder().apply(block).build()
fun checkDefaultZoneId(){
    val id = TimeZone.getDefault().getID()
    if ("Asia/Hanoi".equals(id)) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"))
    }
}
fun Long.toDateTimeStr(): String {
    checkDefaultZoneId()
    val dateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(this@toDateTimeStr), ZoneId.systemDefault())
    return if (dateTime.year == YearMonth.now().year) {
        DateTimeFormatter.ofPattern("dd/MM hh:mm").format(dateTime)
    } else {
        DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm").format(dateTime)
    }
}

fun Long.toDateStr(): String {
    checkDefaultZoneId()
    val dateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(this@toDateStr), ZoneId.systemDefault())
    return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(dateTime)
}

fun ZonedDateTime.toDateStr(): String = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(this)

fun Long.toYearMonthStr(): String {
    checkDefaultZoneId()
    val dateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(this@toYearMonthStr), ZoneId.systemDefault())
    return if (dateTime.year == YearMonth.now().year) {
        DateTimeFormatter.ofPattern("MMMM").format(dateTime).capitalize()
    } else {
        DateTimeFormatter.ofPattern("MMMMM/yyyy").format(dateTime).capitalize()
    }
}

fun Long.toZonedDateTime(): ZonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(this), ZoneId.systemDefault())