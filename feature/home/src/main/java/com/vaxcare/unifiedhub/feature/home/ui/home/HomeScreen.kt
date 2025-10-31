package com.vaxcare.unifiedhub.feature.home.ui.home

import android.content.res.Configuration
import androidx.compose.animation.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.color
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.ui.TestTags
import com.vaxcare.unifiedhub.core.ui.arch.BaseMviScreen
import com.vaxcare.unifiedhub.core.ui.component.NoInternetDialog
import com.vaxcare.unifiedhub.core.ui.component.VCScaffold
import com.vaxcare.unifiedhub.core.ui.component.button.ElevatedIconButton
import com.vaxcare.unifiedhub.core.ui.compose.LocalStock
import com.vaxcare.unifiedhub.core.ui.compose.ProvideStock
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullDevicePreview
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullscreenPreview
import com.vaxcare.unifiedhub.core.ui.compose.preview.PortraitPreview
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.home.R
import com.vaxcare.unifiedhub.feature.home.ui.component.StockMenuDialog
import com.vaxcare.unifiedhub.feature.home.ui.component.StockSelectorOneLine
import com.vaxcare.unifiedhub.feature.home.ui.component.StockSelectorTwoLine
import com.vaxcare.unifiedhub.feature.home.ui.home.HomeDialog.AdjustInventory
import com.vaxcare.unifiedhub.feature.home.ui.home.HomeDialog.NoInternet
import com.vaxcare.unifiedhub.feature.home.ui.home.HomeDialog.StockSelection
import com.vaxcare.unifiedhub.feature.home.ui.home.HomeEvent.*
import com.vaxcare.unifiedhub.feature.home.ui.home.HomeIntent.*
import com.vaxcare.unifiedhub.feature.home.ui.home.component.AdjustmentMenuDialog
import com.vaxcare.unifiedhub.feature.home.ui.home.component.NotificationsCard
import com.vaxcare.unifiedhub.feature.home.ui.home.model.AdjustmentListItemUi
import com.vaxcare.unifiedhub.feature.home.ui.home.model.Notification
import com.vaxcare.unifiedhub.feature.home.ui.home.model.TransactionType
import com.vaxcare.unifiedhub.core.designsystem.R as DesignSystemR

@Composable
fun Home(
    navigateToAdmin: () -> Unit,
    navigateToTransaction: (
        transactionType: TransactionType,
        stockType: StockType,
        shouldConfirmStock: Boolean
    ) -> Unit,
    navigateToReturnsExpired: (StockType) -> Unit,
    launchAppUpdate: () -> Unit,
    launchNetworkSettings: () -> Unit,
    openHamburgerMenu: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    BaseMviScreen(
        viewModel = viewModel,
        onEvent = { event ->
            when (event) {
                is NavigateToAdmin -> navigateToAdmin()
                is NavigateToCount -> navigateToTransaction(
                    TransactionType.COUNTS,
                    event.stockType,
                    event.shouldConfirmStock
                )

                is LaunchAppUpdate -> launchAppUpdate()
                is OpenHamburgerMenu -> openHamburgerMenu()
                is NavigateToAddPublic -> navigateToTransaction(
                    TransactionType.ADD_PUBLIC,
                    event.stockType,
                    event.shouldConfirmStock
                )

                is NavigateToBuyback -> {}
                is NavigateToLogWaste -> navigateToTransaction(
                    TransactionType.LOG_WASTE,
                    event.stockType,
                    event.shouldConfirmStock
                )

                is NavigateToReturns -> {
                    if (event.preLoadExpired) {
                        navigateToReturnsExpired(event.stockType)
                    } else {
                        navigateToTransaction(
                            TransactionType.RETURNS,
                            event.stockType,
                            event.shouldConfirmStock,
                        )
                    }
                }
                is NavigateToTransfer -> {}

                is LaunchNetworkSettings -> {
                    launchNetworkSettings()
                }
            }
        }
    ) { state, handleIntent ->

        ProvideStock(state.activeStock) {
            val activeDialog = state.activeDialog
            when (activeDialog) {
                StockSelection -> {
                    StockMenuDialog(
                        onDismiss = {
                            handleIntent(DismissDialog)
                        },
                        stockOptions = state.availableStocks,
                        activeStock = state.activeStock,
                        onStockSelected = {
                            handleIntent(SelectStock(it))
                        }
                    )
                }

                is AdjustInventory -> {
                    AdjustmentMenuDialog(
                        items = activeDialog.adjustmentItems,
                        onDismiss = {
                            handleIntent(DismissDialog)
                        },
                        onItemClick = {
                            handleIntent(SelectAdjustment(it))
                        }
                    )
                }

                is NoInternet -> {
                    NoInternetDialog(
                        allowRetry = activeDialog.allowRetry,
                        onDismiss = {
                            handleIntent(DismissDialog)
                        },
                        onRetry = {
                            handleIntent(NoInternetTryAgain)
                        },
                        onGoToNetworkSettings = {
                            handleIntent(GoToNetworkSettings)
                        }
                    )
                }
            }

            HomeContent(
                orientation = LocalConfiguration.current.orientation,
                state = state,
                handleIntent = handleIntent,
            )
        }
    }
}

