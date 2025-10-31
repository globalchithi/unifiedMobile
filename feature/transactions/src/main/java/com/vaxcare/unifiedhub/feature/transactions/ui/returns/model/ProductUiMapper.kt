package com.vaxcare.unifiedhub.feature.transactions.ui.returns.model

import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.data.repository.ProductRepository
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.session.ReturnsSession
import javax.inject.Inject

class ProductUiMapper @Inject constructor(
    private val lotRepository: LotRepository,
    private val productRepository: ProductRepository,
) {
    suspend fun sessionToUi(lotState: Map<String, ReturnsSession.LotState>): List<ProductUi> {
        val lotNumbers = lotState
            .filterNot {
                // exclude any lots in the 'deleted' state
                it.value.isDeleted
            }.keys
            .toList()
        val products = productRepository.getProductsByLotNumber(lotNumbers)
        val lots = lotRepository.getLotsByNumber(lotNumbers)

        return products
            .map { product ->
                val productLots = lots.filter { it.productId == product.id }
                val productLotState = productLots.mapNotNull {
                    lotState[it.lotNumber] ?: return@mapNotNull null
                }

                ProductUi(
                    id = product.id,
                    antigen = product.antigen,
                    prettyName = product.prettyName ?: product.displayName,
                    quantity = productLotState.sumOf { it.count },
                    lotNumber = productLots.first().lotNumber,
                    noOfLots = productLots.size,
                    presentation = product.presentation,
                )
            }
    }
}
