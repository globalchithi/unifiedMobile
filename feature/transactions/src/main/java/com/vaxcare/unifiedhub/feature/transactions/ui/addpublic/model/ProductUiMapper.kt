package com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.model

import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.data.repository.ProductRepository
import com.vaxcare.unifiedhub.feature.transactions.ui.addpublic.session.AddPublicSession
import javax.inject.Inject

class ProductUiMapper @Inject constructor(
    private val lotRepository: LotRepository,
    private val productRepository: ProductRepository
) {
    suspend fun sessionToUi(
        lotState: Map<String, AddPublicSession.LotState>,
        productState: Map<Int, AddPublicSession.ProductState>,
    ): List<ProductUi> {
        val lotNumbers = lotState
            .filterNot {
                // exclude any lots in the 'deleted' state
                it.value.isDeleted
            }.keys
            .toList()
        val lots = lotNumbers.let { lotRepository.getLotsByNumber(it) }
        val products = productRepository
            .getProductsByLotNumber(lotNumbers)
            .filter {
                // exclude any products in the 'deleted' state
                // this, by extension, will exclude the associated lots

                // NOTE::Optimization: it is inefficient to fetch data for a product and its lots,
                //  only to immediately filter them out based on prior knowledge of a deleted state.
                productState[it.id]?.isDeleted != true
            }

        return products
            .map { product ->
                val productLots = lots.filter { it.productId == product.id }

                val inventory = productLots.mapNotNull {
                    val lotState = lotState[it.lotNumber] ?: return@mapNotNull null
                    AddedLotInventoryUi(
                        lotNumber = it.lotNumber,
                        count = lotState.count,
                        isDeleted = lotState.isDeleted,
                        expiration = ""
                    )
                }
                ProductUi(
                    id = product.id,
                    inventory = inventory,
                    antigen = product.antigen,
                    prettyName = product.prettyName ?: product.displayName,
                    cartonCount = 0,
                    presentation = product.presentation
                )
            }.sortedBy {
                it.antigen
            }
    }
}
