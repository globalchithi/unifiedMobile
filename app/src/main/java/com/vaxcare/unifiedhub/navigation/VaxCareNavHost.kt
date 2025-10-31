package com.vaxcare.unifiedhub.navigation

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.datadog.android.compose.ExperimentalTrackingApi
import com.datadog.android.compose.NavigationViewTrackingEffect
import com.datadog.android.rum.tracking.AcceptAllNavDestinations
import com.vaxcare.unifiedhub.core.model.inventory.ReturnReason
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.devtools.DevTools
import com.vaxcare.unifiedhub.devtools.DevToolsRoute
import com.vaxcare.unifiedhub.feature.admin.navigation.AdminDetailsRoute
import com.vaxcare.unifiedhub.feature.admin.navigation.AdminInfoRoute
import com.vaxcare.unifiedhub.feature.admin.navigation.AdminLoginRoute
import com.vaxcare.unifiedhub.feature.admin.navigation.adminSection
import com.vaxcare.unifiedhub.feature.admin.navigation.navigateToAdmin
import com.vaxcare.unifiedhub.feature.admin.navigation.navigateToAdminDetails
import com.vaxcare.unifiedhub.feature.admin.navigation.navigateToAdminInfo
import com.vaxcare.unifiedhub.feature.admin.ui.details.AdminDetailsScreen
import com.vaxcare.unifiedhub.feature.admin.ui.info.AdminInfoScreen
import com.vaxcare.unifiedhub.feature.admin.ui.login.AdminLoginScreen
import com.vaxcare.unifiedhub.feature.home.navigation.HomeRoute
import com.vaxcare.unifiedhub.feature.home.navigation.navigateToHome
import com.vaxcare.unifiedhub.feature.home.ui.home.model.TransactionType.ADD_PUBLIC
import com.vaxcare.unifiedhub.feature.home.ui.home.model.TransactionType.BUY_BACK
import com.vaxcare.unifiedhub.feature.home.ui.home.model.TransactionType.COUNTS
import com.vaxcare.unifiedhub.feature.home.ui.home.model.TransactionType.LOG_WASTE
import com.vaxcare.unifiedhub.feature.home.ui.home.model.TransactionType.RETURNS
import com.vaxcare.unifiedhub.feature.home.ui.home.model.TransactionType.TRANSFER
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.HomeRoute
import com.vaxcare.unifiedhub.feature.pinin.navigation.PinInRoute
import com.vaxcare.unifiedhub.feature.pinin.ui.PinInScreen
import com.vaxcare.unifiedhub.feature.transactions.navigation.AddLotRoute
import com.vaxcare.unifiedhub.feature.transactions.navigation.AddPublicCompleteRoute
import com.vaxcare.unifiedhub.feature.transactions.navigation.AddPublicHomeRoute
import com.vaxcare.unifiedhub.feature.transactions.navigation.AddPublicLotInteractionRoute
import com.vaxcare.unifiedhub.feature.transactions.navigation.AddPublicSectionRoute
import com.vaxcare.unifiedhub.feature.transactions.navigation.AddPublicSummaryRoute
import com.vaxcare.unifiedhub.feature.transactions.navigation.CountsCompleteRoute
import com.vaxcare.unifiedhub.feature.transactions.navigation.CountsHomeRoute
import com.vaxcare.unifiedhub.feature.transactions.navigation.CountsSectionRoute
import com.vaxcare.unifiedhub.feature.transactions.navigation.CountsSubmitRoute
import com.vaxcare.unifiedhub.feature.transactions.navigation.LogWasteCompleteRoute
import com.vaxcare.unifiedhub.feature.transactions.navigation.LogWasteReasonRoute
import com.vaxcare.unifiedhub.feature.transactions.navigation.LogWasteRoute
import com.vaxcare.unifiedhub.feature.transactions.navigation.LogWasteSectionRoute
import com.vaxcare.unifiedhub.feature.transactions.navigation.LogWasteSummaryRoute
import com.vaxcare.unifiedhub.feature.transactions.navigation.LotInteractionRoute
import com.vaxcare.unifiedhub.feature.transactions.navigation.LotSearchRoute
import com.vaxcare.unifiedhub.feature.transactions.navigation.ReturnProductInteractionRoute
import com.vaxcare.unifiedhub.feature.transactions.navigation.ReturnReasonRoute
import com.vaxcare.unifiedhub.feature.transactions.navigation.ReturnsCompleteRoute
import com.vaxcare.unifiedhub.feature.transactions.navigation.ReturnsSectionRoute
import com.vaxcare.unifiedhub.feature.transactions.navigation.ReturnsSummaryRoute
import com.vaxcare.unifiedhub.feature.transactions.navigation.ReturnsWindowRoute
import com.vaxcare.unifiedhub.feature.transactions.navigation.addPublicSection
import com.vaxcare.unifiedhub.feature.transactions.navigation.countsSection
import com.vaxcare.unifiedhub.feature.transactions.navigation.logWasteSection
import com.vaxcare.unifiedhub.feature.transactions.navigation.navigateToAddLot
import com.vaxcare.unifiedhub.feature.transactions.navigation.navigateToAddPublic
import com.vaxcare.unifiedhub.feature.transactions.navigation.navigateToAddPublicComplete
import com.vaxcare.unifiedhub.feature.transactions.navigation.navigateToAddPublicHome
import com.vaxcare.unifiedhub.feature.transactions.navigation.navigateToAddPublicLotInteraction
import com.vaxcare.unifiedhub.feature.transactions.navigation.navigateToAddPublicSummary
import com.vaxcare.unifiedhub.feature.transactions.navigation.navigateToCounts
import com.vaxcare.unifiedhub.feature.transactions.navigation.navigateToCountsComplete
import com.vaxcare.unifiedhub.feature.transactions.navigation.navigateToCountsHome
import com.vaxcare.unifiedhub.feature.transactions.navigation.navigateToCountsSubmit
import com.vaxcare.unifiedhub.feature.transactions.navigation.navigateToLogWaste
import com.vaxcare.unifiedhub.feature.transactions.navigation.navigateToLogWasteComplete
import com.vaxcare.unifiedhub.feature.transactions.navigation.navigateToLogWasteReason
import com.vaxcare.unifiedhub.feature.transactions.navigation.navigateToLogWasteSection
import com.vaxcare.unifiedhub.feature.transactions.navigation.navigateToLogWasteSummary
import com.vaxcare.unifiedhub.feature.transactions.navigation.navigateToLotInteraction
import com.vaxcare.unifiedhub.feature.transactions.navigation.navigateToLotSearch
import com.vaxcare.unifiedhub.feature.transactions.navigation.navigateToReturns
import com.vaxcare.unifiedhub.feature.transactions.navigation.navigateToReturnsComplete
import com.vaxcare.unifiedhub.feature.transactions.navigation.navigateToReturnsProductInteraction
import com.vaxcare.unifiedhub.feature.transactions.navigation.navigateToReturnsReason
import com.vaxcare.unifiedhub.feature.transactions.navigation.navigateToReturnsSummary
import com.vaxcare.unifiedhub.feature.transactions.navigation.navigateToReturnsWindow
import com.vaxcare.unifiedhub.feature.transactions.navigation.returnsSection
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.complete.AddPublicCompleteScreen
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.complete.AddPublicCompleteViewModel
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.home.AddPublicHomeScreen
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.home.AddPublicHomeViewModel
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.lotinteraction.AddPublicLotInteractionScreen
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.lotinteraction.AddPublicLotInteractionViewModel
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.session.AddPublicSession
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.summary.AddPublicSummary
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.summary.AddPublicSummaryViewModel
import com.vaxcare.unifiedhub.feature.transactions.ui.common.confirmstock.ConfirmStockScreen
import com.vaxcare.unifiedhub.feature.transactions.ui.common.confirmstock.navigation.ConfirmStockRoute
import com.vaxcare.unifiedhub.feature.transactions.ui.common.confirmstock.navigation.navigateToConfirmStock
import com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.add.AddLotScreen
import com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.search.LotSearchScreen
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.complete.CountsComplete
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.home.CountsHomeScreen
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.home.CountsHomeViewModel
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.interaction.LotInteractionScreen
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.interaction.LotInteractionViewModel
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.session.CountSession
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.submit.CountsSubmit
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.submit.CountsSubmitViewModel
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.LogWasteScreen
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.LogWasteViewModel
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.complete.LogWasteCompleteScreen
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.complete.LogWasteCompleteViewModel
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.LogWasteReasonScreen
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.LogWasteReasonViewModel
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.reason.model.LogWasteReason
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.session.LogWasteSession
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.summary.LogWasteSummary
import com.vaxcare.unifiedhub.feature.transactions.ui.logwaste.summary.LogWasteSummaryViewModel
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.complete.ReturnsCompleteScreen
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.complete.ReturnsCompleteViewModel
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionScreen
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.productInteraction.ReturnsProductInteractionViewModel
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.reason.ReturnReasonScreen
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.session.ReturnsSession
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.summary.ReturnsSummaryScreen
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.summary.ReturnsSummaryViewModel
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.window.ReturnsWindowScreen
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.window.ReturnsWindowViewModel
import timber.log.Timber
import com.vaxcare.unifiedhub.feature.transactions.R as TransactionsR

