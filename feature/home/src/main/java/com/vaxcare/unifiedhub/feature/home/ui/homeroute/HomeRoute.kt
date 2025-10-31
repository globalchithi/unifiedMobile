package com.vaxcare.unifiedhub.feature.home.ui.homeroute

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.ui.arch.BaseMviScreen
import com.vaxcare.unifiedhub.core.ui.component.NoInternetDialog
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullDevicePreview
import com.vaxcare.unifiedhub.core.ui.compose.preview.FullscreenPreview
import com.vaxcare.unifiedhub.feature.home.R
import com.vaxcare.unifiedhub.feature.home.ui.home.Home
import com.vaxcare.unifiedhub.feature.home.ui.home.model.TransactionType
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.HomeRouteEvent.*
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.HomeRouteIntent.*
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.ManageMenuItem.ADD_DOSES
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.ManageMenuItem.BUYBACK
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.ManageMenuItem.COUNT
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.ManageMenuItem.LOG_WASTE
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.ManageMenuItem.RETURNS
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.ManageMenuItem.TRANSFER
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.hamburgerMenu.HamburgerMenu
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.hamburgerMenu.model.ResIdHamburgerItem
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.hamburgerMenu.model.ResIdHamburgerSection
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.hamburgerMenu.model.StringHamburgerItem
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.hamburgerMenu.model.StringHamburgerSection
import com.vaxcare.unifiedhub.feature.home.ui.onhand.OnHand
import kotlinx.coroutines.launch

