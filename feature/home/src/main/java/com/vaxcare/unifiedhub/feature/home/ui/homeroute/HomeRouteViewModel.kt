package com.vaxcare.unifiedhub.feature.home.ui.homeroute

import androidx.lifecycle.viewModelScope
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.data.repository.ClinicRepository
import com.vaxcare.unifiedhub.core.data.repository.LocationRepository
import com.vaxcare.unifiedhub.core.datastore.datasource.UsagePreferenceDataSource
import com.vaxcare.unifiedhub.core.domain.IsConnectedUseCase
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModel
import com.vaxcare.unifiedhub.feature.home.ui.home.model.TransactionType
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.HomeRouteEvent.*
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.HomeRouteIntent.*
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.ManageMenuItem.ADD_DOSES
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.ManageMenuItem.BUYBACK
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.ManageMenuItem.COUNT
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.ManageMenuItem.LOG_WASTE
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.ManageMenuItem.RETURNS
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.ManageMenuItem.TRANSFER
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeRouteViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val clinicRepository: ClinicRepository,
    private val usagePrefs: UsagePreferenceDataSource,
    private val dispatcherProvider: DispatcherProvider,
    private val isConnected: IsConnectedUseCase
) : BaseViewModel<HomeRouteState, HomeRouteEvent, HomeRouteIntent>(
        initialState = HomeRouteState()
    ) {
    // Items which always appear in MANAGE section
    private val standardManageMenuItems: List<ManageMenuItem> =
        listOf(
            COUNT,
            TRANSFER,
            LOG_WASTE,
            RETURNS
        )
    private lateinit var eventToRetry: HomeRouteEvent
    private var noOfRetries = 0

    private var shouldConfirmStock = true
    private var shouldConfirmPublicStock = true

    override fun start() {
        viewModelScope.launch(dispatcherProvider.io) {
            locationRepository
                .getStockTypes()
                .filterNotNull()
                .combine(clinicRepository.getNoOfPermanentClinics()) { types, noOfClinics ->
                    val isSingleStock = types.size == 1

                    shouldConfirmStock = !isSingleStock
                    shouldConfirmPublicStock = types.filter { it != StockType.PRIVATE }.size > 1

                    standardManageMenuItems.toMutableList().apply {
                        if (isSingleStock && noOfClinics <= 1) {
                            remove(TRANSFER)
                        }
                    }
                }.combine(
                    flow = usagePrefs.lastSelectedStock.map(StockType::fromId)
                ) { menuItems, activeStock ->
                    menuItems.apply {
                        when (activeStock) {
                            StockType.PRIVATE -> {
                                add(BUYBACK)
                            }
                            else -> {
                                add(ADD_DOSES)
                            }
                        }
                    } to activeStock
                }.collectLatest { (menuItems, activeStock) ->
                    setState {
                        copy(
                            activeStock = activeStock,
                            manageMenuItems = menuItems
                        )
                    }
                }
        }
    }

    override fun handleIntent(intent: HomeRouteIntent) {
        when (intent) {
            GoToAdmin -> sendEvent(NavigateToAdmin)

            GoToAdminInfo -> sendEvent(NavigateToAdminInfo)

            GoToCount -> {
                sendEventOrShowNoInternet(
                    NavigateToTransaction(
                        transactionType = TransactionType.COUNTS,
                        stockType = currentState().activeStock,
                        shouldConfirmStock = shouldConfirmStock
                    )
                )
            }

            GoToAddPublic -> {
                sendEventOrShowNoInternet(
                    NavigateToTransaction(
                        transactionType = TransactionType.ADD_PUBLIC,
                        stockType = currentState().activeStock,
                        shouldConfirmStock = shouldConfirmPublicStock
                    )
                )
            }

            is GoToOnHand -> {
                setState {
                    copy(
                        jumpToSeasonal = intent.jumpToSeasonal,
                        jumpToNonSeasonal = intent.jumpToNonSeasonal
                    )
                }
                sendEvent(ScrollToOnHand)
            }

            GoToLogWaste -> {
                sendEventOrShowNoInternet(
                    NavigateToTransaction(
                        transactionType = TransactionType.LOG_WASTE,
                        stockType = currentState().activeStock,
                        shouldConfirmStock = shouldConfirmStock
                    )
                )
            }

            GoToReturns -> {
                sendEventOrShowNoInternet(
                    NavigateToTransaction(
                        transactionType = TransactionType.RETURNS,
                        stockType = currentState().activeStock,
                        shouldConfirmStock = shouldConfirmStock
                    )
                )
            }

            JumpCompleted -> {
                setState {
                    copy(jumpToSeasonal = false, jumpToNonSeasonal = false)
                }
            }

            DismissDialog -> {
                setState {
                    noOfRetries = 0
                    copy(activeDialog = null)
                }
            }

            NoInternetTryAgain -> {
                viewModelScope.launch(dispatcherProvider.io) {
                    noOfRetries++

                    setState {
                        copy(activeDialog = null)
                    }

                    delay(500)

                    sendEventOrShowNoInternet(eventToRetry)
                }
            }

            GoToNetworkSettings -> {
                noOfRetries = 0
                setState {
                    copy(activeDialog = null)
                }
                sendEvent(LaunchNetworkSettings)
            }
        }
    }

    private fun sendEventOrShowNoInternet(event: HomeRouteEvent) {
        viewModelScope.launch(dispatcherProvider.io) {
            if (isConnected()) {
                sendEvent(event)
            } else {
                eventToRetry = event
                setState {
                    copy(
                        activeDialog = HomeRouteDialog.NoInternet(allowRetry = noOfRetries < 3)
                    )
                }
            }
        }
    }
}
