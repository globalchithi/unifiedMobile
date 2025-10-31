package com.vaxcare.unifiedhub.feature.admin.ui.info

import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.vaxcare.unifiedhub.core.designsystem.component.ContentBlockIconTrailing
import com.vaxcare.unifiedhub.core.designsystem.component.ContentBlockTextTrailing
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import com.vaxcare.unifiedhub.core.ui.arch.BaseMviScreen
import com.vaxcare.unifiedhub.core.ui.component.BaseTitleBar
import com.vaxcare.unifiedhub.feature.admin.R
import com.vaxcare.unifiedhub.feature.admin.ui.info.dialog.ValidateScannerDialog
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

@Composable
fun AdminInfoScreen(
    onBackClick: () -> Unit,
    onOpenSystemConnectivity: () -> Unit,
    viewModel: AdminInfoViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    BaseMviScreen<AdminInfoState, AdminInfoEvent, AdminInfoIntent>(
        viewModel = viewModel,
        onEvent = { event ->
            when (event) {
                AdminInfoEvent.NavigateBack -> onBackClick()
                AdminInfoEvent.NavigateToOpenSourceLibrary -> {
                    context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
                }

                AdminInfoEvent.NavigateToSystemConnectivity -> onOpenSystemConnectivity()
            }
        }
    ) { state, handleIntent ->
        AdminInfoContent(
            state = state,
            handleIntent = handleIntent,
        )
    }
}

@Composable
fun AdminInfoContent(
    state: AdminInfoState,
    handleIntent: (AdminInfoIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            BaseTitleBar(
                modifier = Modifier,
                title = R.string.admin_info_title,
                buttonIcon = DesignSystemR.drawable.ic_close,
                onButtonClick = { handleIntent(AdminInfoIntent.CloseScreen) }
            )
        },
        containerColor = VaxCareTheme.color.surface.surface
    ) { paddingValues ->
        val screenOrientation = LocalConfiguration.current.orientation
        with(VaxCareTheme) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(
                        top = when (screenOrientation) {
                            Configuration.ORIENTATION_PORTRAIT -> {
                                measurement.spacing.topBarLargeY
                            }

                            Configuration.ORIENTATION_LANDSCAPE -> {
                                measurement.spacing.topBarMediumY
                            }

                            else -> measurement.spacing.topBarMediumY
                        },
                        bottom = measurement.spacing.medium
                    ),
                contentAlignment = Alignment.TopCenter
            ) {
                Surface(
                    modifier = Modifier.width(measurement.size.cardLarge),
                    shape = RoundedCornerShape(measurement.radius.cardMedium),
                    color = color.container.primaryContainer,
                ) {
                    Column(
                        modifier = Modifier.padding(measurement.spacing.large),
                        verticalArrangement = Arrangement.spacedBy(measurement.spacing.medium),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                style = type.bodyTypeStyle.body4,
                                text = stringResource(R.string.admin_info_apk)
                            )
                            Text(
                                style = type.bodyTypeStyle.body4,
                                text = stringResource(
                                    R.string.admin_info_apk_value,
                                    state.apkVersion
                                )
                            )
                        }
                        ContentBlockTextTrailing(
                            headerText = stringResource(R.string.admin_info_serial_number),
                            bodyText = stringResource(R.string.admin_info_serial_number_description),
                            trailingText = state.serialNumber
                        )
/*                      TODO: we are temporarily hiding these unimplemented pieces for the pilot release
                        ContentBlockIconTrailing(
                            headerText = stringResource(R.string.admin_info_system_connectivity),
                            onClick = { handleIntent(AdminInfoIntent.OpenSystemConnectivity) }
                        )
                        ContentBlock(
                            headerText = stringResource(R.string.admin_info_database_records),
                            bodyText = stringResource(R.string.admin_info_database_description, "-", "-"),
                        )
*/
                        ContentBlockIconTrailing(
                            headerText = stringResource(R.string.admin_info_open_source_library_attribution),
                            onClick = { handleIntent(AdminInfoIntent.OpenSourceLibrary) }
                        )
                        TextButton(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            content = {
                                Text(
                                    style = type.bodyTypeStyle.body5Bold,
                                    color = color.onContainer.onContainerPrimary,
                                    textDecoration = TextDecoration.Underline,
                                    text = stringResource(R.string.admin_info_validate_scanner_license)
                                )
                            },
                            onClick = { handleIntent(AdminInfoIntent.ValidateScannerLicenseClicked) }
                        )
                    }
                }
            }
            if (state.activeDialog == AdminInfoDialog.ValidateScannerLicense) {
                ValidateScannerDialog(onDismiss = { handleIntent(AdminInfoIntent.CloseValidateScanner) })
            }
        }
    }
}

@Preview(heightDp = 1182, widthDp = 738, showBackground = true)
@Composable
fun PreviewPortraitAdminInfoContent() {
    VaxCareTheme {
        AdminInfoContent(
            state = AdminInfoState(
                apkVersion = "2025.05.20",
                serialNumber = "VAXHUB212345"
            ),
            handleIntent = {}
        )
    }
}

@Preview(heightDp = 1182, widthDp = 738, showBackground = true)
@Composable
fun PreviewPortraitAdminInfoContentWithValidateScannerDialog() {
    VaxCareTheme {
        AdminInfoContent(
            state = AdminInfoState(
                apkVersion = "2025.05.20",
                serialNumber = "VAXHUB212345",
                activeDialog = AdminInfoDialog.ValidateScannerLicense
            ),
            handleIntent = {}
        )
    }
}

@Preview(heightDp = 738, widthDp = 1182, showBackground = true)
@Composable
fun PreviewLandscapeAdminInfoContent() {
    VaxCareTheme {
        AdminInfoContent(
            state = AdminInfoState(
                apkVersion = "2025.05.20",
                serialNumber = "VAXHUB212345"
            ),
            handleIntent = {}
        )
    }
}

@Preview(heightDp = 738, widthDp = 1182, showBackground = true)
@Composable
fun PreviewLandscapeAdminInfoContentWithValidateScannerDialog() {
    VaxCareTheme {
        AdminInfoContent(
            state = AdminInfoState(
                apkVersion = "2025.05.20",
                serialNumber = "VAXHUB212345",
                activeDialog = AdminInfoDialog.ValidateScannerLicense
            ),
            handleIntent = {}
        )
    }
}
