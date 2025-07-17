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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sample.common.model.IcasAppointment
import com.example.sample.ui.DateNavigator
import com.example.sample.ui.DatePickerView
import com.example.sample.ui.categoryColor
import com.example.sample.ui.convertMilliToLocalDate
import com.example.sample.ui.convertToStringPattern
import com.example.sample.ui.eventListData
import com.example.sample.ui.getEndTimeFloat
import com.example.sample.ui.getHrAndMinPair
import com.example.sample.ui.getMostRecentUpcomingDate
import com.example.sample.ui.getStartTimeFloat
import com.example.sample.ui.getTimeMinutesBlockHeight
import com.example.sample.ui.getYOffestFromStartHr
import com.example.sample.ui.groupOverlappingAppointments
import com.example.sample.ui.groupOverlappingEvents2
import com.example.sample.ui.setUpdatedDateInZonedDateTime
import com.example.sample.ui.startTimeTxt
import com.example.sample.ui.theme.SampleAppThemePreview
import com.example.sample.ui.toZonedLocalDate
import com.example.sample.ui.viewmodel.DayTimeSlotViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import java.time.ZonedDateTime


@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayTimeSlotScreen(
    modifier: Modifier = Modifier,
    viewModel: DayTimeSlotViewModel = koinViewModel()
) {

    // TODO: test mock http service
    LaunchedEffect(Unit) {
        viewModel.getAppointments()


        viewModel.appointmentData.collectLatest {
            println("collectLatest data = $it")
        }
    }
    val appointmentsList by viewModel.appointmentData.collectAsStateWithLifecycle()


    var showDatePicker by remember { mutableStateOf(false) }
    var dayTitleTxt by remember { mutableStateOf("Today") }
    var currentZonedDateTime by remember { mutableStateOf(ZonedDateTime.now()) }


    // todo: get most recent upcoming date in list
    if (appointmentsList.isNotEmpty()) {
        val mostRecentUpcomingZonedDateTime = getMostRecentUpcomingDate(
            appointmentsList.map { it.startDate },
            currentZonedDateTime
        )

        println("mostRecentUpcomingZonedDateTime = $mostRecentUpcomingZonedDateTime")

        currentZonedDateTime = mostRecentUpcomingZonedDateTime.takeIf {
            mostRecentUpcomingZonedDateTime != null
        }
    }

    println("currentZonedDateTime = $currentZonedDateTime")

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
                textTitle = updateDayTitleText(
                    currentZonedDateTime
                ),
                textOnClick = { showDatePicker = true },
                forwardOnClick = {
                    currentZonedDateTime = currentZonedDateTime.plusDays(1)
                },
                backOnClick = {
                    currentZonedDateTime = currentZonedDateTime.minusDays(1)
                }
            )

            println("value appointmentsList = $appointmentsList")

            val filteredAppointmentList = appointmentsList.filter {
                it.startDate.toZonedLocalDate() == currentZonedDateTime.toLocalDate()
            }

            println("value filteredAppointmentList = $filteredAppointmentList")

            //EventTimeLineView(eventList = filteredEventList)

            EventTimeLineViewForAppointment(appointmentList = filteredAppointmentList)
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


// todo: working for IcasAppoints
// todo: working good as expected with overlap & good spacing

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun EventTimeLineViewForAppointment(
    modifier: Modifier = Modifier,
    hrHeight: Dp = 80.dp,
    yOffsetCoEff: Float = 0.50f,
    appointmentList: List<IcasAppointment> = emptyList()
) {

    val vScrollState = rememberScrollState()
    val density = LocalDensity.current

    println("DayTimeSlotScreen appointmentList = $appointmentList, size = ${appointmentList.size}")

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

        // TODO: For Appointments
        val groupOverlapAppointment = groupOverlappingAppointments(appointmentList)

        Box(
            modifier = Modifier
                .padding(start = 65.dp)
                .fillMaxWidth()
        ) {
            groupOverlapAppointment.forEach {

                Row {

                    it.forEachIndexed { index, appointment ->

                        val startTimePair = getHrAndMinPair(appointment.startTimeTxt)

                        val startTimeFloat = appointment.getStartTimeFloat()
                        val endTimeFloat = appointment.getEndTimeFloat()

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
                                appointment.categoryColor.copy(alpha = 0.65f)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(vertical = 1.dp, horizontal = 10.dp)
                                    .padding(bottom = 2.dp),
                                text =  appointment.displayName, color = Color.White,
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

// todo: try 4 final
// todo: working good as expected with overlap & good spacing

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun EventTimeLineView(
    modifier: Modifier = Modifier,
    eventList: List<com.example.sample.ui.Event> = emptyList(),
    hrHeight: Dp = 80.dp,
    yOffsetCoEff: Float = 0.50f,
) {

    val vScrollState = rememberScrollState()
    val density = LocalDensity.current


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
        //println("WeekView_Overlap_2 = ${groupOverlappingEvents2(eventList)}")


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

@Preview
@Composable
fun DayTimeSlot_Preview() = SampleAppThemePreview {
    DayTimeSlotScreen()
}

