package com.example.sample.ui

import androidx.compose.ui.graphics.Color
import com.example.sample.ui.screens.getHrAndMinPair
import com.example.sample.ui.screens.getTimeAsFloat
import com.example.sample.ui.theme.CategoryColor
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.minutes


data class Event(
    var title: String,
    var startTimeTxt: String,
    var endTimeTxt: String,
    var categoryColor: Color = CategoryColor.blue_crayola,
    var description: String = "",
    var startZonedDateTime: ZonedDateTime = ZonedDateTime.now(),
    var endZonedDateTime: ZonedDateTime = startZonedDateTime.plusMinutes(30.minutes.inWholeMinutes)
) {

    val getStartTimeFloat = {
        val pair = getHrAndMinPair(startTimeTxt)
        getTimeAsFloat(pair)
    }

    val getEndTimeFloat = {
        val pair = getHrAndMinPair(endTimeTxt)
        getTimeAsFloat(pair)
    }
}


var eventListData = mutableListOf(
//    Event("Event 1", 1.0f, 2.00f, Color.Green),
//    Event("Event 3", 3.50f, 3.50f + (50f * 0.00633f)),
//    Event("Event 5", 5.50f, 5.50f + (30f * 0.00633f)),
//    Event("Event 7", 7.50f, 7.50f + (60f * 0.00633f)),
//    Event("Event 10", 10.50f, 10.50f + (90f * 0.00633f)),
//    Event("Event 12", 13.50f - (60f * 0.00633f), 13.50f + (60f * 0.00633f)),
//    Event("Event 16", 16.50f, 16.50f + (60f * 0.00633f)),

//    Event("Event 1", "01:00", "02:00"),
    Event("Event 3", "03:00", "04:00"),

    Event("Event 5:30", "05:30", "06:30"),
    Event("Event 5:30", "05:30", "06:30", CategoryColor.brown_sandy),
    Event("Event 5:30", "05:30", "06:30", CategoryColor.purple_veronica),
    //Event("Event 5:30", "05:30", "06:30", Color(0xFF8744F9)),
    //Event("Event 5:30", "05:30", "06:30", Color(0xFF8744F9)),

    Event("Event 5:00", "05:00", "06:00"),
    Event("Event 8 to 10", "08:00", "10:00"),
    Event("Event 8 to 10 2", "08:00", "10:00", CategoryColor.green_light),
    Event("Event 10:45", "10:45", "11:00"),
    Event("Event 14 to 15", "14:00", "15:00"),
    Event("Event 20:30 to 21:00", "20:30", "21:00"),
    Event("Event 20 to 21:30", "20:00", "21:30"),
    Event("Event 23-24", "23:20", "24:00"),
    Event("Event 23", "23:00", "23:30"),

    )