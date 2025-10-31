package com.vaxcare.unifiedhub.core.data.repository

import androidx.annotation.WorkerThread
import com.vaxcare.unifiedhub.core.data.mapper.ProductMapper
import com.vaxcare.unifiedhub.core.database.dao.ProductDao
import com.vaxcare.unifiedhub.core.database.model.enums.Gender
import com.vaxcare.unifiedhub.core.database.model.inventory.product.AgeIndicationEntity
import com.vaxcare.unifiedhub.core.database.model.inventory.product.AgeWarningEntity
import com.vaxcare.unifiedhub.core.database.model.inventory.product.CptCvxCodeEntity
import com.vaxcare.unifiedhub.core.database.model.inventory.product.LegacyProductMappingEntity
import com.vaxcare.unifiedhub.core.database.model.inventory.product.NdcCodeEntity
import com.vaxcare.unifiedhub.core.database.model.inventory.product.PackageEntity
import com.vaxcare.unifiedhub.core.model.product.Product
import com.vaxcare.unifiedhub.core.network.api.InventoryApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ProductRepository {
    suspend fun getProductAsync(id: Int): Product?

    fun getProduct(id: Int): Flow<Product?>

    fun getProductsByLotNumber(lotNumbers: List<String>): List<Product>

    suspend fun getProductsByIds(productIds: List<Int>): List<Product>

    suspend fun syncProducts(isCalledByJob: Boolean = false)

    suspend fun updateProductMappings(isCalledByJob: Boolean)

    fun getAllProducts(): Flow<List<Product>>

    suspend fun getSalesProductIdFromProductId(productId: Int): Int?
}

class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val inventoryApi: InventoryApi,
    private val productMapper: ProductMapper
) : ProductRepository {
    override suspend fun getProductAsync(id: Int): Product? =
        productDao
            .getProductById(id)
            .first()
            .let(productMapper::entityToDomain)

    override fun getProduct(id: Int): Flow<Product?> =
        productDao
            .getProductById(id)
            .map { productMapper.entityToDomain(it) }

    override fun getProductsByLotNumber(lotNumbers: List<String>): List<Product> =
        productDao
            .getProductsByLotNumber(lotNumbers)
            .mapNotNull(productMapper::entityToDomain)

    override suspend fun getProductsByIds(productIds: List<Int>): List<Product> {
        if (productIds.isEmpty()) {
            return emptyList()
        }

        return productDao
            .getProductsByIds(productIds)
            .mapNotNull(productMapper::entityToDomain)
    }

    override suspend fun syncProducts(isCalledByJob: Boolean) {
        updateProductMappings(isCalledByJob)
        val productMapping =
            getProductMappingsToPrettyName(isCalledByJob).groupBy { it.epProductId }
        val products = inventoryApi
            .getProducts(isCalledByJob)
            .map {
                it.apply { prettyName = productMapping[it.id]?.firstOrNull()?.prettyName }
            }
        val productEntities = productMapper.networkToEntity(products)
        val packageEntities = products.flatMap {
            it.packages.map { pack ->
                PackageEntity(
                    description = pack.description,
                    id = pack.id,
                    itemCount = pack.itemCount,
                    productId = pack.productId,
                    salesProductId = pack.salesProductId
                )
            }
        }
        val ageIndications = products.flatMap { prod ->
            prod.ageIndications.map { age ->
                AgeIndicationEntity(
                    gender = Gender.fromInt(age.gender.ordinal),
                    id = age.id,
                    minAge = age.minAge,
                    maxAge = age.maxAge,
                    warning = age.warning?.let { AgeWarningEntity(it.title, it.message) },
                    productId = age.productId
                )
            }
        }
        val cptCvxCodes = products.flatMap {
            it.cptCvxCodes.map { code ->
                CptCvxCodeEntity(
                    cptCode = code.cptCode,
                    cvxCode = code.cvxCode,
                    isMedicare = code.isMedicare == 1,
                    productId = code.productId
                )
            }
        }
        val ndcCodes = products
            .map { pro ->
                pro.packages
                    .map { pack ->
                        pack.packageNdcs.map {
                            NdcCodeEntity(
                                ndcCode = it,
                                productId = pack.productId
                            )
                        }
                    }.flatten()
            }.flatten()
        productDao.insert(productEntities, packageEntities, cptCvxCodes, ndcCodes, ageIndications)
    }

    /**
     * Get the product mapping that we'll need to map adjustments to the product PrettyName
     */
    @WorkerThread
    private suspend fun getProductMappingsToPrettyName(isCalledByJob: Boolean) = productDao.getAllMappings()

    override suspend fun updateProductMappings(isCalledByJob: Boolean) {
        val mappings = inventoryApi.getProductMappings(isCalledByJob).map {
            LegacyProductMappingEntity(
                id = it.coreProductId,
                epProductName = it.epProductName,
                epPackageId = it.epPackageId,
                epProductId = it.epProductId,
                prettyName = it.prettyName,
                dosesInSeries = it.dosesInSeries
            )
        }

        productDao.insertMapping(mappings)
    }

    override fun getAllProducts(): Flow<List<Product>> =
        productDao.getAllProducts().map { it.mapNotNull(productMapper::entityToDomain) }

    override suspend fun getSalesProductIdFromProductId(productId: Int): Int? = productDao.getSalesProductId(productId)
}
