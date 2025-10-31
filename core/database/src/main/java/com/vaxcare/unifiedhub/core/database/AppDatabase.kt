package com.vaxcare.unifiedhub.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vaxcare.unifiedhub.core.database.converter.BasicTypeConverters
import com.vaxcare.unifiedhub.core.database.converter.InventorySourceEnumTypeConverters
import com.vaxcare.unifiedhub.core.database.converter.ProductEnumTypeConverters
import com.vaxcare.unifiedhub.core.database.dao.ClinicDao
import com.vaxcare.unifiedhub.core.database.dao.CountDao
import com.vaxcare.unifiedhub.core.database.dao.LocationDao
import com.vaxcare.unifiedhub.core.database.dao.LotConfirmationDao
import com.vaxcare.unifiedhub.core.database.dao.LotInventoryDao
import com.vaxcare.unifiedhub.core.database.dao.LotNumberDao
import com.vaxcare.unifiedhub.core.database.dao.NdcCodeDao
import com.vaxcare.unifiedhub.core.database.dao.OfflineRequestDao
import com.vaxcare.unifiedhub.core.database.dao.PackageDao
import com.vaxcare.unifiedhub.core.database.dao.ProductDao
import com.vaxcare.unifiedhub.core.database.dao.TransactionConfirmationDao
import com.vaxcare.unifiedhub.core.database.dao.UserDao
import com.vaxcare.unifiedhub.core.database.dao.WrongProductNdcDao
import com.vaxcare.unifiedhub.core.database.model.FeatureFlagEntity
import com.vaxcare.unifiedhub.core.database.model.LocationEntity
import com.vaxcare.unifiedhub.core.database.model.OfflineRequestEntity
import com.vaxcare.unifiedhub.core.database.model.UserEntity
import com.vaxcare.unifiedhub.core.database.model.inventory.ClinicEntity
import com.vaxcare.unifiedhub.core.database.model.inventory.WrongProductNdcEntity
import com.vaxcare.unifiedhub.core.database.model.inventory.count.CountEntity
import com.vaxcare.unifiedhub.core.database.model.inventory.count.CountEntryEntity
import com.vaxcare.unifiedhub.core.database.model.inventory.lot.LotConfirmationEntity
import com.vaxcare.unifiedhub.core.database.model.inventory.lot.LotInventoryEntity
import com.vaxcare.unifiedhub.core.database.model.inventory.lot.LotNumberEntity
import com.vaxcare.unifiedhub.core.database.model.inventory.lot.TransactionConfirmationEntity
import com.vaxcare.unifiedhub.core.database.model.inventory.product.AgeIndicationEntity
import com.vaxcare.unifiedhub.core.database.model.inventory.product.CptCvxCodeEntity
import com.vaxcare.unifiedhub.core.database.model.inventory.product.LegacyProductMappingEntity
import com.vaxcare.unifiedhub.core.database.model.inventory.product.NdcCodeEntity
import com.vaxcare.unifiedhub.core.database.model.inventory.product.PackageEntity
import com.vaxcare.unifiedhub.core.database.model.inventory.product.ProductEntity

@Database(
    entities = [
        AgeIndicationEntity::class,
        ClinicEntity::class,
        CountEntity::class,
        CountEntryEntity::class,
        CptCvxCodeEntity::class,
        FeatureFlagEntity::class,
        LegacyProductMappingEntity::class,
        LocationEntity::class,
        LotConfirmationEntity::class,
        LotInventoryEntity::class,
        LotNumberEntity::class,
        NdcCodeEntity::class,
        OfflineRequestEntity::class,
        PackageEntity::class,
        ProductEntity::class,
        TransactionConfirmationEntity::class,
        UserEntity::class,
        WrongProductNdcEntity::class
    ],
    version = 5
)
@TypeConverters(
    BasicTypeConverters::class,
    InventorySourceEnumTypeConverters::class,
    ProductEnumTypeConverters::class
)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "UnifiedHub"

        fun initialize(context: Context): AppDatabase {
            // encryption?
            return Room
                .databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).apply {
                    fallbackToDestructiveMigration()
                }.build()
        }
    }

    abstract fun clinicDao(): ClinicDao

    abstract fun countDao(): CountDao

    abstract fun locationDao(): LocationDao

    abstract fun lotConfirmationDao(): LotConfirmationDao

    abstract fun lotInventoryDao(): LotInventoryDao

    abstract fun lotNumberDao(): LotNumberDao

    abstract fun ndcCodeDao(): NdcCodeDao

    abstract fun offlineRequestDao(): OfflineRequestDao

    abstract fun packageDao(): PackageDao

    abstract fun productDao(): ProductDao

    abstract fun transactionConfirmationDao(): TransactionConfirmationDao

    abstract fun userDao(): UserDao

    abstract fun wrongProductNdcDao(): WrongProductNdcDao
}
