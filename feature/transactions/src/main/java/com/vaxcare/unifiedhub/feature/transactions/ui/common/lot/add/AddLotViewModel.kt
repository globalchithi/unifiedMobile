package com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.vaxcare.unifiedhub.core.common.dispatcher.DispatcherProvider
import com.vaxcare.unifiedhub.core.data.repository.ProductRepository
import com.vaxcare.unifiedhub.core.domain.PostNewLotUseCase
import com.vaxcare.unifiedhub.core.model.lot.LotNumberSource
import com.vaxcare.unifiedhub.core.model.product.Product
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModel
import com.vaxcare.unifiedhub.feature.transactions.navigation.AddLotRoute
import com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.add.model.LotForm
import com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.add.model.PresentationUI
import com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.add.model.ProductUI
import com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.add.model.mapper.PresentationUiMapper
import com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.add.model.mapper.ProductUIMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddLotViewModel @Inject constructor(
    private val dispatchers: DispatcherProvider,
    private val productRepository: ProductRepository,
    private val postNewLot: PostNewLotUseCase,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<AddLotState, AddLotEvent, AddLotIntent>(initialState = AddLotState()) {
    private var allProducts: List<Product> = emptyList()

    private val routeArgs: AddLotRoute = savedStateHandle.toRoute()
    private val preSelectedLotNumber: String = routeArgs.lotNumber
    private val preSelectedProductId: Int? = routeArgs.productId

    override fun start() {
        updateForm { copy(lotNumber = preSelectedLotNumber) }

        viewModelScope.launch(dispatchers.io) {
            productRepository
                .getAllProducts()
                .onStart { startLoading() }
                .catch { e ->
                    Timber.e(e)
                }.collect { products ->
                    allProducts = products

                    preSelectedProductId?.let { preSelectedProductId ->
                        products
                            .firstOrNull { it.id == preSelectedProductId }
                            ?.let { preselectedProduct ->
                                val presentation = products
                                    .filter { it.id == preselectedProduct.id }
                                    .map { it.presentation }
                                    .singleOrNull()

                                updateForm {
                                    copy(
                                        antigen = preselectedProduct.antigen,
                                        product = ProductUIMapper.map(preselectedProduct),
                                        presentation = presentation?.let {
                                            PresentationUiMapper.map(
                                                it
                                            )
                                        },
                                        isPreSelected = true
                                    )
                                }
                            }
                    }

                    stopLoading()
                }
        }
    }

    override fun handleIntent(intent: AddLotIntent) {
        when (intent) {
            AddLotIntent.OpenAntigenPicker -> openAntigenPicker()
            AddLotIntent.OpenProductPicker -> openProductPicker()
            AddLotIntent.OpenPresentationPicker -> openPresentationPicker()
            AddLotIntent.OpenExpirationPicker -> openExpirationPicker()

            is AddLotIntent.AntigenPicked -> onAntigenPicked(intent.antigen)
            is AddLotIntent.ProductPicked -> {
                onProductPicked(intent.product)
            }

            is AddLotIntent.PresentationPicked -> {
                onPresentationPicked(intent.presentation)
            }

            is AddLotIntent.ExpirationPicked -> onExpirationPicked(intent.expirationDate)

            AddLotIntent.CreateLot -> createLot()
            AddLotIntent.CloseDialog -> clearDialog()
            AddLotIntent.Close -> sendEvent(AddLotEvent.Close)
        }
    }

    private fun onAntigenPicked(antigen: String) {
        updateForm {
            copy(
                antigen = antigen,
                product = null,
                presentation = null,
                expirationDate = null
            )
        }
        clearDialog()
        openProductPicker()
    }

    private fun onProductPicked(product: ProductUI) {
        updateForm { copy(product = product, presentation = null, expirationDate = null) }
        clearDialog()
        openPresentationPicker()
    }

    private fun onPresentationPicked(presentation: PresentationUI) {
        updateForm {
            copy(presentation = presentation, expirationDate = null)
        }
        clearDialog()
        openExpirationPicker()
    }

    private fun onExpirationPicked(expirationDate: LocalDate) {
        updateForm { copy(expirationDate = expirationDate) }
        clearDialog()
    }

    private fun openAntigenPicker() {
        if (allProducts.isEmpty()) return

        val antigens = allProducts
            .map { it.antigen }
            .distinct()
            .sorted()

        setState { copy(activeDialog = AddLotDialog.AntigenPicker(antigens)) }
    }

    private fun openProductPicker() {
        currentState().form.antigen?.let { antigen ->
            val products = allProducts
                .filter { it.antigen == antigen }
                .sortedBy { it.prettyName }
                .distinctBy { (it.prettyName ?: it.displayName) }
                .map { ProductUIMapper.map(it) }

            setState { copy(activeDialog = AddLotDialog.ProductPicker(products)) }
        }
    }

    private fun openPresentationPicker() {
        currentState().form.product?.let { product ->
            val presentations = allProducts
                .filter { (it.prettyName ?: it.displayName) == product.name }
                .map { it.presentation }
                .distinct()
                .sorted()
                .map { PresentationUiMapper.map(it) }

            setState { copy(activeDialog = AddLotDialog.PresentationPicker(presentations)) }
        }
    }

    private fun openExpirationPicker() {
        val expirationDate = currentState().form.expirationDate ?: LocalDate.now()
        setState { copy(activeDialog = AddLotDialog.ExpirationPicker(expirationDate)) }
    }

    private fun createLot() =
        viewModelScope.launch(dispatchers.io) {
            val form = currentState().form
            if (!form.isComplete) return@launch
            startLoading()
            with(currentState().form) {
                postNewLot(
                    lotNumber = lotNumber ?: run {
                        Timber.e("Impossible scenario: LotNumber is null on a completed form")
                        return@launch
                    },
                    productId = allProducts.getProductIdFromAntigenNameAndPresentation(
                        antigen = antigen,
                        name = product?.name,
                        presentationId = presentation?.id
                    ),
                    expiration = expirationDate ?: LocalDate.MIN,
                    source = LotNumberSource.ManualEntry
                )
                sendEvent(AddLotEvent.ConfirmLot(lotNumber))
                stopLoading()
            }
        }

    private fun startLoading() {
        setState { copy(loading = true) }
    }

    private fun stopLoading() {
        setState { copy(loading = false) }
    }

    private fun clearDialog() = setState { copy(activeDialog = null) }

    private inline fun updateForm(crossinline block: LotForm.() -> LotForm) {
        setState {
            val newForm = form.block()
            copy(form = newForm)
        }
    }

    /**
     * Grabs the correct ProductId based on the information from the form
     */
    private fun List<Product>.getProductIdFromAntigenNameAndPresentation(
        antigen: String?,
        name: String?,
        presentationId: Int?
    ) = first {
        (it.prettyName ?: it.displayName) == name &&
            it.antigen == antigen &&
            it.presentation.ordinal == presentationId
    }.id
}
