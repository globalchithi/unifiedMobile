package com.vaxcare.unifiedhub.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.vaxcare.unifiedhub.core.designsystem.R
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.ui.component.button.OutlineButton
import com.vaxcare.unifiedhub.core.ui.component.button.PrimaryButton
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullDevicePreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalStaticSheet(
    modifier: Modifier = Modifier,
    title: String,
    cancelButtonText: String,
    confirmButtonText: String,
    confirmButtonEnabled: Boolean,
    navigationOption: ModalStaticSheetNavigationOption,
    onCancelRequest: () -> Unit,
    onConfirmRequest: () -> Unit,
    onDismissRequest: () -> Unit,
    onNavigateBackRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(VaxCareTheme.measurement.spacing.medium),
            contentAlignment = Alignment.CenterEnd
        ) {
            Surface(
                shape = RoundedCornerShape(VaxCareTheme.measurement.radius.cardMedium),
                modifier = Modifier
                    .widthIn(min = 280.dp, max = 360.dp)
                    .fillMaxHeight(),
                color = VaxCareTheme.color.surface.surfaceBright
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(VaxCareTheme.measurement.spacing.medium),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    TopAppBar(
                        title = {
                            Box(
                                modifier = Modifier,
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    style = type.titleTypeStyle.titleMedium
                                )
                            }
                        },
                        navigationIcon = {
                            if (navigationOption == ModalStaticSheetNavigationOption.BACK) {
                                IconButton(onClick = {
                                    onNavigateBackRequest()
                                }) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_arrow_back),
                                        contentDescription = "Back"
                                    )
                                }
                            }
                        },
                        actions = {
                            if (navigationOption == ModalStaticSheetNavigationOption.CLOSE) {
                                IconButton(onClick = {
                                    onDismissRequest()
                                }) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_close),
                                        contentDescription = "Close"
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = VaxCareTheme.color.surface.surfaceContainer
                        )
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = VaxCareTheme.measurement.spacing.xSmall)
                    ) {
                        content()
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(VaxCareTheme.measurement.spacing.small)
                    ) {
                        OutlineButton(
                            modifier = Modifier.weight(0.5f),
                            onClick = {
                                onCancelRequest()
                            },
                            text = cancelButtonText
                        )
                        PrimaryButton(
                            modifier = Modifier.weight(0.5f),
                            enabled = confirmButtonEnabled,
                            onClick = {
                                onConfirmRequest()
                            },
                            text = confirmButtonText
                        )
                    }
                }
            }
        }
    }
}

enum class ModalStaticSheetNavigationOption {
    CLOSE,
    BACK
}

@FullDevicePreview
@Composable
private fun PreviewModalStaticSheetClose() {
    VaxCareTheme {
        ModalStaticSheet(
            title = "Test",
            onDismissRequest = { },
            content = {
                val itemsList = (0..60).toList()
                LazyColumn {
                    items(itemsList.size) {
                        SingleSelectionMenuItem(
                            title = "Title",
                            onClick = { }
                        )
                    }
                }
            },
            onNavigateBackRequest = { },
            onConfirmRequest = { },
            onCancelRequest = { },
            navigationOption = ModalStaticSheetNavigationOption.CLOSE,
            cancelButtonText = "Cancel",
            confirmButtonText = "Done",
            confirmButtonEnabled = false
        )
    }
}

@FullDevicePreview
@Composable
private fun PreviewModalStaticSheetBack() {
    VaxCareTheme {
        ModalStaticSheet(
            title = "Test",
            onDismissRequest = { },
            content = {
                val itemsList = (0..60).toList()
                LazyColumn {
                    items(itemsList.size) {
                        SingleSelectionMenuItem(
                            title = "Title",
                            onClick = { }
                        )
                    }
                }
            },
            onNavigateBackRequest = { },
            onConfirmRequest = { },
            onCancelRequest = { },
            navigationOption = ModalStaticSheetNavigationOption.BACK,
            cancelButtonText = "Cancel",
            confirmButtonText = "Save",
            confirmButtonEnabled = false
        )
    }
}
