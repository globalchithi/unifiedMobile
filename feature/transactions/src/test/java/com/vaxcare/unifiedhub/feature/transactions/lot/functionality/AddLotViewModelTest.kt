@file:OptIn(ExperimentalCoroutinesApi::class)

package com.vaxcare.unifiedhub.feature.transactions.lot.functionality

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.vaxcare.unifiedhub.core.data.repository.ProductRepository
import com.vaxcare.unifiedhub.core.domain.PostNewLotUseCase
import com.vaxcare.unifiedhub.core.model.product.Presentation
import com.vaxcare.unifiedhub.core.model.product.Product
import com.vaxcare.unifiedhub.core.ui.arch.BaseViewModelTest
import com.vaxcare.unifiedhub.feature.transactions.navigation.AddLotRoute
import com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.add.AddLotDialog
import com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.add.AddLotEvent
import com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.add.AddLotIntent
import com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.add.AddLotState
import com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.add.AddLotViewModel
import com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.add.model.ProductUI
import com.vaxcare.unifiedhub.feature.transactions.ui.common.lot.add.model.mapper.PresentationUiMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class AddLotViewModelTest :
    BaseViewModelTest<AddLotState, AddLotEvent, AddLotIntent>() {
    private val productRepository: ProductRepository = mockk(relaxed = true)
    private val postNewLotUseCase: PostNewLotUseCase = mockk(relaxed = true)
    private val savedStateHandle = SavedStateHandle()

    private val hepB = Product(
        id = 1,
        inventoryGroup = "invGroup1",
        antigen = "Hep-B",
        displayName = "Engerix-B",
        presentation = Presentation.SINGLE_DOSE_VIAL,
        categoryId = 10,
        prettyName = "Engerix-B",
        lossFee = 1f
    )
    private val flu = Product(
        id = 2,
        inventoryGroup = "invGroup2",
        antigen = "Flu",
        displayName = "Fluzone",
        presentation = Presentation.PREFILLED_SYRINGE,
        categoryId = 11,
        prettyName = "Fluzone",
        lossFee = 2f
    )

    override lateinit var viewModel: AddLotViewModel

    @Before
    fun setUp() {
        every { productRepository.getAllProducts() } returns flowOf(listOf(hepB, flu))

        mockkStatic("androidx.navigation.SavedStateHandleKt")
        every { savedStateHandle.toRoute<AddLotRoute>() } returns
            AddLotRoute(lotNumber = "ABC1234")

        viewModel = AddLotViewModel(
            dispatchers = testDispatcherProvider,
            productRepository = productRepository,
            postNewLot = postNewLotUseCase,
            savedStateHandle = savedStateHandle
        )
    }

    @Test
    fun `GIVEN products loaded WHEN openAntigenPicker THEN dialog contains distinct sorted antigens`() =
        whenState {
            var state = awaitItem()
            while (state.loading) state = awaitItem()

            viewModel.handleIntent(AddLotIntent.OpenAntigenPicker)

            val expected = AddLotDialog.AntigenPicker(listOf("Flu", "Hep-B"))
            thenStateShouldBe(state.copy(activeDialog = expected))
        }

    @Test
    fun `GIVEN antigen selected WHEN antigenPicked THEN product presentation expiration reset and product dialog opened`() =
        runTest {
            viewModel.uiState.first { !it.loading }

            viewModel.handleIntent(AddLotIntent.AntigenPicked("Hep-B"))
            advanceUntilIdle()

            val state = viewModel.currentState()
            with(state.form) {
                assert(antigen == "Hep-B")
                assert(product == null && presentation == null && expirationDate == null)
            }
            assert(state.activeDialog is AddLotDialog.ProductPicker)
        }

    @Test
    fun `GIVEN a product selected WHEN presentationPicked intent comes THEN open expiration picker`() =
        runTest {
            viewModel.uiState.first { !it.loading }

            viewModel.handleIntent(
                AddLotIntent.AntigenPicked(hepB.antigen)
            )
            viewModel.handleIntent(
                AddLotIntent.ProductPicked(
                    ProductUI(
                        hepB.id,
                        hepB.prettyName ?: hepB.displayName
                    )
                )
            )
            advanceUntilIdle()

            viewModel.handleIntent(
                AddLotIntent.PresentationPicked(
                    PresentationUiMapper.map(hepB.presentation)
                )
            )
            advanceUntilIdle()

            val state = viewModel.currentState()
            assert(state.activeDialog is AddLotDialog.ExpirationPicker)
        }
}
