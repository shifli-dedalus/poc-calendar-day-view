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

//    val eventList = remember { eventListData }

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
        // val groupOverlapEvents = groupOverlappingEvents(eventList)
        //println("WeekView_Overlap = $groupOverlapEvents")
        //println("WeekView_Overlap.size = ${groupOverlapEvents.size}")

        println("WeekView_Overlap_2.size = ${groupOverlappingEvents2(eventList).size}")
        println("WeekView_Overlap_2 = ${groupOverlappingEvents2(eventList)}")


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












// todo: working ui
//@SuppressLint("UnusedBoxWithConstraintsScope")
//@Composable
//fun DayTimeSlotScreen(
//    modifier: Modifier = Modifier,
//    hrHeight: Dp = 80.dp,
//    minEventWidth: Dp = 125.dp,
//    xToPxOffsetDp: Dp = 45.dp
//) {
//    val vScrollState = rememberScrollState()
//    val hScrollState = rememberScrollState()
//    val density = LocalDensity.current
//
//    val nestedScroll = rememberNestedScrollInteropConnection()
//
//    Box(
//        modifier = modifier
//            .padding(start = 10.dp, end = 10.dp)
//            .fillMaxSize()
//            .verticalScroll(vScrollState)
//            .fillMaxHeight()
//    ) {
//        Column(
//            modifier = Modifier.fillMaxSize(),
//        ) {
//            for (hour in 0 until 25) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(hrHeight),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    val hourText = if (hour == 24) {
//                        "00:00"
//                    } else {
//                        "%02d:00".format(hour)
//                    }
//                    Text(
//                        text = hourText,
//                        modifier = Modifier
//                            .width(56.dp)
//                            .padding(end = 10.dp)
//                    )
//                    HorizontalDivider(
//                        modifier = Modifier
//                            .weight(1f)
//                    )
//                }
//            }
//        }
//
//        // for overlap
//        val groupOverlapEvents = groupOverlappingEvents2(eventList)
//        //        val groupOverlapEvents = groupOverlappingEvents(eventList)
//        println("WeekView_Overlap = $groupOverlapEvents")
//        println("WeekView_Overlap.size = ${groupOverlapEvents.size}")
//
//        println("WeekView_Overlap_2.size = ${groupOverlappingEvents2(eventList).size}")
//        println("WeekView_Overlap_2 = ${groupOverlappingEvents2(eventList)}")
//
//        BoxWithConstraints(
//            modifier = Modifier
//                .fillMaxSize()
//        ) {
//            // todo: try 3 working overlapping except horizontal scroll
//            groupOverlapEvents.forEach {
//
//                val overlapEventsCount = it.size
//
//                it.forEachIndexed { index, event ->
//
//                    val startTimePair = getHrAndMinPair(event.startTime)
//                    val endTimePair = getHrAndMinPair(event.endTime)
//
//                    val startTimeFloat = event.getStartTimeFloat()
//                    val endTimeFloat = event.getEndTimeFloat()
//
//                    val yOffsetValue = getYOffestFromStartHr(startTimePair) + 0.50f
//                    val yToPx = with(density) { (yOffsetValue) * hrHeight.toPx() }
//
//                    val heightToPx =
//                        getTimeMinutesBlockHeight(startTimeFloat, endTimeFloat, hrHeight)
//
//                    //  val widthFraction = 1f / overlapEventsCount.toFloat()
//                    val widthFraction = if (overlapEventsCount > 1) 0.5f else 1f
//
//                    var xToPx = with(density) { index * minEventWidth.toPx() }
////                    var xToPx = with(density) { index * 330.0f }
//
//                    val widthDp = with(density) { widthFraction }
//
//                    Box(
//                        modifier = Modifier
////                            .width(constraints.maxWidth.dp / overlapEventsCount)
////                            .fillMaxWidth((1f/3))
//                            .fillMaxWidth(widthDp)
//                            .absoluteOffset {
//                                // IntOffset(x = 56.dp.roundToPx(), y = yToPx.toInt())
//                                if (index == 0) {
//                                    IntOffset(
//                                        x = xToPxOffsetDp.roundToPx() + xToPx.toInt(),
//                                        y = yToPx.toInt()
//                                    )
//                                } else {
//                                    IntOffset(
//                                        x = (xToPxOffsetDp - 5.dp).roundToPx() + xToPx.toInt(),
//                                        y = yToPx.toInt()
//                                    )
////                                    IntOffset(
////                                        x = xToPx.toInt(),
////                                        y = yToPx.toInt()
////                                    )
//                                }
//                            }
//                            .padding(start = 20.dp, end = 60.dp)
////                            .padding(start = 20.dp, end = 20.dp)
//                            .height(heightToPx)
//                            .background(event.color.copy(alpha = 0.7f))
//                    ) {
//                        Text(event.title, color = Color.White)
//                    }
//                }
//            }
//
//
//            // todo: try 2 except overlapping
//
//            //            timeSlices.forEach { (sliceStart, sliceEnd) ->
//            //                val activeEvents = eventList.filter {
//            //                    it.getStartTimeFloat() < sliceEnd && it.getEndTimeFloat() > sliceStart
//            //                }
//            //
//            //                activeEvents.forEachIndexed { index, event ->
//            //
//            //                    val startTimePair = getHrAndMinPair(event.startTime)
//            //                    val endTimePair = getHrAndMinPair(event.endTime)
//            //
//            //                    val startTimeFloat = event.getStartTimeFloat()
//            //                    val endTimeFloat = event.getEndTimeFloat()
//            //
//            //                    val yOffsetValue = getYOffestFromStartHr(startTimePair) + 0.50f
//            //                    val yToPx = with(density){ (yOffsetValue) * hrHeight.toPx() }
//            //
//            //                    val heightToPx = getTimeMinutesBlockHeight(startTimeFloat, endTimeFloat, hrHeight)
//            //
//            //                    val widthFraction = 1f / activeEvents.size.toFloat()
//            //                    val xToPx = index * widthFraction * totalConstraintsWidthPx
//            //
//            //                    val widthDp = with(density) { widthFraction }
//            //
//            //                    Box(
//            //                        modifier = Modifier
//            //                            .fillMaxWidth(widthDp)
//            //                            .absoluteOffset {
//            ////                                IntOffset(x = 56.dp.roundToPx(), y = yToPx.toInt())
//            //                                IntOffset(x = 56.dp.roundToPx() + xToPx.toInt(), y = yToPx.toInt())
//            //                            }
//            //                            .padding(start = 20.dp, end = 60.dp)
//            //                            .height(heightToPx)
//            //                            .background(event.color.copy(alpha = 0.7f))
//            //                    ) {
//            //                        Text(event.title, color = Color.White)
//            //                    }
//            //                }
//            //            }
//
//
//            // TODO: working code remove /**/
//            /*eventList.forEach { event ->
//                // old calculation
////                val yToPx = with(density){ ((event.startTime + 0.50f) * hrHeight.toPx()) }
////                val heightToPx = with(density){ ((event.endTime - event.startTime) * hrHeight.toPx()) }
//
////                val minHeightDp = hrHeight / 60f
////                val heightToPx = ((event.endTime * hrHeight.value) - (event.startTime * hrHeight.value)).dp
////                val heightToPx = (120 * minHeightDp.value).dp
//
//                // new calculation
//                val startTimePair = getHrAndMinPair(event.startTime)
//                val endTimePair = getHrAndMinPair(event.endTime)
//
//                val startTimeFloat = getTimeAsFloat(startTimePair)
//                val endTimeFloat = getTimeAsFloat(endTimePair)
//
//                println("TEST_LOG = startTimeFloat - $startTimeFloat, endTimeFloat - $endTimeFloat")
////
//                val yOffsetValue = getYOffestFromStartHr(startTimePair) + 0.50f
//                val yToPx = with(density){ (yOffsetValue) * hrHeight.toPx() }
////                val yToPx = with(density){ ( (2.0 + (30f/60f)) + 0.50f) * hrHeight.toPx() }
////
//                val minHeightDp = hrHeight / 60f
////                val heightToPx = (minHeightDp.value * (endTimeFloat - startTimeFloat)).dp
////                val heightToPx = (minHeightDp.value * 120).dp
//                val heightToPx = getTimeMinutesBlockHeight(startTimeFloat, endTimeFloat, hrHeight)
//
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .absoluteOffset {
//                            IntOffset(x = 56.dp.roundToPx(), y = yToPx.toInt())
//                        }
//                        .padding(start = 20.dp, end = 60.dp)
//                        .height(heightToPx)
//                        .background(event.color.copy(alpha = 0.7f))
//                ) {
//                    Text(event.title, color = Color.White)
//                }
//            }*/
//        }
//
//    }
//}

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


fun findOverlapEvents(events: List<com.example.sample.ui.Event>): Map<com.example.sample.ui.Event, Int> {
    val sortedEventsByStartTime = events.sortedBy { it.getStartTimeFloat() }
    val columnsMap = mutableMapOf<com.example.sample.ui.Event, Int>()
    val activeEvents = mutableListOf<com.example.sample.ui.Event>()

    sortedEventsByStartTime.forEach { event ->
        val usedColumn = activeEvents.mapNotNull { columnsMap[it] }.toSet()

        var col = 0
        while (usedColumn.contains(col)) col ++

        columnsMap[event] = col
        activeEvents.add(event)
    }

    return columnsMap
}














//@Composable
//fun DayTimeSlotScreen(modifier: Modifier = Modifier) {
//    val listState = rememberLazyListState()
//    Box(
//        modifier = modifier.fillMaxSize()
//    ) {
//        LazyColumn(
//            modifier = Modifier.fillMaxSize(),
//            state = listState
//        ) {
//            items(24) { hour ->
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(80.dp)
//                ) {
//                    Text(
//                        "%02d:00".format(hour),
//                        modifier = Modifier.width(56.dp)
//                    )
//                    HorizontalDivider(
//                        modifier = Modifier.weight(1f)
//                    )
//                }
//            }
//        }
//
//        EventOverlay(
//            listState = listState,
//            eventList = eventList
//        )
//    }
//}
//
//@Composable
//fun Int.toPx(): Int {
//    return with(LocalDensity.current) { this@toPx.dp.roundToPx() }
//}
//
//@Composable
//fun Float.toDp(): Dp {
//    return with(LocalDensity.current) { this@toDp.dp }
//}
//
//
//@Composable
//fun EventOverlay(
//    modifier: Modifier = Modifier,
//    eventList: List<Event>,
//    listState: LazyListState,
//    hourHeight: Int = 80
//) {
//    Box(
//        modifier = Modifier
//            .padding(start = 65.dp, end = 10.dp)
//            .fillMaxSize()
////            .pointerInput(Unit) {}
//    ) {
//        val firstVisibleIndex = listState.firstVisibleItemIndex
//        val scrollOffsetPx = listState.firstVisibleItemScrollOffset
//
//        println("EventOverlay: firstVisibleIndex = $firstVisibleIndex, scrollOffsetPx = $scrollOffsetPx")
//
////        val firstVisibleIndex = remember { derivedStateOf { listState.firstVisibleItemIndex } }
////        val scrollOffsetPx = remember { derivedStateOf { listState.firstVisibleItemScrollOffset } }
//
//        eventList.forEach { event ->
//            val yOffsetPx = ((event.startTime - firstVisibleIndex) * hourHeight.toPx() - scrollOffsetPx)
//            val eventHeightPx = (event.endTime - event.startTime) * hourHeight.toPx()
//
//            println("EventOverlay: yOffsetPx => event.startTime - ${event.startTime}, " +
//                    "firstVisibleIndex = $firstVisibleIndex, " +
//                    "hourHeight.toPx() = ${hourHeight.toPx()}, scrollOffsetPx - $scrollOffsetPx")
//
//            println("EventOverlay: eventHeightPx => event.endTime - ${event.endTime},")
//
//            println("EventOverlay: yOffsetPx = $yOffsetPx, eventHeightPx = $eventHeightPx")
//
//            Box (
//                modifier = Modifier
//                    .absoluteOffset(y = yOffsetPx.toDp())
//                    .fillMaxWidth()
//                    .height(eventHeightPx.toDp())
//                    .background(event.color.copy(alpha = 0.7f))
//            ) {
//                Text(
//                    text = event.title,
//                    modifier = Modifier.padding(8.dp),
//                    color = Color.White
//                )
//            }
//
//        }
//    }
//}





//@Composable
//fun DayTimeSlotScreen(modifier: Modifier = Modifier) {
//    val eventList = listOf(
//        Event("Test 5AM", 5, 7, Color(0xFF783FAB)),
//        Event("Test 1AM", 1, 2, Color(0xFFA06FCA)),
//        //Event("Test 9:30AM", 9.30f, 10.60f, Color(0xFFA06FCA)),
////        Event("Test 9:30AM", 11f, 12f, Color(0xFFA06FCA)),
//        Event("Test 17 AM", 17, 18, Color(0xFFA06FCA)),
//        Event("Test 17 AM", 23, 24, Color(0xFFA06FCA)),
//    )
//
//    val listState = rememberLazyListState()
//
//    Box {
//        LazyColumn (
//            modifier.fillMaxWidth().fillMaxHeight()
//                .padding(top = 30.dp, bottom = 30.dp),
//            state = listState
//        ) {
//            items(24) { index ->
//                val hour = index
//
//                val eventsForThisHour = eventList.filter { it.startTime.toInt() == hour }
//
//                Row(
//                    modifier = Modifier.fillMaxWidth()
//                        .padding(start = 10.dp)
//                        .height(80.dp),
//                ) {
//                    Text(
//                        "%02d:00".format(index),
//                        modifier = Modifier.width(56.dp),
//                        style = MaterialTheme.typography.bodySmall
//                    )
//
//                    HorizontalDivider(
//                        modifier = Modifier.fillMaxWidth()
//                    )
//
//                    Box (
//                        modifier = Modifier.fillMaxSize()
//                    ){
//                        val scrollOffset = remember { derivedStateOf { listState.firstVisibleItemScrollOffset } }
//                        val firstVisibleIndex = remember { derivedStateOf { listState.firstVisibleItemIndex } }
//
//                        eventList.forEach {
//                            val yOffset = ((it.startTime - firstVisibleIndex.value) * 80 - scrollOffset.value)
//                                .coerceAtLeast(0)
//
//                            val height = (it.endTime - it.startTime) * 80
//
//                            Box(
//                                modifier = Modifier
//                                    .offset(y = yOffset.dp)
//                                    .fillMaxWidth()
//                                    .height(height.dp)
//                                    .background(it.color)
//                            ) {
//                                Text(
//                                    it.title,
//                                    modifier = Modifier.padding(8.dp),
//                                    color = Color.White
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}


//@Composable
//fun EventSlot(event: Event) {
//    val hourHeight = 80
//    val statOffset = (event.startTime) + 6
//    val durationHeight = (event.endTime - event.startTime) * hourHeight
//
//    println(" Time_event = ${event.startTime}, offset - $statOffset")
//
//    Box(
//        modifier = Modifier
////            .offset(y = statOffset.dp)
//            .padding(start = 20.dp, end = 10.dp)
//            .fillMaxHeight()
//            .fillMaxWidth(fraction = 0.5f)
//            .background(color = event.color)
//    ) {
//        Text(
//            event.title,
//            modifier = Modifier.padding(8.dp),
//            color = Color.White
//        )
//    }
//}

@Preview(name= "320 Dpi", device = "spec:dpi=320,width=411dp,height=891dp")
//@Preview(name= "420 Dpi",device = "spec:width=412dp,height=915dp,dpi=420")
//@Preview(name= "640 Dpi",device = "spec:dpi=640,width=411dp,height=891dp")
//@Preview()
@Composable
private fun DayTimeSlot_Preview() = SampleAppThemePreview {
    DayTimeSlotScreen()
}