@Composable
private fun HomeContent(
    orientation: Int,
    state: HomeState,
    handleIntent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    VCScaffold(
        modifier = modifier.testTag(TestTags.Home.CONTAINER),
        topBar = {
            TopBar(
                clinicName = state.clinicName,
                partnerName = state.partnerName,
                onHamburgerClick = { handleIntent(ShowHamburgerMenu) },
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
            )
        }
    ) {
        val onNotificationClick: (Notification) -> Unit = {
            when (it) {
                is Notification.OverdueCount -> {
                    handleIntent(GoToCount)
                }

                is Notification.ExpiredDoses -> {
                    handleIntent(ReturnExpiredDoses)
                }

                is Notification.AppUpdate -> {
                    handleIntent(ApplyAppUpdate)
                }
            }
        }

        val showStockSelectionButton = state.availableStocks.size > 1
        when (orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                HomePortrait(
                    selectedStock = state.activeStock,
                    notifications = state.notifications,
                    showStockSelectionButton = showStockSelectionButton,
                    modifier = modifier,
                    onStockClick = { handleIntent(OpenStockSelector) },
                    onAdjustClick = { handleIntent(AdjustInventory) },
                    onReturnsClick = { handleIntent(SelectAdjustment(AdjustmentListItemUi.Returns)) },
                    onCountClick = { handleIntent(GoToCount) },
                    onNotificationClick = onNotificationClick
                )
            }

            Configuration.ORIENTATION_LANDSCAPE -> {
                HomeLandscape(
                    selectedStock = state.activeStock,
                    notifications = state.notifications,
                    showStockSelectionButton = showStockSelectionButton,
                    modifier = modifier,
                    onStockClick = { handleIntent(OpenStockSelector) },
                    onAdjustClick = { handleIntent(AdjustInventory) },
                    onReturnsClick = { handleIntent(SelectAdjustment(AdjustmentListItemUi.Returns)) },
                    onCountClick = { handleIntent(GoToCount) },
                    onNotificationClick = onNotificationClick
                )
            }
        }
    }
}

@Composable
private fun HomePortrait(
    selectedStock: StockUi,
    notifications: Set<Notification>,
    showStockSelectionButton: Boolean,
    modifier: Modifier = Modifier,
    onStockClick: () -> Unit,
    onAdjustClick: () -> Unit,
    onReturnsClick: () -> Unit,
    onCountClick: () -> Unit,
    onNotificationClick: (Notification) -> Unit,
) {
    Column(modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = measurement.spacing.large)
        ) {
            Column(Modifier.padding(top = 184.dp)) {
                StockSelectorTwoLine(selectedStock, showStockSelectionButton, onStockClick)

                NotificationsCard(
                    notifications = notifications,
                    modifier = Modifier.padding(end = measurement.spacing.medium),
                    onNotificationClick = onNotificationClick
                )
            }

            SideActionBar(
                onCountClick = onCountClick,
                onReturnsClick = onReturnsClick,
                onAdjustClick = onAdjustClick,
                modifier = Modifier.padding(top = 336.dp)
            )
        }
    }
}

@Composable
private fun HomeLandscape(
    selectedStock: StockUi,
    notifications: Set<Notification>,
    showStockSelectionButton: Boolean,
    modifier: Modifier = Modifier,
    onStockClick: () -> Unit,
    onAdjustClick: () -> Unit,
    onReturnsClick: () -> Unit,
    onCountClick: () -> Unit,
    onNotificationClick: (Notification) -> Unit,
) {
    Column(modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = measurement.spacing.large)
        ) {
            Column(
                modifier = Modifier.padding(
                    top = 64.dp,
                    start = 72.dp
                )
            ) {
                StockSelectorOneLine(selectedStock, showStockSelectionButton, onStockClick)

                NotificationsCard(
                    notifications = notifications,
                    modifier = Modifier.padding(end = measurement.spacing.medium),
                    onNotificationClick = onNotificationClick
                )
            }

            SideActionBar(
                onCountClick = onCountClick,
                onReturnsClick = onReturnsClick,
                onAdjustClick = onAdjustClick,
                modifier = Modifier.padding(top = 96.dp)
            )
        }
    }
}

