package com.example.sample.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.sample.ui.DateNavigator
import com.example.sample.ui.DatePickerView
import com.example.sample.ui.convertMilliToLocalDate
import com.example.sample.ui.convertToStringPattern
import com.example.sample.ui.eventListData
import com.example.sample.ui.setUpdatedDateInZonedDateTime
import com.example.sample.ui.theme.SampleAppThemePreview
import java.time.ZonedDateTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayTimeSlotScreen(modifier: Modifier = Modifier) {
    
    var showDatePicker by remember { mutableStateOf(false) }
    var dayTitleTxt by remember { mutableStateOf("Today") }
    
    var currentZonedDateTime by remember { mutableStateOf(ZonedDateTime.now()) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = currentZonedDateTime.toLocalDateTime()
            .toInstant(currentZonedDateTime.offset)
            .toEpochMilli()
    )


    Scaffold { innerPadding ->
        Column(modifier.padding(innerPadding)) {
            DateNavigator(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .padding(top = 10.dp),
                textTitle = dayTitleTxt,
                textOnClick = { showDatePicker = true },
                forwardOnClick = {
                    currentZonedDateTime = currentZonedDateTime.plusDays(1)
                    dayTitleTxt = updateDayTitleText(currentZonedDateTime)
                },
                backOnClick = {
                    currentZonedDateTime = currentZonedDateTime.minusDays(1)
                    dayTitleTxt = updateDayTitleText(currentZonedDateTime)
                }
            )


            val filteredEventList = eventListData.filter {
                it.startZonedDateTime.toLocalDate() == currentZonedDateTime.toLocalDate()
            }

            EventTimeLineView(eventList = filteredEventList)
        }
    }

    if (showDatePicker) {
        val dismiss = { showDatePicker = false }
        DatePickerView(
            onDismissRequest = dismiss,
            onDismissButton = {
                TextButton(onClick = dismiss) {
                    Text("Cancel")
                }
            },
            onConfirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.convertMilliToLocalDate()?.let {
                        currentZonedDateTime =
                            setUpdatedDateInZonedDateTime(
                                currentZonedDateTime,
                                it
                            )
                    }

                    dayTitleTxt = updateDayTitleText(currentZonedDateTime)

                    dismiss()
                }) {
                    Text("Select")
                }
            },
            datePickerState = datePickerState
        )
    }
}

private fun updateDayTitleText(currentZonedDateTime: ZonedDateTime): String {
    val tempZonedDateTime = ZonedDateTime.now()
    return if (currentZonedDateTime.toLocalDate().isEqual(tempZonedDateTime.toLocalDate())) {
         "Today"
    } else if (currentZonedDateTime.toLocalDate()
        .isEqual(tempZonedDateTime.toLocalDate().plusDays(1))) {
        "Tomorrow"
    } else if (currentZonedDateTime.toLocalDate()
        .isEqual(tempZonedDateTime.toLocalDate().minusDays(1))) {
        "Yesterday"
    } else {
        currentZonedDateTime.toLocalDate().convertToStringPattern()
    }
}


// todo: try 4 final
// todo: working good as expected with overlap & good spacing

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun EventTimeLineView(
    modifier: Modifier = Modifier,
    eventList: List<com.example.sample.ui.Event> = emptyList(),
    hrHeight: Dp = 80.dp,
    yOffsetCoEff: Float = 0.50f
) {

    val vScrollState = rememberScrollState()
    val density = LocalDensity.current

    LaunchedEffect(Unit) {
        println("DayTimeSlotScreen eventList = $eventList, size = ${eventList.size}")
    }

    Box(
        modifier = modifier
            .padding(start = 10.dp, end = 10.dp)
            .fillMaxSize()
            .verticalScroll(vScrollState)
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            // hour timeline view
            for (hour in 0 until 25) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(hrHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val hourText = if (hour == 24) {
                        "00:00"
                    } else {
                        "%02d:00".format(hour)
                    }
                    Text(
                        text = hourText,
                        modifier = Modifier
                            .width(56.dp)
                            .padding(end = 10.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .weight(1f)
                    )
                }
            }
        }

        // for overlap
        val groupOverlapEvents = groupOverlappingEvents2(eventList)
//        println("WeekView_Overlap_2 = ${groupOverlappingEvents2(eventList)}")


        Box(
            modifier = Modifier
                .padding(start = 65.dp)
                .fillMaxWidth()
        ) {
            groupOverlapEvents.forEach {

                Row {

                    it.forEachIndexed { index, event ->

                        val startTimePair = getHrAndMinPair(event.startTimeTxt)

                        val startTimeFloat = event.getStartTimeFloat()
                        val endTimeFloat = event.getEndTimeFloat()

                        val yOffsetValue = getYOffestFromStartHr(startTimePair) + yOffsetCoEff
                        val yToPx = with(density) { (yOffsetValue) * hrHeight.toPx() }

                        val heightToPx =
                            getTimeMinutesBlockHeight(startTimeFloat, endTimeFloat, hrHeight)

                        Card (
                            modifier = Modifier
                                .weight(1f)
                                .absoluteOffset {
                                    IntOffset(x = 0, y = yToPx.toInt())
                                }
                                .padding(end = 5.dp)
                                .height(heightToPx),
                            colors = CardDefaults.cardColors(
                                event.categoryColor.copy(alpha = 0.65f)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(vertical = 1.dp, horizontal = 10.dp)
                                    .padding(bottom = 2.dp),
                                text =  event.title, color = Color.White,
                                fontWeight = FontWeight.Bold,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

    }
}



private fun groupOverlappingEvents(events: List<com.example.sample.ui.Event>): List<List<com.example.sample.ui.Event>> {
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

private fun groupOverlappingEvents2(events: List<com.example.sample.ui.Event>): List<List<com.example.sample.ui.Event>> {
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

@Preview()
@Composable
private fun DayTimeSlot_Preview() = SampleAppThemePreview {
    DayTimeSlotScreen()
}

