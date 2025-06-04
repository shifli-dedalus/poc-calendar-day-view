package com.example.sample.ui

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


fun setUpdateTimeInZonedDateTime(zonedDateTime: ZonedDateTime, hour: Int, minute: Int): ZonedDateTime =
    zonedDateTime
        .withHour(hour)
        .withMinute(minute)


fun setUpdatedDateInZonedDateTime(zonedDateTime: ZonedDateTime, localDate: LocalDate): ZonedDateTime =
    zonedDateTime
        .withYear(localDate.year)
        .withMonth(localDate.monthValue)
        .withDayOfMonth(localDate.dayOfMonth)




fun Long.convertMilliToLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault()).toLocalDate()
}

fun LocalDate.convertToStringPattern(pattern: String = "dd/MM/yyyy"): String {
    return DateTimeFormatter.ofPattern(pattern).let { formatter ->
        this.format(formatter)
    }
}

@SuppressLint("DefaultLocale")
fun ZonedDateTime.formatedHourAndMin(): String {
    return "${String.format("%02d", this.hour)}:${String.format("%02d", this.minute)}"
}

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
fun TimePickerState.hourAndMin(): String {
    return "${String.format("%02d", this.hour)}:${String.format("%02d", this.minute)}"
}

