package com.vaxcare.unifiedhub.feature.admin.ui.details

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import com.vaxcare.unifiedhub.core.ui.TestTags
import com.vaxcare.unifiedhub.core.ui.component.BaseTitleBar
import com.vaxcare.unifiedhub.core.ui.component.ErrorMessage
import com.vaxcare.unifiedhub.core.ui.component.button.OutlineButton
import com.vaxcare.unifiedhub.core.ui.component.keypad.KeypadDialog
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullDevicePreview
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullscreenPreview
import com.vaxcare.unifiedhub.feature.admin.R
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

@Composable
fun AdminDetailsScreen(
    onBackClick: () -> Unit,
    onHubInfoClick: () -> Unit,
    viewModel: AdminDetailsViewModel = hiltViewModel(),
) {
    val screenUiState by viewModel.screenUiState.collectAsStateWithLifecycle()
    val dialogUiState by viewModel.keypadUiState.collectAsStateWithLifecycle()

    if (screenUiState.showKeypad) {
        val title = if (dialogUiState.isPartnerId) {
            R.string.enter_partner_id
        } else {
            R.string.enter_clinic_id
        }
        KeypadDialog(
            dialogTitle = stringResource(title),
            input = dialogUiState.input,
            onCloseClick = viewModel::onKeypadClose,
            onDigitClick = viewModel::onKeypadNumberClick,
            onDeleteClick = viewModel::onKeypadDeleteClick,
            onClearClick = viewModel::onKeypadClearClick,
            onSubmit = viewModel::onKeypadSubmit
        )
    }

    AdminDetailsScreen(
        partnerID = screenUiState.partnerID,
        clinicID = screenUiState.clinicID,
        clinicName = screenUiState.clinicName,
        isLoading = screenUiState.isLoading,
        isError = screenUiState.isError,
        onBackClick = onBackClick,
        onEditPartnerID = viewModel::onEditPartnerID,
        onEditClinicID = viewModel::onEditClinicID,
        onHubInfoClick = onHubInfoClick,
    )
}

@Composable
private fun AdminDetailsScreen(
    modifier: Modifier = Modifier,
    partnerID: String,
    clinicID: String,
    clinicName: String,
    isLoading: Boolean,
    isError: Boolean,
    onBackClick: () -> Unit,
    onEditPartnerID: () -> Unit,
    onEditClinicID: () -> Unit,
    onHubInfoClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            BaseTitleBar(
                title = R.string.admin_details_title,
                buttonIcon = DesignSystemR.drawable.ic_close,
                onButtonClick = onBackClick,
            )
        },
        containerColor = VaxCareTheme.color.surface.surface,
        modifier = modifier
    ) { innerPadding ->

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            ErrorMessage(
                text = R.string.error_invalid_id_pairing,
                isError = isError,
                modifier = Modifier.padding(top = VaxCareTheme.measurement.spacing.large)
            )
            AdminDetailsCard(
                modifier = Modifier.padding(top = VaxCareTheme.measurement.spacing.large)
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = clinicName.isNotBlank(),
                    enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessVeryLow)) + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                    label = "Clinic name"
                ) {
                    Text(
                        text = stringResource(R.string.clinic_name_prefix) + ' ' + clinicName,
                        style = VaxCareTheme.type.bodyTypeStyle.body4,
                        modifier = Modifier.padding(bottom = VaxCareTheme.measurement.spacing.large)
                    )
                }

                PartnerSection(partnerID, isLoading, isError, onEditPartnerID)
                HorizontalDivider()

                ClinicSection(clinicID, isLoading, isError, onEditClinicID)
                HorizontalDivider()

                TextButton(
                    onClick = onHubInfoClick,
                    modifier = Modifier
                        .padding(top = VaxCareTheme.measurement.spacing.large)
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = VaxCareTheme.color.onContainer.onContainerPrimary
                    )
                ) {
                    Text(
                        text = stringResource(R.string.go_to_hub_info),
                        style = VaxCareTheme.type.bodyTypeStyle.body5Bold,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
        }
    }
}

@Composable
fun AdminDetailsCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(VaxCareTheme.measurement.radius.cardLarge),
        colors = CardDefaults.cardColors(
            containerColor = VaxCareTheme.color.container.primaryContainer
        ),
        modifier = modifier
            .padding(horizontal = VaxCareTheme.measurement.spacing.small)
            .width(VaxCareTheme.measurement.size.cardLarge)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = VaxCareTheme.measurement.spacing.medium,
                vertical = VaxCareTheme.measurement.spacing.large
            ),
            content = content
        )
    }
}

