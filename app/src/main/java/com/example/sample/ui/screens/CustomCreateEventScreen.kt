package com.example.sample.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sample.ui.CategoryChooserDropMenu
import com.example.sample.ui.DatePickerView
import com.example.sample.ui.ElevatedIconButton
import com.example.sample.ui.Event
import com.example.sample.ui.LocalNavController
import com.example.sample.ui.TimePickerDialog
import com.example.sample.ui.convertMilliToLocalDate
import com.example.sample.ui.convertToStringPattern
import com.example.sample.ui.eventListData
import com.example.sample.ui.formatedHourAndMin
import com.example.sample.ui.hourAndMin
import com.example.sample.ui.setUpdateTimeInZonedDateTime
import com.example.sample.ui.setUpdatedDateInZonedDateTime
import com.example.sample.ui.theme.CategoryColor
import com.example.sample.ui.theme.SampleAppThemePreview
import com.example.sample.ui.theme.Typography
import java.time.LocalDate
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.minutes


@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomCreateEventScreen(modifier: Modifier = Modifier) {

    val navHostController = LocalNavController.current

    var titleText by remember { mutableStateOf("") }
    var descriptionText by remember { mutableStateOf("") }

    var showStartTimeModal by remember { mutableStateOf(false) }
    var showStartDateModal by remember { mutableStateOf(false) }

    var showEndTimeModal by remember { mutableStateOf(false) }
    var showEndDateModal by remember { mutableStateOf(false) }

    var dialogDateSelectedText by remember { mutableStateOf("") }

    var selectedStartDateText by remember { mutableStateOf("") }
    var selectedEndDateText by remember { mutableStateOf("") }

    var selectedStartTimeText by remember { mutableStateOf("") }
    var selectedEndTimeText by remember { mutableStateOf("") }

    // time picker state
    var startZonedDateTime by remember { mutableStateOf(ZonedDateTime.now()) }
    var endZonedDateTime by remember {
        mutableStateOf(startZonedDateTime.plusMinutes(30.minutes.inWholeMinutes))
    }

    LaunchedEffect(Unit) {
        println("LaunchEffect_startZonedDateTime = $startZonedDateTime")
        println("LaunchEffect_endZonedDateTime = $endZonedDateTime")
    }

    val startTimePickerState = rememberTimePickerState(
        initialHour = startZonedDateTime.hour,
        initialMinute = startZonedDateTime.minute,
        is24Hour = true,
    )

    val endTimePickerState = rememberTimePickerState(
        initialHour = endZonedDateTime.hour,
        initialMinute = endZonedDateTime.minute,
        is24Hour = true,
    )


    // date picker state
    val selectableDatesValidator = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return utcTimeMillis >= System.currentTimeMillis()
        }

        override fun isSelectableYear(year: Int): Boolean {
            return year >= LocalDate.now().year
        }
    }

    val startDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = startZonedDateTime.toLocalDateTime()
            .toInstant(startZonedDateTime.offset)
            .toEpochMilli(),
        selectableDates = selectableDatesValidator
    )

    val endDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = endZonedDateTime.toLocalDateTime()
            .toInstant(endZonedDateTime.offset)
            .toEpochMilli(),
        selectableDates = selectableDatesValidator
    )


    // category color
    var selectedCategoryColor by remember { mutableStateOf(CategoryColor.blue_crayola) }
    var showCategoryMenuOption by remember { mutableStateOf(false) }


    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "New Event",
                        style = Typography.titleMedium
                    )
                },
                navigationIcon = {
                    ElevatedIconButton(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        onClick = navHostController::popBackStack,
                        shadowElevation = 2.dp
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(start = 20.dp, end = 20.dp, top = 10.dp)
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                value = titleText,
                onValueChange = {
                    titleText = it
                },
                placeholder = {
                    Text("New Event")
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                value = descriptionText,
                onValueChange = {
                    descriptionText = it
                },
                placeholder = {
                    Text("Description (optional)")
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )

            Row(
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box{
                    Text(
                        modifier = Modifier
                            .padding(12.dp),
                        text = "Category",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }

                Box{
                    Card(
                        modifier = Modifier
                            .padding(12.dp)
                            .size(25.dp)
                            .clickable { showCategoryMenuOption = true },
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(selectedCategoryColor),
                        elevation = CardDefaults.elevatedCardElevation(2.dp)
                    ) {}

                    val onDismiss = { showCategoryMenuOption = !showCategoryMenuOption }

                    CategoryChooserDropMenu(
                        expand = showCategoryMenuOption,
                        selectedColor = selectedCategoryColor,
                        onSelected = {
                            selectedCategoryColor = it
                            onDismiss()
                        },
                        onDismiss = onDismiss
                    )
                }
            }

            HorizontalDivider(Modifier.padding(vertical = 5.dp))

            // start time
            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .clickable { showStartDateModal = true },
                ) {
                    Text(
                        modifier = Modifier
                            .padding(12.dp),
                        text = selectedStartDateText.let {
                            it.ifEmpty {
                                startZonedDateTime.toLocalDate()
                                    .convertToStringPattern("EEE, dd MMM, uuuu")
                            }
                        },
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }

                //Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .clickable { showStartTimeModal = true },
                ) {
                    Text(
                        modifier = Modifier
                            .padding(12.dp),
                        text = selectedStartTimeText.let {
                            it.ifEmpty {
                                selectedStartTimeText = startZonedDateTime.formatedHourAndMin()
                            }
                            it
                        },
                        fontWeight = FontWeight.Medium
                    )
                }

            }

            // end time
            Row(
                modifier = Modifier
                    .padding(top = 5.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val endZoneDateTime = endZonedDateTime.plusMinutes(30.minutes.inWholeMinutes)

                Box(
                    modifier = Modifier
                        .clickable { showEndDateModal = true },
                ) {
                    Text(
                        modifier = Modifier.padding(12.dp),
                        text = selectedEndDateText.let {
                            it.ifEmpty {
                                endZoneDateTime.toLocalDate()
                                    .convertToStringPattern("EEE, dd MMM, uuuu")
                            }
                        },
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }

                //Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .clickable { showEndTimeModal = true },
                ) {
                    Text(
                        modifier = Modifier.padding(12.dp),
                        text = selectedEndTimeText.let {
                            it.ifEmpty {
                                selectedEndTimeText = endZoneDateTime.formatedHourAndMin()
                            }
                            it
                        },
                        fontWeight = FontWeight.Medium
                    )
                }

            }

            // confirm button
            Row(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    modifier = Modifier
                        .padding(end = 10.dp),
                    colors = ButtonDefaults.buttonColors(Color.LightGray),
                    onClick = { navHostController.popBackStack() }
                ) {
                    Text(
                        text = "Cancel",
                        color = Color.Black
                    )
                }

                Button(
                    modifier = Modifier,
                    onClick = {
                        if (titleText.isNotEmpty() && selectedStartTimeText.isNotEmpty()
                            && selectedEndTimeText.isNotEmpty()) {

                            addNewEvent(
                                Event(
                                    title = titleText,
                                    description = descriptionText,
                                    startZonedDateTime = startZonedDateTime,
                                    endZonedDateTime = endZonedDateTime,
                                    startTimeTxt = selectedStartTimeText,
                                    endTimeTxt = selectedEndTimeText,
                                    categoryColor = selectedCategoryColor
                                )
                            )
                        }

                        navHostController.popBackStack()
                    }
                ) {
                    Text("Create")
                }
            }
        }
    }


    // handle date picker
    if (showStartDateModal || showEndDateModal) {
        val dismissDatePicker = {
            if (showStartDateModal) { showStartDateModal = false }
            if (showEndDateModal) { showEndDateModal = false }
        }

        DatePickerView(
            onDismissRequest = dismissDatePicker,
            onConfirmButton = {

                TextButton(onClick = {

                    if (showStartDateModal) {
                        startDatePickerState.selectedDateMillis?.let {
                            val localDate = it.convertMilliToLocalDate()

                            dialogDateSelectedText = localDate.convertToStringPattern()
                            selectedStartDateText = localDate
                                .convertToStringPattern("EEE, dd MMM, uuuu")

                            println("startZonedDateTime_localDate = $localDate")

                            startZonedDateTime =
                                setUpdatedDateInZonedDateTime(
                                    startZonedDateTime,
                                    localDate
                                )

                            if (startZonedDateTime.isAfter(endZonedDateTime)) {
                                endZonedDateTime =
                                    startZonedDateTime.plusMinutes(30.minutes.inWholeMinutes)

                                selectedEndDateText = endZonedDateTime.toLocalDate()
                                    .convertToStringPattern("EEE, dd MMM, uuuu")
                            }

                        }
                    }

                    if (showEndDateModal) {
                        endDatePickerState.selectedDateMillis?.let {
                            val localDate = it.convertMilliToLocalDate()

                            dialogDateSelectedText = localDate.convertToStringPattern()
                            selectedEndDateText = localDate
                                .convertToStringPattern("EEE, dd MMM, uuuu")

                            println("endZonedDateTime_localDate = $localDate")

                            endZonedDateTime =
                                setUpdatedDateInZonedDateTime(
                                    endZonedDateTime,
                                    localDate
                                )
                        }
                    }

                    println("CustomCreatEvent selectedStartDateText = $selectedStartDateText, selectedEndDateText = $selectedEndDateText")

                    dismissDatePicker()
                }) {
                    Text("Select")
                }
            },
            onDismissButton = {
                TextButton(onClick = dismissDatePicker) {
                    Text("Cancel")
                }
            },
            datePickerState = if (showStartDateModal)
                startDatePickerState else endDatePickerState
        )
    }


    // handle time picker
    if (showStartTimeModal || showEndTimeModal) {

        val onDismiss = {
            if (showStartTimeModal) { showStartTimeModal = false }
            if (showEndTimeModal) { showEndTimeModal = false }
        }

        val showDatePicker = {
            if (showStartTimeModal) { showStartDateModal = true }
            if (showEndTimeModal) { showEndDateModal = true }
        }

        TimePickerDialog(
            timePickerState = if (showStartTimeModal)
                startTimePickerState else endTimePickerState,
            onDismiss = onDismiss,
            onSet = {
                if (showStartTimeModal) {
                    selectedStartTimeText = startTimePickerState.hourAndMin()

                    startZonedDateTime =
                        setUpdateTimeInZonedDateTime(
                            startZonedDateTime, startTimePickerState.hour,
                            startTimePickerState.minute
                        )

                    if (startZonedDateTime.isBefore(endZonedDateTime)) {
                        endZonedDateTime = startZonedDateTime.plusMinutes(30.minutes.inWholeMinutes)
                        selectedEndTimeText = endZonedDateTime.formatedHourAndMin()
                    }
                }

                if (showEndTimeModal) {
                    selectedEndTimeText = endTimePickerState.hourAndMin()

                    endZonedDateTime =
                        setUpdateTimeInZonedDateTime(
                            endZonedDateTime, endTimePickerState.hour,
                            endTimePickerState.minute
                        )
                }
                onDismiss()
            },
            zonedDateTime = if (showStartTimeModal)
                startZonedDateTime else endZonedDateTime,
            dateSelectedText = dialogDateSelectedText,
            textOnValueChange = {
                dialogDateSelectedText = it
            },
            datePickerOnClick = showDatePicker
        )
    }



}

fun addNewEvent(event: Event) {
    eventListData.add(event)
}


@Preview
@Composable
private fun CustomCreateEventScreen_Preview() =
    SampleAppThemePreview {
        CustomCreateEventScreen()
    }