@OptIn(ExperimentalTrackingApi::class)
@Composable
fun VaxCareNavHost(navController: NavHostController, launchAppUpdate: () -> Unit) {
    // Connect Datadog to NavHost for navigation tracking
    NavigationViewTrackingEffect(
        navController = navController,
        trackArguments = true,
        destinationPredicate = AcceptAllNavDestinations()
    )

    NavHost(
        navController = navController,
        startDestination = HomeRoute,
    ) {
        composable<DevToolsRoute> {
            DevTools(
                navigateToAdmin = navController::navigateToAdmin,
                navigateToAdminInfo = navController::navigateToAdminInfo,
                navigateToHome = navController::popBackStack
            )
        }

        composable<HomeRoute> {
            val context = LocalContext.current
            val networkSettingsIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
            HomeRoute(
                navigateToAdmin = navController::navigateToAdmin,
                navigateToAdminInfo = navController::navigateToAdminInfo,
                navigateToTransaction = { transactionType, stock, shouldConfirmStock ->
                    when (transactionType) {
                        COUNTS -> navController.navigateToCounts(
                            stockType = stock,
                            shouldConfirmStock = shouldConfirmStock
                        )

                        LOG_WASTE -> {
                            navController.navigateToLogWasteSection(
                                stockType = stock,
                                shouldConfirmStock = shouldConfirmStock
                            )
                        }

                        TRANSFER -> {}

                        ADD_PUBLIC -> navController.navigateToAddPublic(
                            stockType = stock,
                            shouldConfirmStock = shouldConfirmStock
                        )

                        BUY_BACK -> {}

                        RETURNS -> navController.navigateToReturns(
                            stockType = stock,
                            skipStockConfirmation = !shouldConfirmStock
                        )
                    }
                },
                navigateToReturnsExpired = { stockType ->
                    navController.navigateToReturns(
                        stockType = stockType,
                        reason = ReturnReason.EXPIRED,
                        skipStockConfirmation = true,
                        skipReasonSelection = true
                    )
                },
                launchAppUpdate = launchAppUpdate,
                launchNetworkSettings = {
                    context.startActivity(networkSettingsIntent)
                }
            )
        }

        adminSection {
            composable<AdminLoginRoute> {
                AdminLoginScreen(
                    onBackClick = navController::popBackStack,
                    onLoginSuccess = navController::navigateToAdminDetails
                )
            }

            composable<AdminDetailsRoute> {
                AdminDetailsScreen(
                    onBackClick = {
                        navController.navigateToHome {
                            popUpTo<HomeRoute> {
                                inclusive = true
                            }
                        }
                    },
                    onHubInfoClick = {
                        navController.navigateToAdminInfo()
                    }
                )
            }

            composable<AdminInfoRoute> {
                AdminInfoScreen(
                    onBackClick = {
                        navController.navigateToHome {
                            popUpTo<HomeRoute> {
                                inclusive = true
                            }
                        }
                    },
                    onOpenSystemConnectivity = {
                        // TODO: implement
                    }
                )
            }
        }

        countsSection(PinInRoute) {
            composable<PinInRoute> {
                val args = it.savedStateHandle.toRoute<CountsSectionRoute>()
                val shouldConfirmStock =
                    it.savedStateHandle.toRoute<CountsSectionRoute>().shouldConfirmStock
                PinInScreen(
                    navigateBack = navController::popBackStack,
                    onSuccess = {
                        if (shouldConfirmStock) {
                            navController.navigateToConfirmStock(
                                title = TransactionsR.string.count_product,
                                subtitle = TransactionsR.string.counts_confirm_stock_subtitle,
                                preselectedStock = args.stockType,
                                navOptions = {
                                    popUpTo(CountsSectionRoute::class) {
                                        inclusive = false
                                    }
                                }
                            )
                        } else {
                            navController.navigateToCountsHome(
                                navOptions = navOptions {
                                    popUpTo(CountsSectionRoute::class) {
                                        inclusive = false
                                    }
                                }
                            )
                        }
                    }
                )
            }

            composable<ConfirmStockRoute> {
                val countSession: CountSession = it.sharedViewModel(navController)
                ConfirmStockScreen(
                    onNavigateBack = navController::popBackStack,
                    onStockConfirmed = { stock ->
                        countSession.stockType = stock
                        navController.navigateToCountsHome(
                            navOptions = navOptions {
                                popUpTo(CountsSectionRoute::class) {
                                    inclusive = false
                                }
                            }
                        )
                    }
                )
            }

            composable<CountsHomeRoute> {
                val countSession: CountSession = it.sharedViewModel(navController)

                CountsHomeScreen(
                    navigateBack = navController::popBackStack,
                    navigateToSubmit = navController::navigateToCountsSubmit,
                    navigateToLotSearch = { sourceId ->
                        navController.navigateToLotSearch(
                            sourceId = sourceId,
                            transactionName = COUNTS.name
                        )
                    },
                    navigateToLotInteraction = navController::navigateToLotInteraction,
                    viewModel = hiltViewModel<CountsHomeViewModel, CountsHomeViewModel.Factory> { factory ->
                        factory.create(countSession)
                    }
                )
            }

            composable<LotInteractionRoute> {
                val countSession: CountSession = it.sharedViewModel(navController)
                LotInteractionScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onLotSearchClick = { productId, sourceId ->
                        navController.navigateToLotSearch(
                            filterProductId = productId,
                            sourceId = sourceId,
                            transactionName = COUNTS.name
                        )
                    },
                    viewModel =
                        hiltViewModel<LotInteractionViewModel, LotInteractionViewModel.Factory> { factory ->
                            factory.create(session = countSession)
                        }
                )
            }

            composable<LotSearchRoute> { backStackEntry ->
                val countSession: CountSession = backStackEntry.sharedViewModel(navController)

                LotSearchScreen(
                    onNavigateBack = navController::popBackStack,
                    onLotSelected = { lotNumber, _ ->
                        countSession.setSearchedLot(lotNumber)
                        navController.navigateToLotInteraction {
                            if (countSession.productId == null) {
                                popUpTo<CountsHomeRoute> { inclusive = false }
                            } else {
                                popUpTo<LotInteractionRoute> { inclusive = false }
                            }
                            launchSingleTop = true
                        }
                    },
                    onAddNewLot = { lotNumber, productId ->
                        navController.navigateToAddLot(lotNumber, productId)
                    }
                )
            }

            composable<AddLotRoute> { backStackEntry ->
                val countSession: CountSession = backStackEntry.sharedViewModel(navController)

                AddLotScreen(
                    onClose = navController::popBackStack,
                    onConfirm = { newLotNumber ->
                        countSession.setSearchedLot(newLotNumber)
                        navController.navigateToLotInteraction {
                            popUpTo<LotSearchRoute> { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable<CountsSubmitRoute> {
                val countSession: CountSession = it.sharedViewModel(navController)
                CountsSubmit(
                    navigateBack = navController::popBackStack,
                    navigateToConfirmation = { totals ->
                        navController.navigateToCountsComplete(
                            stockType = countSession.stockType,
                            totals = totals,
                        ) {
                            popUpTo<CountsSectionRoute>()
                        }
                    },
                    viewModel = hiltViewModel<CountsSubmitViewModel, CountsSubmitViewModel.Factory> { factory ->
                        factory.create(countSession)
                    }
                )
            }

            composable<CountsCompleteRoute> {
                CountsComplete(navigateBack = navController::popBackStack)
            }
        }

        logWasteSection(PinInRoute) {
            composable<PinInRoute> {
                val args = it.savedStateHandle.toRoute<LogWasteSectionRoute>()
                val shouldConfirmStock =
                    it.savedStateHandle.toRoute<LogWasteSectionRoute>().shouldConfirmStock
                PinInScreen(
                    navigateBack = navController::popBackStack,
                    onSuccess = {
                        if (shouldConfirmStock) {
                            navController.navigateToConfirmStock(
                                title = TransactionsR.string.log_waste,
                                subtitle = TransactionsR.string.log_waste_confirm_stock_subtitle,
                                preselectedStock = args.stockType,
                                navOptions = {
                                    popUpTo(LogWasteSectionRoute::class) {
                                        inclusive = false
                                    }
                                }
                            )
                        } else {
                            navController.navigateToLogWasteReason()
                        }
                    }
                )
            }

            composable<ConfirmStockRoute> { backStackEntry ->
                val logWasteSession: LogWasteSession = backStackEntry.sharedViewModel(navController)

                ConfirmStockScreen(
                    onNavigateBack = navController::popBackStack,
                    onStockConfirmed = { stock ->
                        logWasteSession.stockType = stock
                        navController.navigateToLogWasteReason(
                            navOptions {
                                popUpTo(LogWasteSectionRoute::class) {
                                    inclusive = false
                                }
                            }
                        )
                    }
                )
            }

            composable<LogWasteReasonRoute> { backStackEntry ->
                val logWasteSession: LogWasteSession = backStackEntry.sharedViewModel(navController)

                LogWasteReasonScreen(
                    reasonConfirmed = { reason ->
                        logWasteSession.setWasteReason(reason)
                        navController.navigateToLogWaste()
                    },
                    navigateBack = navController::popBackStack,
                    returnProducts = { reason ->
                        val returnReason = when (reason) {
                            LogWasteReason.EXPIRED -> ReturnReason.EXPIRED
                            LogWasteReason.DELIVER_OUT_OF_TEMP -> ReturnReason.DELIVER_OUT_OF_TEMP
                            else -> {
                                Timber.e(
                                    "Navigating to RETURNS from LOG WASTE, but the selected reason was $reason. This should be impossible; please investigate."
                                )
                                ReturnReason.EXPIRED
                            }
                        }
                        navController.navigateToReturnsProductInteraction(
                            stockType = logWasteSession.stockType,
                            reason = returnReason
                        )
                    },
                    viewModel = hiltViewModel<LogWasteReasonViewModel, LogWasteReasonViewModel.Factory> { factory ->
                        factory.create(logWasteSession)
                    }
                )
            }

            composable<LogWasteRoute> { backStackEntry ->
                val logWasteSession: LogWasteSession = backStackEntry.sharedViewModel(navController)

                LogWasteScreen(
                    navigateToSummary = navController::navigateToLogWasteSummary,
                    navigateSearchLot = {
                        navController.navigateToLotSearch(
                            sourceId = logWasteSession.stockType.id,
                            transactionName = COUNTS.name,
                            addNewLotEnabled = false
                        )
                    },
                    navigateBack = navController::popBackStack,
                    viewModel = hiltViewModel<LogWasteViewModel, LogWasteViewModel.Factory> { factory ->
                        factory.create(logWasteSession)
                    }
                )
            }

            composable<LogWasteSummaryRoute> { backStackEntry ->
                val logWasteSession: LogWasteSession = backStackEntry.sharedViewModel(navController)
                LogWasteSummary(
                    navigateBack = navController::popBackStack,
                    navigateToComplete = {
                        navController.navigateToLogWasteComplete {
                            popUpTo<LogWasteSectionRoute>()
                        }
                    },
                    viewModel = hiltViewModel<LogWasteSummaryViewModel, LogWasteSummaryViewModel.Factory> { factory ->
                        factory.create(logWasteSession)
                    }
                )
            }

            composable<LotSearchRoute> { backStackEntry ->
                val logWasteSession: LogWasteSession = backStackEntry.sharedViewModel(navController)

                LotSearchScreen(
                    onNavigateBack = navController::popBackStack,
                    onLotSelected = { lotNumber, _ ->
                        logWasteSession.setSearchedLot(lotNumber)
                        navController.navigateToLogWaste {
                            popUpTo<LogWasteRoute> { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onAddNewLot = { lotNumber, productId ->
                        // Not a valid scenario
                    }
                )
            }

            composable<LogWasteCompleteRoute> {
                val logWasteSession: LogWasteSession = it.sharedViewModel(navController)

                LogWasteCompleteScreen(
                    navigateBack = navController::popBackStack,
                    viewModel = hiltViewModel<LogWasteCompleteViewModel, LogWasteCompleteViewModel.Factory> { factory ->
                        factory.create(logWasteSession)
                    }
                )
            }
        }

        addPublicSection(PinInRoute) {
            composable<PinInRoute> { backStackEntry ->
                val args = backStackEntry.savedStateHandle.toRoute<AddPublicSectionRoute>()
                val shouldConfirmStock = args.shouldConfirmStock
                PinInScreen(
                    navigateBack = navController::popBackStack,
                    onSuccess = {
                        if (shouldConfirmStock) {
                            navController.navigateToConfirmStock(
                                title = TransactionsR.string.add_public,
                                subtitle = TransactionsR.string.add_public_confirm_stock_subtitle,
                                preselectedStock = args.stockType,
                                publicStocksOnly = true,
                                navOptions = {
                                    popUpTo(AddPublicSectionRoute::class) {
                                        inclusive = false
                                    }
                                }
                            )
                        } else {
                            navController.navigateToAddPublicHome(
                                navOptions = navOptions {
                                    popUpTo(AddPublicSectionRoute::class) {
                                        inclusive = false
                                    }
                                }
                            )
                        }
                    }
                )
            }

            composable<ConfirmStockRoute> { backStackEntry ->
                val addPublicSession: AddPublicSession =
                    backStackEntry.sharedViewModel(navController)
                ConfirmStockScreen(
                    onNavigateBack = navController::popBackStack,
                    onStockConfirmed = { stock ->
                        addPublicSession.stockType = stock
                        navController.navigateToAddPublicHome(
                            navOptions = navOptions {
                                popUpTo(AddPublicSectionRoute::class) {
                                    inclusive = false
                                }
                            }
                        )
                    }
                )
            }

            composable<AddPublicHomeRoute> { backStackEntry ->
                val addPublicSession: AddPublicSession =
                    backStackEntry.sharedViewModel(navController)
                AddPublicHomeScreen(
                    navigateBack = navController::popBackStack,
                    navigateToSummary = navController::navigateToAddPublicSummary,
                    navigateToLotSearch = { sourceId ->
                        navController.navigateToLotSearch(
                            filterExpiredLots = true,
                            sourceId = sourceId,
                            transactionName = ADD_PUBLIC.name
                        )
                    },
                    navigateToLotInteraction = navController::navigateToAddPublicLotInteraction,
                    viewModel = hiltViewModel<AddPublicHomeViewModel, AddPublicHomeViewModel.Factory> { factory ->
                        factory.create(addPublicSession)
                    }
                )
            }

            composable<AddPublicLotInteractionRoute>(
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Start
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                    )
                }
            ) {
                val session: AddPublicSession = it.sharedViewModel<AddPublicSession>(navController)
                AddPublicLotInteractionScreen(
                    onNavigateBack = navController::popBackStack,
                    onLotSearchClick = { productId, sourceId ->
                        navController.navigateToLotSearch(
                            filterExpiredLots = true,
                            filterProductId = productId,
                            sourceId = sourceId,
                            transactionName = COUNTS.name
                        )
                    },
                    viewModel = hiltViewModel<
                        AddPublicLotInteractionViewModel,
                        AddPublicLotInteractionViewModel.Factory
                    > { factory ->
                        factory.create(session = session)
                    }
                )
            }

            composable<LotSearchRoute> { backStackEntry ->
                val session: AddPublicSession = backStackEntry.sharedViewModel(navController)
                LotSearchScreen(
                    onNavigateBack = navController::popBackStack,
                    onLotSelected = { lotNumber, _ ->
                        session.setCount(lotNumber, 1)
                        session.setSearchedLot(lotNumber)
                        navController.navigateToAddPublicLotInteraction {
                            if (session.productId == null) {
                                popUpTo<AddPublicHomeRoute> { inclusive = false }
                            } else {
                                popUpTo<AddPublicLotInteractionRoute> { inclusive = false }
                            }
                            launchSingleTop = true
                        }
                    },
                    onAddNewLot = { lotNumber, productId ->
                        navController.navigateToAddLot(lotNumber, productId)
                    }
                )
            }

            composable<AddLotRoute> { backStackEntry ->
                val session: AddPublicSession = backStackEntry.sharedViewModel(navController)
                AddLotScreen(
                    onClose = navController::popBackStack,
                    onConfirm = { newLotNumber ->
                        session.setCount(newLotNumber, 1)
                        session.setSearchedLot(newLotNumber)
                        navController.navigateToAddPublicLotInteraction {
                            popUpTo<LotSearchRoute> { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable<AddPublicSummaryRoute> { backStackEntry ->
                val session: AddPublicSession = backStackEntry.sharedViewModel(navController)
                AddPublicSummary(
                    navigateBack = navController::popBackStack,
                    navigateToComplete = {
                        navController.navigateToAddPublicComplete {
                            popUpTo<AddPublicSectionRoute>()
                        }
                    },
                    viewModel = hiltViewModel<AddPublicSummaryViewModel, AddPublicSummaryViewModel.Factory> { factory ->
                        factory.create(session)
                    }
                )
            }

            composable<AddPublicCompleteRoute> {
                val session: AddPublicSession = it.sharedViewModel(navController)

                AddPublicCompleteScreen(
                    navigateBack = navController::popBackStack,
                    viewModel = hiltViewModel<
                        AddPublicCompleteViewModel,
                        AddPublicCompleteViewModel.Factory
                    > { factory ->
                        factory.create(session)
                    }
                )
            }
        }

        returnsSection(PinInRoute) {
            composable<PinInRoute> { backStackEntry ->
                val args = backStackEntry.savedStateHandle.toRoute<ReturnsSectionRoute>()
                PinInScreen(
                    navigateBack = navController::popBackStack,
                    onSuccess = {
                        with(args) {
                            val navOpts: NavOptionsBuilder.() -> Unit = {
                                popUpTo<ReturnsSectionRoute>()
                            }
                            val reason = reason

                            when (skipStockConfirmation to skipReasonSelection) {
                                // skip both screens (reason should ALWAYS be non-null here)
                                true to true if reason != null -> {
                                    navController.navigateToReturnsProductInteraction(
                                        stockType = stockType,
                                        reason = reason,
                                        navOptions = navOpts
                                    )
                                }

                                true to false -> {
                                    navController.navigateToReturnsReason(
                                        stockType = stockType,
                                        navOptions = navOpts
                                    )
                                }

                                else -> {
                                    navController.navigateToConfirmStock(
                                        title = TransactionsR.string.return_products,
                                        subtitle = TransactionsR.string.returns_confirm_stock_subtitle,
                                        preselectedStock = args.stockType,
                                        publicStocksOnly = false,
                                        navOptions = navOpts
                                    )
                                }
                            }
                        }
                    }
                )
            }

            composable<ConfirmStockRoute> { backStackEntry ->
                ConfirmStockScreen(
                    onNavigateBack = navController::popBackStack,
                    onStockConfirmed = { stock ->
                        navController.navigateToReturnsReason(stock) {
                            popUpTo<ReturnsSectionRoute>()
                        }
                    }
                )
            }

            composable<ReturnReasonRoute> { backStackEntry ->
                val args = backStackEntry.savedStateHandle.toRoute<ReturnReasonRoute>()

                ReturnReasonScreen(
                    reasonConfirmed = { reason ->
                        navController.navigateToReturnsProductInteraction(
                            stockType = args.stockType,
                            reason = reason.toDomain()
                        )
                    },
                    navigateBack = navController::popBackStack,
                )
            }

            composable<ReturnProductInteractionRoute> {
                val args = it.savedStateHandle.toRoute<ReturnProductInteractionRoute>()
                val session = it.sharedViewModel<ReturnsSession>(navController)
                val viewModel = hiltViewModel<
                    ReturnsProductInteractionViewModel,
                    ReturnsProductInteractionViewModel.Factory
                > { factory ->
                    factory.create(session)
                }

                ReturnsProductInteractionScreen(
                    stock = StockUi.map(args.stockType),
                    onNavigateBack = navController::popBackStack,
                    onLotSearchClick = { sourceId ->
                        navController.navigateToLotSearch(
                            sourceId = sourceId,
                            transactionName = RETURNS.name,
                            addNewLotEnabled = false
                        )
                    },
                    onNextClick = {
                        val windowReasons = setOf(
                            ReturnReason.EXPIRED,
                            ReturnReason.FRIDGE_OUT_OF_TEMP,
                        )
                        if (args.stockType == StockType.PRIVATE &&
                            windowReasons.contains(args.reason) &&
                            viewModel.isAutomatedReturnsEnabled == true
                        ) {
                            navController.navigateToReturnsWindow(
                                stockType = args.stockType,
                                reason = args.reason
                            )
                        } else {
                            navController.navigateToReturnsSummary(
                                stockType = args.stockType,
                                reason = args.reason,
                            )
                        }
                    },
                    viewModel = viewModel
                )
            }

            composable<ReturnsWindowRoute> {
                val args = it.savedStateHandle.toRoute<ReturnsWindowRoute>()
                val session = it.sharedViewModel<ReturnsSession>(navController)

                ReturnsWindowScreen(
                    onNavigateBack = navController::popBackStack,
                    onNavigateToSummary = { noOfLabels ->
                        navController.navigateToReturnsSummary(
                            stockType = args.stockType,
                            reason = args.reason,
                            noOfLabels = noOfLabels,
                        )
                    },
                    viewModel = hiltViewModel<
                        ReturnsWindowViewModel,
                        ReturnsWindowViewModel.Factory
                    > { factory ->
                        factory.create(session)
                    }
                )
            }

            composable<LotSearchRoute> {
                val session: ReturnsSession = it.sharedViewModel(navController)
                LotSearchScreen(
                    onNavigateBack = navController::popBackStack,
                    onLotSelected = { lotNumber, _ ->
                        session.setCount(lotNumber, 1)
                        navController.popBackStack()
                    },
                    onAddNewLot = { _, _ ->
                        // 'Add Lot' is disabled for Returns.
                    }
                )
            }

            composable<ReturnsSummaryRoute> {
                val args = it.savedStateHandle.toRoute<ReturnsSummaryRoute>()
                val session: ReturnsSession = it.sharedViewModel(navController)
                ReturnsSummaryScreen(
                    onNavigateBack = navController::popBackStack,
                    onNavigateForward = {
                        navController.navigateToReturnsComplete(
                            stockType = args.stockType,
                            reason = args.reason
                        ) {
                            popUpTo<ReturnsSectionRoute>()
                        }
                    },
                    viewModel = hiltViewModel<
                        ReturnsSummaryViewModel,
                        ReturnsSummaryViewModel.Factory
                    > { factory ->
                        factory.create(session)
                    }
                )
            }

            composable<ReturnsCompleteRoute> {
                val session: ReturnsSession = it.sharedViewModel(navController)
                ReturnsCompleteScreen(
                    navigateBack = navController::popBackStack,
                    viewModel = hiltViewModel<
                        ReturnsCompleteViewModel,
                        ReturnsCompleteViewModel.Factory
                    > { factory ->
                        factory.create(session)
                    }
                )
            }
        }
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentEntry)
}