@Composable
fun HomeRoute(
    navigateToAdmin: () -> Unit,
    navigateToAdminInfo: () -> Unit,
    navigateToTransaction: (
        transactionType: TransactionType,
        stockType: StockType,
        shouldConfirmStock: Boolean
    ) -> Unit,
    navigateToReturnsExpired: (StockType) -> Unit,
    launchAppUpdate: () -> Unit,
    launchNetworkSettings: () -> Unit,
    viewModel: HomeRouteViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 2 }
    )

    BaseMviScreen(
        viewModel = viewModel,
        onEvent = { event ->
            if (event !is LaunchNetworkSettings) {
                scope.launch {
                    drawerState.close()
                }
            }

            when (event) {
                is NavigateToAdmin -> navigateToAdmin()
                is NavigateToAdminInfo -> navigateToAdminInfo()
                is NavigateToTransaction -> navigateToTransaction(
                    event.transactionType,
                    event.stockType,
                    event.shouldConfirmStock
                )

                is ScrollToOnHand -> {
                    scope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                }

                is LaunchNetworkSettings -> {
                    launchNetworkSettings()
                }
            }
        }
    ) { state, handleIntent ->

        val activeDialog = state.activeDialog
        when (activeDialog) {
            is HomeRouteDialog.NoInternet -> {
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

            else -> {}
        }

        HomeRouteContent(
            activeStock = state.activeStock,
            activeProductCategory = state.activeProductCategory,
            manageMenuItems = state.manageMenuItems,
            drawerState = drawerState,
            handleIntent = handleIntent,
            onMenuClose = { scope.launch { drawerState.close() } },
        ) {
            Box(Modifier.fillMaxSize()) {
                HorizontalPager(pagerState) { pageNo ->
                    when (pageNo) {
                        0 -> {
                            Home(
                                navigateToAdmin = navigateToAdmin,
                                navigateToTransaction = navigateToTransaction,
                                navigateToReturnsExpired = navigateToReturnsExpired,
                                launchAppUpdate = launchAppUpdate,
                                launchNetworkSettings = launchNetworkSettings,
                                openHamburgerMenu = { scope.launch { drawerState.open() } }
                            )
                        }

                        1 -> {
                            OnHand(
                                jumpToSeasonal = state.jumpToSeasonal,
                                jumpToNonSeasonal = state.jumpToNonSeasonal,
                                onJumpCompleted = {
                                    handleIntent(
                                        JumpCompleted
                                    )
                                }
                            )
                        }
                    }
                }

                PagerIndicator(
                    currentPage = pagerState.currentPage,
                    pageCount = pagerState.pageCount,
                    modifier = Modifier
                        .padding(bottom = 22.dp)
                        .align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Composable
private fun HomeRouteContent(
    activeStock: StockType,
    activeProductCategory: String,
    manageMenuItems: List<ManageMenuItem>,
    drawerState: DrawerState,
    handleIntent: (HomeRouteIntent) -> Unit,
    onMenuClose: () -> Unit,
    content: @Composable () -> Unit,
) {
    val addDosesText = stringResource(R.string.add_doses, activeStock.prettyName)
    val onHandText = stringResource(R.string.on_hand, activeProductCategory, activeStock.prettyName)
    val manageText = stringResource(R.string.manage, activeProductCategory, activeStock.prettyName)
    val drawerItems = remember(activeStock, activeProductCategory, manageMenuItems) {
        listOf(
            StringHamburgerSection(
                headerText = onHandText,
                items = listOf(
                    ResIdHamburgerItem(R.string.seasonal_flu) { handleIntent(GoToOnHand(jumpToSeasonal = true)) },
                    ResIdHamburgerItem(R.string.non_seasonal) { handleIntent(GoToOnHand(jumpToNonSeasonal = true)) },
                )
            ),
            StringHamburgerSection(
                headerText = manageText,
                items = manageMenuItems.map {
                    when (it) {
                        COUNT -> ResIdHamburgerItem(R.string.count_inventory) {
                            handleIntent(
                                GoToCount
                            )
                        }

                        TRANSFER -> ResIdHamburgerItem(R.string.transfer) {}
                        LOG_WASTE -> ResIdHamburgerItem(R.string.log_waste) {
                            handleIntent(
                                GoToLogWaste
                            )
                        }
                        RETURNS -> ResIdHamburgerItem(R.string.returns) { handleIntent(GoToReturns) }
                        BUYBACK -> ResIdHamburgerItem(R.string.buyback) {}
                        ADD_DOSES -> StringHamburgerItem(addDosesText) { handleIntent(GoToAddPublic) }
                    }
                }
            ),
/*          TODO: temporarily hiding the unimplemented "Help" section for the Pilot release.
            ResIdHamburgerSection(
                headerRes = R.string.help,
                items = listOf(
                    ResIdHamburgerItem(R.string.tutorial_videos) {}
                )
            ),
*/
            ResIdHamburgerSection(
                headerRes = R.string.admin,
                items = listOf(
                    ResIdHamburgerItem(R.string.admin_access) { handleIntent(GoToAdmin) },
                    ResIdHamburgerItem(R.string.hub_info) { handleIntent(GoToAdminInfo) }
                )
            ),
        )
    }

    HamburgerMenu(
        menuItems = drawerItems,
        drawerState = drawerState,
        onMenuClose = onMenuClose,
    ) {
        content()
    }
}

@Composable
private fun PagerIndicator(
    currentPage: Int,
    pageCount: Int,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(measurement.spacing.small),
        ) {
            repeat(pageCount) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .border(
                            width = (1.5).dp,
                            color = VaxCareTheme.color.container.disabled,
                            shape = CircleShape
                        )
                ) {}
            }
        }

        val position by animateDpAsState(
            targetValue = currentPage * 28.dp
        )

        Box(
            modifier = Modifier
                .padding(start = position)
                .size(12.dp)
                .background(
                    color = VaxCareTheme.color.onContainer.onContainerPrimary,
                    shape = CircleShape
                )
        ) {}
    }
}

@FullDevicePreview
@Composable
private fun Default() {
    FullscreenPreview {
        HomeRouteContent(
            activeStock = StockType.PRIVATE,
            activeProductCategory = "Vaccines",
            manageMenuItems = listOf(
                COUNT,
                TRANSFER,
                LOG_WASTE,
                RETURNS,
                BUYBACK,
                ADD_DOSES
            ),
            drawerState = DrawerState(DrawerValue.Open),
            handleIntent = {},
            onMenuClose = {},
        ) {}
    }
}
