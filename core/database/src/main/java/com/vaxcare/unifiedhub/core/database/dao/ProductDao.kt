package com.vaxcare.unifiedhub.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.vaxcare.unifiedhub.core.database.model.enums.ProductStatus
import com.vaxcare.unifiedhub.core.database.model.inventory.product.AgeIndicationEntity
import com.vaxcare.unifiedhub.core.database.model.inventory.product.CptCvxCodeEntity
import com.vaxcare.unifiedhub.core.database.model.inventory.product.LegacyProductMappingEntity
import com.vaxcare.unifiedhub.core.database.model.inventory.product.NdcCodeEntity
import com.vaxcare.unifiedhub.core.database.model.inventory.product.PackageEntity
import com.vaxcare.unifiedhub.core.database.model.inventory.product.ProductEntity
import kotlinx.coroutines.flow.Flow

private const val VACCINE_CATEGORY_ID = 2

@Dao
abstract class ProductDao {
    @Transaction
    @Query("SELECT * FROM Product WHERE categoryId = $VACCINE_CATEGORY_ID ORDER BY displayName COLLATE NOCASE")
    abstract suspend fun getAllProductsAsync(): List<ProductEntity>

    @Transaction
    @Query("SELECT * FROM Product WHERE categoryId = $VACCINE_CATEGORY_ID ORDER BY displayName COLLATE NOCASE")
    abstract fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM Product WHERE id = :id LIMIT 1")
    abstract fun getProductById(id: Int): Flow<ProductEntity?>

    @Query(
        """
        SELECT * FROM Product
        WHERE id IN (SELECT productId FROM LotNumber WHERE lotNumber in (:lotNumbers))
    """
    )
    abstract fun getProductsByLotNumber(lotNumbers: List<String>): List<ProductEntity>

    @Transaction
    @Query("SELECT * FROM Product WHERE id IN (:productIds)")
    abstract suspend fun getProductsByIds(productIds: List<Int>): List<ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertProducts(products: List<ProductEntity>)

    // FIXME: Replace this method.
    @Deprecated(
        """
        Dao methods should perform simple database operations in the context of 
        a SINGLE entity.
    """
    )
    @Transaction
    @Insert
    suspend fun insert(
        products: List<ProductEntity>,
        packages: List<PackageEntity>,
        cptCvxCodes: List<CptCvxCodeEntity>,
        ndcCodes: List<NdcCodeEntity>,
        ageIndications: List<AgeIndicationEntity>
    ) {
        val fluStatuses = listOf(
            ProductStatus.DISABLED,
            ProductStatus.FLU_ENABLED,
            ProductStatus.HISTORICAL,
            ProductStatus.HISTORICAL_FLU
        )
        insertProducts(
            products.map {
                ProductEntity(
                    antigen = it.antigen,
                    categoryId = it.categoryId,
                    description = it.description,
                    displayName = if (it.status !in fluStatuses) {
                        it.displayName
                    } else {
                        it.prettyName
                            ?: it.displayName
                    },
                    id = it.id,
                    inventoryGroup = it.inventoryGroup,
                    lossFee = it.lossFee,
                    productNdc = it.productNdc,
                    routeCode = it.routeCode,
                    presentation = it.presentation,
                    purchaseOrderFee = it.purchaseOrderFee,
                    visDates = it.visDates,
                    status = it.status,
                    prettyName = it.prettyName
                )
            }
        )
        insertPackages(packages)
        insertCptCvxCodes(cptCvxCodes)
        insertNdcCodes(ndcCodes)
        deleteAgeIndicationsByProductIds(products.map { it.id })
        insertAgeIndications(ageIndications)
    }

    @Deprecated("This belongs in PackagesDao")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertPackages(packages: List<PackageEntity>)

    @Deprecated("This belongs in CpxCvxCodesDao")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertCptCvxCodes(cptCvxCodes: List<CptCvxCodeEntity>)

    @Deprecated("This belongs in NdcCodesDao")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertNdcCodes(ndcList: List<NdcCodeEntity>)

    @Deprecated("This belongs in AgeIndicationsDao")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertAgeIndications(ageIndications: List<AgeIndicationEntity>)

    @Deprecated("This belongs in AgeIndicationsDao")
    @Query("DELETE FROM AgeIndication WHERE productId in (:productIds)")
    protected abstract suspend fun deleteAgeIndicationsByProductIds(productIds: List<Int>)

    @Deprecated("This belongs in LegacyProductMappingDao")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertMapping(mapping: List<LegacyProductMappingEntity>)

    @Deprecated("This belongs in LegacyProductMappingDao")
    @Query("SELECT * FROM LegacyProductMapping")
    abstract suspend fun getAllMappings(): List<LegacyProductMappingEntity>

    /**
     * Gets the SalesProductId (CoreProductId) for the associated ProductId - edge-case for
     * for newly created offline lots and sending a Count with the offline created count
     */
    @Query("SELECT id FROM LegacyProductMapping WHERE epProductId = :productId LIMIT 1")
    abstract suspend fun getSalesProductId(productId: Int): Int?
}
