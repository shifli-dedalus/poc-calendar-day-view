package com.example.sample.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.android.showkase.annotation.ShowkaseComposable
import com.example.sample.ui.theme.CategoryColor
import com.example.sample.ui.theme.SampleAppThemePreview
import com.example.sample.ui.theme.Typography
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    onSet: () -> Unit,
    dateSelectedText: String,
    textOnValueChange: (String) -> Unit,
    datePickerOnClick: () -> Unit,
    timePickerState: TimePickerState = rememberTimePickerState(),
    zonedDateTime: ZonedDateTime = ZonedDateTime.now()
) {

    BasicAlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
    ) {
        Surface(
            modifier = modifier
                .wrapContentHeight()
                .wrapContentWidth(),
            tonalElevation = AlertDialogDefaults.TonalElevation,
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = modifier
                    .padding(30.dp)
            ) {
                TextField(
                    value = dateSelectedText.let {
                        if (dateSelectedText.isEmpty())
                            zonedDateTime.toLocalDate().convertToStringPattern()
                        else
                            it
                    },
                    label = {
                        Text("Date")
                    },
                    placeholder = {
                        Text("DD/MM/YYYY")
                    },
                    onValueChange = textOnValueChange,
                    trailingIcon = {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier
                                .clickable {
                                    datePickerOnClick()
                                }
                        )
                    }
                )

                Spacer(modifier.padding(vertical = 15.dp))

                TimePicker(
                    state = timePickerState
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {

                    TextButton(
                        onClick = {
                            onDismiss()
                        }
                    ) {
                        Text(
                            text = "Cancel",
                            color = Color.Gray
                        )
                    }

                    Spacer(Modifier.padding(5.dp))

                    TextButton(
                        onClick = onSet
                    ) {
                        Text("Set")
                    }
                }

            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerView(
    onDismissRequest: () -> Unit = {},
    onDismissButton: @Composable (() -> Unit)?,
    onConfirmButton: @Composable (() -> Unit),
    datePickerState: DatePickerState = rememberDatePickerState()
) {
    DatePickerDialog(
        dismissButton = onDismissButton,
        confirmButton = onConfirmButton,
        onDismissRequest = onDismissRequest
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}


@Composable
fun ElevatedIconButton(
    modifier: Modifier = Modifier, onClick: () -> Unit,
    shape: Shape = RoundedCornerShape(10.dp), color: Color = MaterialTheme.colorScheme.surface,
    shadowElevation: Dp = 10.dp, iconContent: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = shape,
        shadowElevation = shadowElevation,
        color = color,
    ) {
        IconButton(
            onClick = onClick,
            content = iconContent
        )
    }
}


@Composable
fun DateNavigator(
    modifier: Modifier = Modifier, textTitle: String, textOnClick: () -> Unit,
    forwardOnClick: () -> Unit, backOnClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        ElevatedIconButton(
            onClick = backOnClick
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                contentDescription = null,
                modifier = Modifier.weight(0.2f),
            )
        }

        TextButton(
            modifier = Modifier.weight(0.6f),
            onClick = textOnClick
        ) {
            Text(
                text = textTitle,
                style = Typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }

        ElevatedIconButton(
            onClick = forwardOnClick
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.weight(0.2f)
            )
        }
    }
}


@Composable
fun CategoryChooserDropMenu(
    modifier: Modifier = Modifier, expand: Boolean, selectedColor: Color,
    onSelected: (Color) -> Unit = {}, onDismiss: () -> Unit = {}
) {

    Box {
        DropdownMenu(
            expand,
            modifier = modifier
                .wrapContentSize(),
            onDismissRequest = onDismiss
        ) {
            DropdownMenuItem(
                text = {
                    CategoryColorItem(color = CategoryColor.blue_crayola, text = "Blue")
                },
                onClick = { onSelected(CategoryColor.blue_crayola) },
                trailingIcon = {
                    if (selectedColor == CategoryColor.blue_crayola) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null
                        )
                    }
                }
            )

            DropdownMenuItem(
                text = {
                    CategoryColorItem(color = CategoryColor.brown_sandy, text = "Brown")
                },
                onClick = { onSelected(CategoryColor.brown_sandy) },
                trailingIcon = {
                    if (selectedColor == CategoryColor.brown_sandy) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null
                        )
                    }
                }
            )

            DropdownMenuItem(
                text = {
                    CategoryColorItem(color = CategoryColor.purple_veronica, text = "Purple")
                },
                onClick = { onSelected(CategoryColor.purple_veronica) },
                trailingIcon = {
                    if (selectedColor == CategoryColor.purple_veronica) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null
                        )
                    }
                }
            )

            DropdownMenuItem(
                text = {
                    CategoryColorItem(color = CategoryColor.green_light, text = "Green")
                },
                onClick = { onSelected(CategoryColor.green_light) },
                trailingIcon = {
                    if (selectedColor == CategoryColor.green_light) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun CategoryColorItem(modifier: Modifier = Modifier, color: Color, text: String) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier
                .padding(end = 10.dp)
                .size(30.dp),
            shape = CircleShape,
            colors = CardDefaults.cardColors(color),
            elevation = CardDefaults.elevatedCardElevation(2.dp)
        ) {}

        Text(text)
    }
}


@Preview
@Composable
fun DateNavigator_Preview() = SampleAppThemePreview {
    DateNavigator(
        modifier = Modifier.fillMaxWidth(),
        textTitle = "TomorrowTomorrowTomorrowTomorrowTomorrowTomorrowTomorrowTomorrowTomorrowTomorrow",
        textOnClick = {},
        forwardOnClick = {},
        backOnClick = {}
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TimePickerDialog_Preview() {
    TimePickerDialog(
        onSet = { },
        dateSelectedText = "",
        textOnValueChange = { },
        datePickerOnClick = { },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun DatePickerView_Preview() = SampleAppThemePreview {
    DatePickerView(
        onDismissButton = {},
        onConfirmButton = {},
    )
}


@Preview
@Composable
fun CategoryDropMenu_Preview() = SampleAppThemePreview {
    CategoryChooserDropMenu(expand = true, selectedColor = CategoryColor.purple_veronica)
}

//@ShowkaseComposable
@Preview
@Composable
fun MyButton() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text("__hgfhjbknClick me__", fontSize = 32.sp)
    }
}
