package com.vaxcare.unifiedhub.core.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.vaxcare.unifiedhub.core.designsystem.R
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldDatePicker(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate?) -> Unit,
    modifier: Modifier = Modifier,
    placeholderText: String = "DD/MM/YYYY",
    dateFormat: DateTimeFormatter = remember {
        DateTimeFormatter.ofPattern(
            "MMM dd, yyyy",
            Locale.getDefault()
        )
    },
    yearRange: IntRange = remember {
        val currentYear = LocalDate.now().year
        (currentYear)..(currentYear + 5)
    },
    selectableDates: SelectableDates = remember {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean = true

            override fun isSelectableYear(year: Int): Boolean = true
        }
    },
    initiallyExpanded: Boolean = false,
    datePickerModifier: Modifier = Modifier,
    showActionButtons: Boolean = true,
    onExpandedChange: (isExpanded: Boolean, pickerSize: IntSize) -> Unit = { _, _ -> }
) {
    var isPickerVisible by remember { mutableStateOf(initiallyExpanded) }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var datePickerSize by remember { mutableStateOf(IntSize.Zero) }

    var pendingSelectedDateMillis by remember {
        mutableStateOf(selectedDate?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli())
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = pendingSelectedDateMillis,
        yearRange = yearRange,
        selectableDates = selectableDates
    )

    LaunchedEffect(selectedDate) {
        val newMillis = selectedDate?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
        pendingSelectedDateMillis = newMillis
        if (datePickerState.selectedDateMillis != newMillis) {
            datePickerState.selectedDateMillis = newMillis
        }
    }

    LaunchedEffect(datePickerState.selectedDateMillis) {
        pendingSelectedDateMillis = datePickerState.selectedDateMillis
    }

    fun togglePickerVisibility() {
        val newVisibility = !isPickerVisible
        isPickerVisible = newVisibility
        if (newVisibility) {
            pendingSelectedDateMillis =
                selectedDate?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
            datePickerState.selectedDateMillis = pendingSelectedDateMillis
            onExpandedChange(true, datePickerSize)
        } else {
            onExpandedChange(false, IntSize.Zero)
            focusManager.clearFocus()
        }
    }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = selectedDate?.format(dateFormat) ?: "",
            onValueChange = { /* Read-only */ },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            readOnly = true,
            placeholder = { Text(placeholderText) },
            trailingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_calendar),
                    contentDescription = "Open Date Picker",
                    modifier = Modifier.clickable { togglePickerVisibility() }
                )
            },
            interactionSource = remember { MutableInteractionSource() }.also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) togglePickerVisibility()
                    }
                }
            }
        )

        AnimatedVisibility(
            visible = isPickerVisible,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = VaxCareTheme.color.container.primaryContainer,
                        shape = RoundedCornerShape(VaxCareTheme.measurement.radius.cardMedium)
                    )
            ) {
                DatePicker(
                    state = datePickerState,
                    modifier = datePickerModifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .onSizeChanged { newSize ->
                            datePickerSize = newSize
                            if (isPickerVisible) {
                                onExpandedChange(true, newSize)
                            }
                        },
                    colors = DatePickerDefaults.colors(containerColor = VaxCareTheme.color.container.primaryContainer),
                    showModeToggle = true
                )

                if (showActionButtons) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                val newDate = pendingSelectedDateMillis?.let {
                                    Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
                                }
                                onDateSelected(newDate)
                                isPickerVisible = false
                                onExpandedChange(false, IntSize.Zero)
                                focusManager.clearFocus()
                            }
                        ) {
                            Text(
                                style = VaxCareTheme.type.bodyTypeStyle.body5Bold,
                                text = stringResource(R.string.cancel),
                                color = VaxCareTheme.color.onContainer.onContainerPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = {
                                pendingSelectedDateMillis =
                                    selectedDate
                                        ?.atStartOfDay(ZoneOffset.UTC)
                                        ?.toInstant()
                                        ?.toEpochMilli()
                                datePickerState.selectedDateMillis = pendingSelectedDateMillis
                                isPickerVisible = false
                                onExpandedChange(false, IntSize.Zero)
                                focusManager.clearFocus()
                            }
                        ) {
                            Text(
                                style = VaxCareTheme.type.bodyTypeStyle.body5Bold,
                                text = stringResource(R.string.ok),
                                color = VaxCareTheme.color.onContainer.onContainerPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewDockedDatePicker() {
    VaxCareTheme {
        TextFieldDatePicker(
            selectedDate = LocalDate.now(),
            onDateSelected = { selectedDate -> }
        )
    }
}
