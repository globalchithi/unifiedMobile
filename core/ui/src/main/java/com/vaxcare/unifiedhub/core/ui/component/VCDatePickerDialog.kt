package com.vaxcare.unifiedhub.core.ui.component

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import com.vaxcare.unifiedhub.core.common.ext.toEpochMillisAtStartOfDay
import com.vaxcare.unifiedhub.core.common.ext.toLocalDate
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import com.vaxcare.unifiedhub.core.ui.component.button.TextButton
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VCDatePickerDialog(
    initialSelectedDate: LocalDate,
    onDismissRequest: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
) {
    val pickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDate.toEpochMillisAtStartOfDay(),
    )

    DatePickerDialog(
        colors = DatePickerDefaults.colors(
            containerColor = VaxCareTheme.color.container.primaryContainer
        ),
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        onDateSelected(millis.toLocalDate())
                    }
                },
                text = "OK"
            )
        },
        dismissButton = {
            TextButton(
                onClick = {
                    pickerState.selectedDateMillis?.let {
                        onDismissRequest()
                    }
                },
                text = "Cancel",
            )
        }
    ) {
        DatePicker(
            state = pickerState,
            colors = DatePickerDefaults.colors(
                containerColor = VaxCareTheme.color.container.primaryContainer
            )
        )
    }
}
