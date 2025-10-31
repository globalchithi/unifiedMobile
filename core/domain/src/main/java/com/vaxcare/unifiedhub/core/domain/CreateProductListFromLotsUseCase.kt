package com.vaxcare.unifiedhub.core.domain

import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.data.repository.PackageRepository
import com.vaxcare.unifiedhub.core.data.repository.ProductRepository
import com.vaxcare.unifiedhub.core.model.product.Package
import com.vaxcare.unifiedhub.core.model.product.Product
import javax.inject.Inject

class CreateProductListFromLotsUseCase @Inject constructor(
    private val lotRepository: LotRepository,
    private val productRepository: ProductRepository,
    private val packageRepository: PackageRepository
) {
    suspend operator fun <T, R> invoke(
        lotNumbers: List<String>,
        inventoryByLotNumber: Map<String, T>,
        mapper: (Product, List<T>, Package) -> R
    ): List<R> {
        if (lotNumbers.isEmpty()) return emptyList<R>()
        val lots = lotRepository.getLotsByNumber(lotNumbers)
        val lotsByProductId = lots.groupBy { it.productId }
        val allProductIds = lotsByProductId.keys.toList()

        val products = productRepository.getProductsByIds(allProductIds)

        return products.mapNotNull { product ->
            val productLots = lotsByProductId[product.id] ?: emptyList()
            val productInventory = productLots.mapNotNull { lot ->
                inventoryByLotNumber[lot.lotNumber]
            }

            if (productInventory.isEmpty()) return@mapNotNull null

            val firstPackage = packageRepository.getOneByProductId(product.id)

            mapper(product, productInventory, firstPackage)
        }
    }
}
