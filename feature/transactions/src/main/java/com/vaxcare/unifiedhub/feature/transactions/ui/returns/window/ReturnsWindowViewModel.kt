package com.vaxcare.unifiedhub.feature.transactions.ui.returns.window

import androidx.lifecycle.viewModelScope
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.data.repository.ReturnRepository
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModel
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.session.ReturnsSession
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.window.ReturnsWindowState.Companion.MAX_SHIPPING_LABEL_QUANTITY
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.window.ReturnsWindowState.Companion.MIN_SHIPPING_LABEL_QUANTITY
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.min

@HiltViewModel(assistedFactory = ReturnsWindowViewModel.Factory::class)
class ReturnsWindowViewModel @AssistedInject constructor(
    @Assisted private val session: ReturnsSession,
    private val repository: ReturnRepository,
    private val dispatchers: DispatcherProvider
) : BaseViewModel<ReturnsWindowState, ReturnsWindowEvent, ReturnsWindowIntent>(
        ReturnsWindowState()
    ) {
    @AssistedFactory
    interface Factory {
        fun create(session: ReturnsSession): ReturnsWindowViewModel
    }

    private lateinit var pageRange: IntRange

    override fun start() {
        fetchPickupAvailability()
    }

    private fun fetchPickupAvailability() {
        viewModelScope.launch(dispatchers.io) {
            setState { copy(loading = true) }

            try {
                val pickupAvailabilities = repository.getPickupAvailability()

                if (pickupAvailabilities.isEmpty()) {
                    setState { copy(loading = false, availablePickups = emptyList()) }
                    sendEvent(
                        event = ReturnsWindowEvent.NavigateToSummary(noOfLabels = null)
                    )
                    // TODO: sendEvent(ReturnsWindowEvent.ShowNoAvailabilityMessage)?
                } else {
                    val lastPage = (pickupAvailabilities.size) / ReturnsWindowState.PAGE_SIZE +
                        min(pickupAvailabilities.size % ReturnsWindowState.PAGE_SIZE, 1) - 1
                    pageRange = (0..lastPage)
                    setState {
                        copy(
                            loading = false,
                            availablePickups = pickupAvailabilities
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
                setState { copy(loading = false, activeDialog = ReturnsWindowDialog.Retry) }
            }
        }
    }

    override fun handleIntent(intent: ReturnsWindowIntent) {
        when (intent) {
            ReturnsWindowIntent.Confirm -> {
                with(currentState()) {
                    if (canConfirm) {
                        session.pickup = availablePickups[selectedIndex!!]
                        sendEvent(
                            ReturnsWindowEvent.NavigateToSummary(
                                noOfLabels = shippingLabels,
                            )
                        )
                    }
                }
            }

            ReturnsWindowIntent.CloseDialog -> setState { copy(activeDialog = null) }

            is ReturnsWindowIntent.DecrementLabelQuantity -> {
                if (currentState().shippingLabels <= MIN_SHIPPING_LABEL_QUANTITY) return

                setState { copy(shippingLabels = shippingLabels.dec()) }
            }

            ReturnsWindowIntent.EditLabelQuantity -> setState {
                copy(activeDialog = ReturnsWindowDialog.EditShippingLabelQuantity)
            }

            ReturnsWindowIntent.GetNextAvailablePickUps -> {
                val nextIndex = (currentState().pageIndex + 1).takeIf {
                    pageRange.contains(it)
                } ?: pageRange.start
                setState {
                    copy(pageIndex = nextIndex)
                }
            }

            ReturnsWindowIntent.GetPreviousAvailablePickUps -> {
                val prevIndex = (currentState().pageIndex - 1).takeIf {
                    pageRange.contains(it)
                } ?: pageRange.endInclusive
                setState {
                    copy(pageIndex = prevIndex)
                }
            }

            ReturnsWindowIntent.NavigateBack -> sendEvent(ReturnsWindowEvent.NavigateBack)
            is ReturnsWindowIntent.IncrementLabelQuantity -> {
                if (currentState().shippingLabels >= MAX_SHIPPING_LABEL_QUANTITY) return

                setState {
                    copy(shippingLabels = shippingLabels.inc())
                }
            }

            is ReturnsWindowIntent.SelectPickUp -> {
                setState {
                    copy(selectedIndex = intent.index)
                }
            }

            is ReturnsWindowIntent.SetLabelQuantity -> {
                if (intent.quantity in MIN_SHIPPING_LABEL_QUANTITY..MAX_SHIPPING_LABEL_QUANTITY) {
                    setState {
                        copy(shippingLabels = intent.quantity, activeDialog = null)
                    }
                }
            }

            ReturnsWindowIntent.NoInternetConnectionIntent.Cancel -> {
                setState { copy(activeDialog = null) }
                sendEvent(ReturnsWindowEvent.NavigateBack)
            }

            ReturnsWindowIntent.NoInternetConnectionIntent.TryAgain -> {
                setState { copy(activeDialog = null) }
                fetchPickupAvailability()
            }
        }
    }
}
