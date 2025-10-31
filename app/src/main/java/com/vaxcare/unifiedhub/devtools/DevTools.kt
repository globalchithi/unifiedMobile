package com.vaxcare.unifiedhub.devtools

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.color
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.ui.component.BaseTitleBar
import com.vaxcare.unifiedhub.core.ui.component.VCScaffold
import com.vaxcare.unifiedhub.core.ui.component.button.OutlineButton
import com.vaxcare.unifiedhub.core.ui.component.button.PrimaryButton
import kotlinx.serialization.Serializable
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

@Serializable
data object DevToolsRoute

@Composable
fun DevTools(
    navigateToAdminInfo: () -> Unit,
    navigateToAdmin: () -> Unit,
    navigateToHome: () -> Unit,
    viewModel: DevToolsViewModel = hiltViewModel()
) {
    val serialNo by viewModel.serialNumber.collectAsState("NO_PERMISSION")
    val network by viewModel.networkData.collectAsState()
    val syncState by viewModel.syncState.collectAsState()

    VCScaffold(
        topBar = {
            BaseTitleBar(
                buttonIcon = DesignSystemR.drawable.ic_close,
                title = "Dev Tools",
                onButtonClick = navigateToHome
            )
        },
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(32.dp),
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(modifier = Modifier.padding(top = 24.dp)) {
                Text(
                    text = "Dev.Actions",
                    style = type.displayTypeStyle.display3,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                SyncSection(
                    syncState = syncState,
                    onSync = viewModel::onSync,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    OutlineButton(
                        onClick = viewModel::sendTestMetric,
                        text = "Test Metric"
                    )

                    OutlineButton(
                        onClick = viewModel::triggerCrash,
                        text = "Trigger Crash",
                        borderStroke = BorderStroke(2.dp, color.onContainer.error)
                    )

                    OutlineButton(
                        onClick = viewModel::triggerANR,
                        text = "Trigger ANR",
                        borderStroke = BorderStroke(2.dp, color.onContainer.error)
                    )
                }
            }

            Column {
                Text(
                    text = "Dev.Shortcuts",
                    style = type.displayTypeStyle.display3,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    OutlineButton(
                        onClick = navigateToAdmin,
                        text = "Admin Loggin'"
                    )

                    OutlineButton(
                        onClick = navigateToAdminInfo,
                        text = "Admin Info"
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Text(
                    text = "Dev.Data",
                    style = type.displayTypeStyle.display3,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                DataRow(
                    name = "Synced Users::",
                    value = "${syncState.totalUsers}"
                )

                DataRow(
                    name = "Serial No::",
                    value = serialNo
                )

                Box(
                    contentAlignment = Alignment.Center
                ) {
                    HorizontalDivider()

                    Text(
                        text = "Network Data".uppercase(),
                        style = type.bodyTypeStyle.body5,
                        color = color.onContainer.disabled,
                        modifier = Modifier
                            .background(color.surface.surface)
                            .padding(horizontal = 8.dp)
                    )
                }

                DataRow(
                    name = "Status::",
                    value = "${network.connectivityStatus}"
                )

                DataRow(
                    name = "Connection::",
                    value = "${network.connectivityStatus}"
                )

                DataRow(
                    name = "Signal Strength::",
                    value = "${network.signalStrengthLevel}"
                )

                DataRow(
                    name = "Frequency::",
                    value = "${network.frequency}"
                )
            }
        }
    }
}

@Composable
fun SyncSection(
    syncState: SyncState,
    modifier: Modifier = Modifier,
    onSync: (DevLocation) -> Unit,
) {
    var locationToSync: DevLocation? by remember {
        mutableStateOf(null)
    }

    OutlinedCard(
        border = BorderStroke(2.dp, color.outline.fourHundred),
        modifier = modifier.animateContentSize()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            var expanded: Boolean by remember {
                mutableStateOf(false)
            }

            Text(
                text = "Quick Location Sync",
                style = type.headerTypeStyle.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Box {
                    TextButton(
                        onClick = {
                            expanded = true
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = color.onContainer.onContainerPrimary
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (locationToSync != null) {
                                    "Sync to ${locationToSync!!.name}"
                                } else {
                                    "Sync to..."
                                },
                                style = type.bodyTypeStyle.body4Bold
                            )

                            Icon(
                                imageVector = Icons.Rounded.KeyboardArrowDown,
                                contentDescription = null
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DevLocation.entries.forEach { location ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = location.name,
                                        style = type.bodyTypeStyle.body4,
                                    )
                                },
                                onClick = {
                                    expanded = false
                                    locationToSync = location
                                }
                            )
                        }
                    }
                }

                PrimaryButton(
                    onClick = {
                        onSync(locationToSync!!)
                    },
                    enabled = !syncState.loading && locationToSync != null,
                    text = "Launch Sync",
                    modifier = Modifier.padding(start = 16.dp)
                )

                if (syncState.loading) {
                    CircularProgressIndicator(Modifier.padding(start = 16.dp))
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                if (syncState.clinicName != null) {
                    Text(
                        text = "Synced to ${syncState.clinicName}",
                        style = type.bodyTypeStyle.body4Bold,
                        color = color.onContainer.success,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .weight(1f, fill = false)
                    )

                    Icon(
                        painter = painterResource(DesignSystemR.drawable.ic_check),
                        contentDescription = null,
                        tint = color.onContainer.success,
                        modifier = Modifier.size(16.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Text(
                            text = "Pins: ",
                            style = type.bodyTypeStyle.body5Bold
                        )

                        syncState.topPins.forEach {
                            Text(
                                text = it,
                                style = type.bodyTypeStyle.body5
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Not Synced",
                        style = type.bodyTypeStyle.body4Bold,
                        color = color.onContainer.error,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Icon(
                        painter = painterResource(DesignSystemR.drawable.ic_close),
                        contentDescription = null,
                        tint = color.onContainer.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DataRow(
    name: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = name,
            style = type.bodyTypeStyle.body4
        )
        Text(
            text = value,
            style = type.bodyTypeStyle.body4Bold
        )
    }
}
