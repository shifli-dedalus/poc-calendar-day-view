package com.example.sample.ui

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.sample.common.model.IcasAppointment
import com.example.sample.ui.theme.CategoryColor
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.minutes


// TODO: class variable/function extensions
val IcasAppointment.startTimeTxt: String
    get() = this.startDate.formatedHourAndMin()

val IcasAppointment.endTimeTxt: String
    get() = this.endDate?.formatedHourAndMin() ?:
    this.startDate.plusMinutes(30.minutes.inWholeMinutes).formatedHourAndMin()

fun IcasAppointment.getStartTimeFloat(): Float  {
    val pair = getHrAndMinPair(this.startTimeTxt)
    return getTimeAsFloat(pair)
}

fun IcasAppointment.getEndTimeFloat(): Float {
    val pair = getHrAndMinPair(this.endTimeTxt)
    return getTimeAsFloat(pair)
}

val IcasAppointment.categoryColor: Color
    get() = CategoryColor.blue_crayola



fun getMostRecentUpcomingDate(
    zonedDateTimeList: List<ZonedDateTime>,
    currentZonedDateTime: ZonedDateTime
): ZonedDateTime? {
    return zonedDateTimeList.firstOrNull { zonedDateTime ->
        zonedDateTime.isEqual(currentZonedDateTime)
                || zonedDateTime.isAfter(currentZonedDateTime)
    }
}

fun groupOverlappingAppointments(appointments: List<IcasAppointment>): List<List<IcasAppointment>> {

    if (appointments.isEmpty()) return emptyList()

    val resultGroupList = mutableListOf<MutableList<IcasAppointment>>()
    var currentGroup = mutableListOf<IcasAppointment>()

    val sortedAppointments = appointments.sortedBy { it.startDate }
    currentGroup.add(sortedAppointments.first())

    for (index in 1 until sortedAppointments.size){
        val currentAppointment = sortedAppointments[index]
        val lastAppointment = currentGroup.last()

        val isOverlapping = currentAppointment.startDate <
                lastAppointment.endDate

        if (isOverlapping) {
            currentGroup.add(currentAppointment)
        } else {
            if (currentGroup.isNotEmpty())
                resultGroupList.add(currentGroup)

            currentGroup = mutableListOf(currentAppointment)
        }
    }

    if (currentGroup.isNotEmpty())
        resultGroupList.add(currentGroup)

    return resultGroupList
}


fun groupOverlappingEvents(events: List<com.example.sample.ui.Event>): List<List<com.example.sample.ui.Event>> {
    val sortedEvents = events.sortedBy { it.getStartTimeFloat() }
    val groupEvents = mutableListOf<MutableList<com.example.sample.ui.Event>>()

    for (sortedEvent in sortedEvents) {
        var addToExistingGroup = false

        for (groupEvent in groupEvents) {
            val isOverlappingEvent = groupEvent.any { it.getStartTimeFloat() < sortedEvent.getEndTimeFloat()
                    && it.getEndTimeFloat() > sortedEvent.getStartTimeFloat() }

            if (isOverlappingEvent) {
                groupEvent.add(sortedEvent)
                addToExistingGroup = true
                break
            }

        }

        if(!addToExistingGroup){
            groupEvents.add(mutableListOf(sortedEvent))
        }
    }

    return groupEvents
}

fun groupOverlappingEvents2(events: List<com.example.sample.ui.Event>): List<List<com.example.sample.ui.Event>> {
    if (events.isEmpty()) return emptyList()

    val sortedEvents = events.sortedBy { it.getStartTimeFloat() }
    val resultGroupEvents = mutableListOf<MutableList<com.example.sample.ui.Event>>()

    var currentGroup = mutableListOf<com.example.sample.ui.Event>()
    currentGroup.add(sortedEvents.first())

    for (index in 1 until sortedEvents.size) {
        val currentEvent = sortedEvents[index]
        val lastEventInGroup = currentGroup.last()

        val isOverlapping = currentEvent.getStartTimeFloat() < lastEventInGroup.getEndTimeFloat()

        if(isOverlapping) {
            currentGroup.add(currentEvent)
        } else {
            if (currentGroup.isNotEmpty())
                resultGroupEvents.add(currentGroup)

            currentGroup = mutableListOf(currentEvent)
        }
    }

    if (currentGroup.isNotEmpty())
        resultGroupEvents.add(currentGroup)

    return resultGroupEvents
}



fun getHrAndMinPair(time: String): Pair<Int, Int> {
    check(time.contains(":") && time.split(":").count() == 2) {
        "Invalid TimeFormat. Required HH:mm in 24hr format"
    }

    val hour = time.split(":")[0].toIntOrNull()
    val mins = time.split(":")[1].toIntOrNull()

    checkNotNull(hour)
    checkNotNull(mins)

    return Pair(hour, mins)
}

fun getTimeAsFloat(hourMinsPair: Pair<Int, Int>): Float {
    val hour = hourMinsPair.first
    val mins = hourMinsPair.second
    return (hour * 60 + mins).toFloat()
}

fun getTimeMinutesBlockHeight(startTime: Float, endTime: Float, hrHeight: Dp): Dp {
    val diffMin = (endTime - startTime)
    val minsHeightDp = (hrHeight / 60f)
    return (diffMin * minsHeightDp.value).dp
}

fun getYOffestFromStartHr(startTimePair: Pair<Int, Int>): Float {
    val hour = startTimePair.first
    val mins = startTimePair.second
    return (hour.toFloat() + (mins.toFloat() / 60f))
}

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

fun ZonedDateTime.toZonedLocalDate(zoneId: ZoneId = ZoneId.systemDefault()): LocalDate{
    return withZoneSameInstant(zoneId).toLocalDate()
}

@SuppressLint("DefaultLocale")
fun ZonedDateTime.formatedHourAndMin(
    zoneId: ZoneId = ZoneId.systemDefault(),
    pattern: String = "HH:mm"
): String {
    return withZoneSameInstant(zoneId)
        .format(DateTimeFormatter.ofPattern(pattern))
}

//@SuppressLint("DefaultLocale")
//fun ZonedDateTime.formatedHourAndMin(): String {
//    return "${String.format("%02d", this.hour)}:${String.format("%02d", this.minute)}"
//}

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
fun TimePickerState.hourAndMin(): String {
    return "${String.format("%02d", this.hour)}:${String.format("%02d", this.minute)}"
}

