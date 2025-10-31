package com.vaxcare.unifiedhub.core.data.di

import com.vaxcare.unifiedhub.core.data.repository.ClinicRepository
import com.vaxcare.unifiedhub.core.data.repository.ClinicRepositoryImpl
import com.vaxcare.unifiedhub.core.data.repository.ConfigRepository
import com.vaxcare.unifiedhub.core.data.repository.ConfigRepositoryImpl
import com.vaxcare.unifiedhub.core.data.repository.LocationRepository
import com.vaxcare.unifiedhub.core.data.repository.LocationRepositoryImpl
import com.vaxcare.unifiedhub.core.data.repository.LotInventoryRepository
import com.vaxcare.unifiedhub.core.data.repository.LotInventoryRepositoryImpl
import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.data.repository.LotRepositoryImpl
import com.vaxcare.unifiedhub.core.data.repository.OfflineRequestRepository
import com.vaxcare.unifiedhub.core.data.repository.OfflineRequestRepositoryImpl
import com.vaxcare.unifiedhub.core.data.repository.ProductRepository
import com.vaxcare.unifiedhub.core.data.repository.ProductRepositoryImpl
import com.vaxcare.unifiedhub.core.data.repository.ReturnRepository
import com.vaxcare.unifiedhub.core.data.repository.ReturnRepositoryImpl
import com.vaxcare.unifiedhub.core.data.repository.UserRepository
import com.vaxcare.unifiedhub.core.data.repository.UserRepositoryImpl
import com.vaxcare.unifiedhub.core.data.repository.WrongProductNdcRepository
import com.vaxcare.unifiedhub.core.data.repository.WrongProductNdcRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindClinicRepository(clinicRepositoryImpl: ClinicRepositoryImpl): ClinicRepository

    @Binds
    abstract fun bindLocationRepository(locationRepositoryImpl: LocationRepositoryImpl): LocationRepository

    @Binds
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindConfigRepository(configRepositoryImpl: ConfigRepositoryImpl): ConfigRepository

    @Binds
    abstract fun bindLotRepository(lotRepositoryImpl: LotRepositoryImpl): LotRepository

    @Binds
    abstract fun bindLotInventoryRepository(
        lotInventoryRepositoryImpl: LotInventoryRepositoryImpl
    ): LotInventoryRepository

    @Binds
    abstract fun bindProductRepository(productRepositoryImpl: ProductRepositoryImpl): ProductRepository

    @Binds
    abstract fun bindOfflineRequestRepository(
        offlineRequestRepositoryImpl: OfflineRequestRepositoryImpl
    ): OfflineRequestRepository

    @Binds
    abstract fun bindWrongProductNdcRepository(
        wrongProductNdcRepositoryImpl: WrongProductNdcRepositoryImpl
    ): WrongProductNdcRepository

    @Binds
    abstract fun bindReturnRepository(returnRepositoryImpl: ReturnRepositoryImpl): ReturnRepository
}