@Composable
fun PartnerSection(
    partnerID: String,
    isLoading: Boolean,
    isError: Boolean,
    onEditPartnerID: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = VaxCareTheme.measurement.spacing.medium)
    ) {
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .padding(end = VaxCareTheme.measurement.spacing.small)
        ) {
            Text(
                text = stringResource(R.string.partner_id),
                style = VaxCareTheme.type.bodyTypeStyle.body4Bold,
                modifier = Modifier.padding(bottom = VaxCareTheme.measurement.spacing.xSmall)
            )
            Text(
                text = stringResource(R.string.partner_id_description),
                style = VaxCareTheme.type.bodyTypeStyle.body4,
                minLines = 2
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.requiredSize(184.dp, 48.dp)
        ) {
            if (partnerID.isEmpty()) {
                OutlineButton(
                    modifier = Modifier
                        .testTag(TestTags.AdminDetails.ENTER_PARTNER_ID_BUTTON)
                        .fillMaxSize(),
                    onClick = onEditPartnerID,
                    text = stringResource(R.string.enter_partner_id),
                )
            } else {
                Text(
                    modifier = Modifier.testTag(TestTags.AdminDetails.PARTNER_ID_LABEL),
                    text = partnerID,
                    style = VaxCareTheme.type.bodyTypeStyle.body4Bold,
                    color = with(VaxCareTheme.color.onContainer) {
                        if (isError) error else onContainerPrimary
                    }
                )
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(48.dp)
                            .testTag(TestTags.AdminDetails.PARTNER_CIRCULAR_PROGRESS_INDICATOR),
                        color = VaxCareTheme.color.container.secondaryContainer
                    )
                } else {
                    IconButton(
                        onClick = onEditPartnerID,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            painter = painterResource(DesignSystemR.drawable.ic_edit),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ClinicSection(
    clinicID: String,
    isLoading: Boolean,
    isError: Boolean,
    onEditClinicID: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = VaxCareTheme.measurement.spacing.medium)
    ) {
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .padding(end = VaxCareTheme.measurement.spacing.small)
        ) {
            Text(
                text = stringResource(R.string.clinic_id),
                style = VaxCareTheme.type.bodyTypeStyle.body4Bold,
                modifier = Modifier.padding(bottom = VaxCareTheme.measurement.spacing.xSmall)
            )
            Text(
                text = stringResource(R.string.clinic_id_description),
                style = VaxCareTheme.type.bodyTypeStyle.body4,
                minLines = 2
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.requiredSize(184.dp, 48.dp)
        ) {
            if (clinicID.isEmpty()) {
                OutlineButton(
                    modifier = Modifier
                        .testTag(TestTags.AdminDetails.ENTER_CLINIC_ID_BUTTON)
                        .fillMaxSize(),
                    onClick = onEditClinicID,
                    text = stringResource(R.string.enter_clinic_id),
                )
            } else {
                Text(
                    modifier = Modifier.testTag(TestTags.AdminDetails.CLINIC_ID_LABEL),
                    text = clinicID,
                    style = VaxCareTheme.type.bodyTypeStyle.body4Bold,
                    color = with(VaxCareTheme.color.onContainer) {
                        if (isError) error else onContainerPrimary
                    }
                )
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = VaxCareTheme.color.container.secondaryContainer
                    )
                } else {
                    IconButton(
                        onClick = onEditClinicID,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            painter = painterResource(DesignSystemR.drawable.ic_edit),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@FullDevicePreview
@Composable
private fun Default() {
    FullscreenPreview {
        AdminDetailsScreen(
            partnerID = "100001",
            clinicID = "10808",
            clinicName = "2nd Baptist Church",
            isLoading = false,
            isError = false,
            onBackClick = {},
            onEditClinicID = {},
            onEditPartnerID = {},
            onHubInfoClick = {}
        )
    }
}

@FullDevicePreview
@Composable
private fun Empty() {
    FullscreenPreview {
        AdminDetailsScreen(
            partnerID = "",
            clinicID = "",
            clinicName = "",
            isLoading = false,
            isError = false,
            onBackClick = {},
            onEditClinicID = {},
            onEditPartnerID = {},
            onHubInfoClick = {}
        )
    }
}

@FullDevicePreview
@Composable
private fun Loading() {
    FullscreenPreview {
        AdminDetailsScreen(
            partnerID = "12345",
            clinicID = "67890",
            clinicName = "",
            isLoading = true,
            isError = false,
            onBackClick = {},
            onEditClinicID = {},
            onEditPartnerID = {},
            onHubInfoClick = {}
        )
    }
}

@FullDevicePreview
@Composable
private fun Error() {
    FullscreenPreview {
        AdminDetailsScreen(
            partnerID = "12345",
            clinicID = "67890",
            clinicName = "",
            isLoading = false,
            isError = true,
            onBackClick = {},
            onEditClinicID = {},
            onEditPartnerID = {},
            onHubInfoClick = {}
        )
    }
}
