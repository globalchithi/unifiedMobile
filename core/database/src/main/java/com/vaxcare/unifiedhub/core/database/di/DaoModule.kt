package com.vaxcare.unifiedhub.core.database.di

import com.vaxcare.unifiedhub.core.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {
    @Provides
    fun provideClinicDao(appDatabase: AppDatabase) = appDatabase.clinicDao()

    @Provides
    fun provideCountDao(appDatabase: AppDatabase) = appDatabase.countDao()

    @Provides
    fun provideLocationDao(appDatabase: AppDatabase) = appDatabase.locationDao()

    @Provides
    fun provideLotConfirmationDao(appDatabase: AppDatabase) = appDatabase.lotConfirmationDao()

    @Provides
    fun provideLotInventoryDao(appDatabase: AppDatabase) = appDatabase.lotInventoryDao()

    @Provides
    fun provideLotNumberDao(appDatabase: AppDatabase) = appDatabase.lotNumberDao()

    @Provides
    fun provideNdcCodeDao(appDatabase: AppDatabase) = appDatabase.ndcCodeDao()

    @Provides
    fun provideOfflineRequestDao(appDatabase: AppDatabase) = appDatabase.offlineRequestDao()

    @Provides
    fun providePackageDao(appDatabase: AppDatabase) = appDatabase.packageDao()

    @Provides
    fun provideProductDao(appDatabase: AppDatabase) = appDatabase.productDao()

    @Provides
    fun provideTransactionConfirmationDao(appDatabase: AppDatabase) = appDatabase.transactionConfirmationDao()

    @Provides
    fun provideUserDao(appDatabase: AppDatabase) = appDatabase.userDao()

    @Provides
    fun provideWrongProductNdcDao(appDatabase: AppDatabase) = appDatabase.wrongProductNdcDao()
}
