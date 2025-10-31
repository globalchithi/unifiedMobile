package com.vaxcare.unifiedhub.feature.admin.di

import com.vaxcare.unifiedhub.feature.admin.repository.AdminRepository
import com.vaxcare.unifiedhub.feature.admin.repository.AdminRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AdminModule {
    @Binds
    abstract fun bindAdminLoginRepository(adminRepositoryImpl: AdminRepositoryImpl): AdminRepository
}
