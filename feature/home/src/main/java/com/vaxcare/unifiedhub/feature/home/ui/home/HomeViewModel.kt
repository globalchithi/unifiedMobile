package com.vaxcare.unifiedhub.feature.home.ui.home

import androidx.lifecycle.viewModelScope
import com.google.android.play.core.install.model.AppUpdateType
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.data.datasource.AppUpdateRepository
import com.vaxcare.unifiedhub.core.data.repository.ClinicRepository
import com.vaxcare.unifiedhub.core.data.repository.CountRepository
import com.vaxcare.unifiedhub.core.data.repository.LocationRepository
import com.vaxcare.unifiedhub.core.data.repository.LotInventoryRepository
import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.datastore.datasource.UsagePreferenceDataSource
import com.vaxcare.unifiedhub.core.domain.IsConnectedUseCase
import com.vaxcare.unifiedhub.core.model.inventory.StockType
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModel
import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.home.ui.home.HomeEvent.LaunchAppUpdate
import com.vaxcare.unifiedhub.feature.home.ui.home.HomeEvent.NavigateToAddPublic
import com.vaxcare.unifiedhub.feature.home.ui.home.HomeEvent.NavigateToBuyback
import com.vaxcare.unifiedhub.feature.home.ui.home.HomeEvent.NavigateToCount
import com.vaxcare.unifiedhub.feature.home.ui.home.HomeEvent.NavigateToLogWaste
import com.vaxcare.unifiedhub.feature.home.ui.home.HomeEvent.NavigateToReturns
import com.vaxcare.unifiedhub.feature.home.ui.home.HomeEvent.NavigateToTransfer
import com.vaxcare.unifiedhub.feature.home.ui.home.HomeEvent.OpenHamburgerMenu
import com.vaxcare.unifiedhub.feature.home.ui.home.HomeIntent.AdjustInventory
import com.vaxcare.unifiedhub.feature.home.ui.home.HomeIntent.ApplyAppUpdate
import com.vaxcare.unifiedhub.feature.home.ui.home.HomeIntent.DismissDialog
import com.vaxcare.unifiedhub.feature.home.ui.home.HomeIntent.GoToCount
import com.vaxcare.unifiedhub.feature.home.ui.home.HomeIntent.GoToLogWaste
import com.vaxcare.unifiedhub.feature.home.ui.home.HomeIntent.GoToOnHand
import com.vaxcare.unifiedhub.feature.home.ui.home.HomeIntent.OpenStockSelector
import com.vaxcare.unifiedhub.feature.home.ui.home.HomeIntent.ReturnExpiredDoses
import com.vaxcare.unifiedhub.feature.home.ui.home.HomeIntent.SelectAdjustment
import com.vaxcare.unifiedhub.feature.home.ui.home.HomeIntent.SelectStock
import com.vaxcare.unifiedhub.feature.home.ui.home.HomeIntent.ShowHamburgerMenu
import com.vaxcare.unifiedhub.feature.home.ui.home.model.AdjustmentListItemUi
import com.vaxcare.unifiedhub.feature.home.ui.home.model.Notification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.Collections.addAll
import javax.inject.Inject
import kotlin.math.absoluteValue

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val clinicRepository: ClinicRepository,
    private val locationRepository: LocationRepository,
    private val usagePrefs: UsagePreferenceDataSource,
    private val appUpdateRepository: AppUpdateRepository,
    private val countRepository: CountRepository,
    private val lotRepository: LotRepository,
    private val lotInventoryRepository: LotInventoryRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val isConnected: IsConnectedUseCase
) : BaseViewModel<HomeState, HomeEvent, HomeIntent>(initialState = HomeState()) {
    private lateinit var eventToRetry: HomeEvent
    private var noOfRetries = 0
    private var shouldShowTransfers: Boolean = false

    override fun start() {
        appUpdateRepository.checkAppUpdateInfo()

        viewModelScope.launch(dispatcherProvider.io) {
            getLatestCountDate().collectLatest { lastCountDate ->
                val daysSince = lastCountDate?.let {
                    val now = LocalDateTime.now()
                    ChronoUnit.DAYS
                        .between(now, it)
                        .toInt()
                        .absoluteValue
                } ?: 0

                setState {
                    if (daysSince > 7) {
                        copy(
                            notifications = notifications.plusDistinct(
                                Notification.OverdueCount(daysSince)
                            )
                        )
                    } else {
                        copy(notifications = notifications.filterNotInstanceOf<Notification.OverdueCount>())
                    }
                }
            }
        }

        viewModelScope.launch(dispatcherProvider.io) {
            getNoOfExpiredDoses()
                .collectLatest { noOfExpiredDoses ->
                    setState {
                        if (noOfExpiredDoses > 0) {
                            copy(
                                notifications = notifications.plusDistinct(
                                    Notification.ExpiredDoses(noOfExpiredDoses)
                                )
                            )
                        } else {
                            copy(notifications = notifications.filterNotInstanceOf<Notification.ExpiredDoses>())
                        }
                    }
                }
        }

        viewModelScope.launch(dispatcherProvider.io) {
            appUpdateRepository
                .appUpdateInfo
                .collectLatest {
                    setState {
                        if (it.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                            copy(notifications = notifications.plusDistinct(Notification.AppUpdate))
                        } else {
                            copy(notifications = notifications.filterNotInstanceOf<Notification.AppUpdate>())
                        }
                    }
                }
        }

        viewModelScope.launch(dispatcherProvider.io) {
            locationRepository
                .getLocation()
                .filterNotNull()
                .combine(usagePrefs.lastSelectedStock) { location, stockId ->
                    val stockType = StockType.fromId(stockId)
                    if (location.stockTypes.contains(stockType)) {
                        location to stockType
                    } else {
                        usagePrefs.clearLastSelectedStock()
                        location to StockType.PRIVATE
                    }
                }.collectLatest { (location, lastSelectedStock) ->
                    setState {
                        copy(
                            clinicName = location.clinicName,
                            partnerName = location.partnerName,
                            availableStocks = location.stockTypes.map(StockUi::map),
                            activeStock = StockUi.map(lastSelectedStock)
                        )
                    }
                }
        }

        viewModelScope.launch(dispatcherProvider.io) {
            locationRepository
                .getStockTypes()
                .combine(clinicRepository.getNoOfPermanentClinics()) { types, noOfClinics ->
                    types.size > 1 || noOfClinics > 1
                }.collect {
                    shouldShowTransfers = it
                }
        }
    }

    private fun sendEventOrShowNoInternet(event: HomeEvent) {
        viewModelScope.launch(dispatcherProvider.io) {
            if (isConnected()) {
                sendEvent(event)
            } else {
                eventToRetry = event
                setState {
                    copy(
                        activeDialog = HomeDialog.NoInternet(allowRetry = noOfRetries < 3)
                    )
                }
            }
        }
    }

    private inline fun <reified R : Notification> Set<Notification>.filterNotInstanceOf(): Set<Notification> =
        filterNot {
            it is R
        }.toSet()

    private fun Set<Notification>.plusDistinct(notification: Notification): Set<Notification> =
        mutableSetOf(notification).let { result ->
            when (notification) {
                is Notification.OverdueCount -> {
                    filterNotTo(result) { it is Notification.OverdueCount }
                }

                is Notification.ExpiredDoses -> {
                    filterNotTo(result) { it is Notification.ExpiredDoses }
                }

                is Notification.AppUpdate -> {
                    filterNotTo(result) { it is Notification.AppUpdate }
                }
            }
        }

    private fun getLatestCountDate(): Flow<LocalDateTime?> =
        usagePrefs
            .lastSelectedStock
            .flatMapLatest {
                countRepository.getLatestCountDate(StockType.fromId(it))
            }

    private fun getNoOfExpiredDoses(): Flow<Int> =
        lotRepository
            .getExpiredLots()
            .combine(usagePrefs.lastSelectedStock) { expiredLots, stockId ->
                expiredLots to StockType.fromId(stockId)
            }.flatMapLatest { (expiredLots, stockType) ->
                val lotNumbers = expiredLots.map { it.lotNumber }
                lotInventoryRepository
                    .getLotInventory(lotNumbers, stockType)
                    .map { expiredInventory ->
                        expiredInventory
                            .filter { it.onHand > 0 }
                            .sumOf { it.onHand }
                    }
            }

    override fun handleIntent(intent: HomeIntent) {
        when (intent) {
            OpenStockSelector -> {
                setState {
                    copy(activeDialog = HomeDialog.StockSelection)
                }
            }

            AdjustInventory -> {
                setState {
                    val adjustmentItems = buildList {
                        addAll(
                            listOf(
                                AdjustmentListItemUi.Returns,
                                AdjustmentListItemUi.LogWaste,
                            )
                        )

                        if (shouldShowTransfers) {
                            add(AdjustmentListItemUi.Transfer)
                        }

                        if (activeStock == StockUi.PRIVATE) {
                            add(AdjustmentListItemUi.Buyback)
                        } else {
                            add(AdjustmentListItemUi.AddPublic)
                        }
                    }

                    copy(activeDialog = HomeDialog.AdjustInventory(adjustmentItems))
                }
            }

            ShowHamburgerMenu -> {
                sendEvent(OpenHamburgerMenu)
            }

            DismissDialog -> {
                noOfRetries = 0
                setState {
                    copy(activeDialog = null)
                }
            }

            ApplyAppUpdate -> {
                sendEvent(LaunchAppUpdate)
            }

            ReturnExpiredDoses -> {
                sendEventOrShowNoInternet(
                    NavigateToReturns(
                        stockType = currentState().activeStock.toDomain(),
                        preLoadExpired = true,
                    )
                )
            }

            GoToCount -> {
                sendEventOrShowNoInternet(
                    NavigateToCount(
                        stockType = currentState().activeStock.toDomain(),
                        shouldConfirmStock = shouldTransactionConfirmStock(currentState().availableStocks)
                    )
                )
            }

            GoToLogWaste -> {
                sendEventOrShowNoInternet(
                    NavigateToLogWaste(
                        stockType = currentState().activeStock.toDomain(),
                        shouldConfirmStock = shouldTransactionConfirmStock(currentState().availableStocks)
                    )
                )
            }

            GoToOnHand -> {}

            is SelectStock -> {
                setState {
                    copy(
                        activeStock = intent.stock,
                        activeDialog = null,
                    )
                }
                viewModelScope.launch(dispatcherProvider.io) {
                    usagePrefs.setLastSelectedStock(intent.stock.toDomain().id)
                }
            }

            is SelectAdjustment -> {
                setState { copy(activeDialog = null) }

                when (intent.selectedAdjustment) {
                    AdjustmentListItemUi.AddPublic -> {
                        sendEventOrShowNoInternet(
                            NavigateToAddPublic(
                                stockType = currentState().activeStock.toDomain(),
                                shouldConfirmStock = shouldTransactionConfirmStock(getPublicStocks())
                            )
                        )
                    }

                    AdjustmentListItemUi.Buyback -> {
                        sendEventOrShowNoInternet(
                            NavigateToBuyback(currentState().activeStock.toDomain())
                        )
                    }

                    AdjustmentListItemUi.LogWaste -> {
                        sendEventOrShowNoInternet(
                            NavigateToLogWaste(
                                stockType = currentState().activeStock.toDomain(),
                                shouldConfirmStock = shouldTransactionConfirmStock(currentState().availableStocks)
                            )
                        )
                    }

                    AdjustmentListItemUi.Returns -> {
                        sendEventOrShowNoInternet(
                            NavigateToReturns(
                                stockType = currentState().activeStock.toDomain(),
                                shouldConfirmStock = shouldTransactionConfirmStock(currentState().availableStocks)
                            )
                        )
                    }

                    AdjustmentListItemUi.Transfer -> {
                        sendEventOrShowNoInternet(
                            NavigateToTransfer(currentState().activeStock.toDomain())
                        )
                    }
                }
            }

            is HomeIntent.NoInternetTryAgain -> {
                viewModelScope.launch(dispatcherProvider.io) {
                    noOfRetries++

                    setState {
                        copy(activeDialog = null)
                    }

                    delay(500)

                    sendEventOrShowNoInternet(eventToRetry)
                }
            }

            is HomeIntent.GoToNetworkSettings -> {
                noOfRetries = 0
                setState {
                    copy(activeDialog = null)
                }
                sendEvent(HomeEvent.LaunchNetworkSettings)
            }
        }
    }

    private fun getPublicStocks() = currentState().availableStocks.filter { it != StockUi.PRIVATE }

    private fun shouldTransactionConfirmStock(availableStocks: List<StockUi>): Boolean = availableStocks.size > 1
}
