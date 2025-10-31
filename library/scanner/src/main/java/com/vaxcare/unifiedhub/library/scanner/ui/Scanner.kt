package com.vaxcare.unifiedhub.library.scanner.ui

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.vaxcare.unifiedhub.core.designsystem.R
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.color
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.library.scanner.domain.ParsedBarcode
import com.vaxcare.unifiedhub.library.scanner.domain.ScanType

@Composable
fun Scanner(
    modifier: Modifier = Modifier,
    scanType: ScanType,
    onBarcode: (ParsedBarcode) -> Unit,
    scannerActive: Boolean = true,
    invalidLicenseContent: @Composable () -> Unit = { EmptyScanner(modifier) }
) {
    /*
    Show this if is used in a Preview.
    Ref: https://developer.android.com/develop/ui/compose/tooling/previews#localinspectionmode
     */
    if (LocalInspectionMode.current) {
        Box(
            modifier
                .background(Color.LightGray.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Favorite, contentDescription = null)
                Text("Scanner Preview")
            }
        }
        return
    }

    val viewModel: ScannerViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is ScannerEvent.BarcodeScanned -> onBarcode(event.barcode)
            }
        }
    }

    LifecycleEventEffect(event = Lifecycle.Event.ON_START) {
        viewModel.handleIntent(ScannerIntent.Start(scanType))
    }

    LifecycleEventEffect(event = Lifecycle.Event.ON_STOP) {
        viewModel.handleIntent(ScannerIntent.Stop)
    }

    LaunchedEffect(scannerActive) {
        if (scannerActive) {
            viewModel.handleIntent(ScannerIntent.Resume)
        } else {
            viewModel.handleIntent(ScannerIntent.Pause)
        }
    }

    Box(modifier) {
        uiState.retryStatus?.let {
            ScannerErrorState(
                retryStatus = it,
                modifier = modifier,
                scanType = scanType,
                handleIntent = viewModel::handleIntent
            )
        } ?: run {
            when (val status = uiState.status) {
                is ScannerStatus.Activation -> {
                    if (!status.ok) invalidLicenseContent()
                }

                is ScannerStatus.Scanning,
                is ScannerStatus.Paused -> {
                    AnimatedVisibility(
                        modifier = modifier,
                        visible = uiState.previewView != null,
                        enter = fadeIn(animationSpec = tween(1000, 500)),
                        exit = fadeOut(animationSpec = tween(1000, 500))
                    ) {
                        uiState.previewView?.let { preview ->
                            Box(
                                modifier = modifier,
                                contentAlignment = Alignment.Center
                            ) {
                                AndroidView(
                                    factory = { context ->
                                        FrameLayout(context).apply {
                                            clipToOutline = true
                                            if (preview.parent == null) {
                                                addView(
                                                    preview,
                                                    FrameLayout.LayoutParams(
                                                        MATCH_PARENT,
                                                        MATCH_PARENT
                                                    )
                                                )
                                            }
                                        }
                                    },
                                )

                                Icon(
                                    painter = painterResource(R.drawable.scanner_crosshairs),
                                    tint = MaterialTheme.colorScheme.primary,
                                    contentDescription = "Scanner Crosshairs"
                                )
                            }
                        } ?: EmptyScanner(modifier)
                    }
                }

                is ScannerStatus.Activating,
                is ScannerStatus.Uninitialized,
                is ScannerStatus.Stopped -> EmptyScanner(modifier)
            }
        }
    }
}

@Composable
private fun ScannerErrorState(
    modifier: Modifier = Modifier,
    retryStatus: RetryStatus,
    scanType: ScanType,
    handleIntent: (ScannerIntent) -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val transition = rememberInfiniteTransition()
        val angle by transition.animateFloat(
            initialValue = 0f,
            targetValue = if (retryStatus == RetryStatus.Running) 360f else 0f,
            animationSpec = infiniteRepeatable(animation = tween(1000, easing = LinearEasing))
        )
        Icon(
            modifier = Modifier.graphicsLayer(rotationZ = angle),
            painter = painterResource(R.drawable.ic_refresh),
            contentDescription = "refresh"
        )

        Spacer(Modifier.height(35.dp))
        Text(
            text = stringResource(R.string.scanner_problem_title),
            style = type.bodyTypeStyle.body5
        )

        when (retryStatus) {
            RetryStatus.Idle ->
                TextButton(
                    onClick = { handleIntent(ScannerIntent.Refresh(scanType)) },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = color.onContainer.onContainerPrimary
                    )
                ) {
                    Text(
                        text = stringResource(R.string.scanner_problem_action),
                        style = type.bodyTypeStyle.body5Bold.copy(textDecoration = TextDecoration.Underline)
                    )
                }

            RetryStatus.Block -> {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.scanner_problem_try_again),
                    style = type.bodyTypeStyle.body5
                )
            }

            else -> Unit
        }
    }
}

@Composable
fun EmptyScanner(modifier: Modifier = Modifier) {
    Box(modifier)
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
private fun ScannerPreview() {
    Scanner(
        scanType = ScanType.DOSE,
        invalidLicenseContent = { },
        onBarcode = { },
    )
}

@Preview
@Composable
private fun ScannerErrorState_Preview() {
    ScannerErrorState(
        modifier = Modifier
            .size(width = 296.dp, height = 184.dp)
            .clip(RoundedCornerShape(measurement.radius.sheetHard))
            .background(Color.White),
        retryStatus = RetryStatus.Idle,
        scanType = ScanType.DOSE,
        handleIntent = {}
    )
}