@Composable
private fun TopBar(
    clinicName: String?,
    partnerName: String?,
    onHamburgerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(
                start = 20.dp,
                end = measurement.spacing.large,
                top = measurement.spacing.xSmall,
                bottom = measurement.spacing.xSmall,
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onHamburgerClick,
                modifier = Modifier
                    .padding(end = 4.dp)
                    .testTag(TestTags.TopBar.CLOSE_BUTTON)
            ) {
                Icon(
                    painter = painterResource(DesignSystemR.drawable.ic_menu),
                    contentDescription = null
                )
            }
            Image(
                painter = painterResource(DesignSystemR.drawable.ic_vaxcare_logo),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 5.dp)
                    .size(28.dp)
            )

            Image(
                painter = painterResource(DesignSystemR.drawable.ic_vaxcare_logo_text),
                contentDescription = null
            )
        }

        if (partnerName != null && clinicName != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = measurement.spacing.medium)
            ) {
                Text(
                    modifier = Modifier.weight(1f, fill = false),
                    text = partnerName,
                    style = type.bodyTypeStyle.body4Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                VerticalDivider(
                    modifier = Modifier
                        .padding(horizontal = measurement.spacing.small)
                        .height(32.dp),
                    thickness = 1.dp,
                    color = color.onContainer.onContainerPrimary
                )

                Text(
                    modifier = Modifier
                        .weight(1.5f, fill = false)
                        .testTag(TestTags.Home.CLINIC_LABEL),
                    text = clinicName,
                    style = type.bodyTypeStyle.body4,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun SideActionBar(
    onCountClick: () -> Unit,
    onReturnsClick: () -> Unit,
    onAdjustClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = CircleShape,
        color = color.container.neutralPress,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(measurement.spacing.small),
            modifier = Modifier.padding(
                horizontal = measurement.spacing.medium,
                vertical = measurement.spacing.large
            )
        ) {
            LabelledIconButton(
                iconPainter = painterResource(DesignSystemR.drawable.ic_dotdotdot),
                containerColor = color.container.primaryContainer,
                label = stringResource(DesignSystemR.string.adjust),
                onClick = onAdjustClick,
                buttonTestTag = TestTags.Home.ADJUST_BUTTON
            )

            LabelledIconButton(
                iconPainter = painterResource(DesignSystemR.drawable.ic_arrow_return),
                containerColor = color.container.primaryContainer,
                label = stringResource(R.string.returns),
                onClick = onReturnsClick,
                buttonTestTag = TestTags.Home.RETURNS_BUTTON
            )

            val currentStockColor = LocalStock.current.colors.container
            val containerColor = remember {
                Animatable(currentStockColor)
            }
            LaunchedEffect(currentStockColor) {
                containerColor.animateTo(currentStockColor)
            }
            LabelledIconButton(
                iconPainter = painterResource(DesignSystemR.drawable.ic_check),
                containerColor = containerColor.value,
                contentColor = color.onContainer.onContainerSecondary,
                label = stringResource(DesignSystemR.string.count),
                onClick = onCountClick,
                buttonTestTag = TestTags.Home.COUNT_BUTTON
            )
        }
    }
}

@Composable
private fun LabelledIconButton(
    onClick: () -> Unit,
    iconPainter: Painter,
    containerColor: Color,
    label: String,
    modifier: Modifier = Modifier,
    contentColor: Color = color.onContainer.onContainerPrimary,
    buttonTestTag: String? = null
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ElevatedIconButton(
            onClick = onClick,
            size = 96.dp,
            modifier = modifier
                .padding(bottom = measurement.spacing.small)
                .then(if (buttonTestTag != null) Modifier.testTag(buttonTestTag) else Modifier)
        ) {
            Icon(
                painter = iconPainter,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier
                    .background(containerColor)
                    .size(96.dp)
                    .padding(28.dp)
            )
        }

        Text(
            text = label,
            style = type.bodyTypeStyle.body6Bold
        )
    }
}

@FullDevicePreview
@Composable
private fun Default() {
    FullscreenPreview {
        HomeContent(
            orientation = LocalConfiguration.current.orientation,
            state = HomeSampleData.Default,
            handleIntent = {}
        )
    }
}

@PortraitPreview
@Composable
private fun StockDialog() {
    FullscreenPreview {
        StockMenuDialog(
            onDismiss = {},
            stockOptions = StockUi.entries,
            activeStock = StockUi.STATE,
            onStockSelected = {}
        )
    }
}
