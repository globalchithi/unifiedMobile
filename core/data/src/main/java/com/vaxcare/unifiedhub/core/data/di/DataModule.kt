package com.vaxcare.unifiedhub.core.data.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.EnumJsonAdapter
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.vaxcare.unifiedhub.core.data.adapter.TimeAdapter
import com.vaxcare.unifiedhub.core.data.adapter.TypeAdapters
import com.vaxcare.unifiedhub.core.data.adapter.VaccineCountTypeAdapter
import com.vaxcare.unifiedhub.core.database.model.enums.PresentationDTO
import com.vaxcare.unifiedhub.core.database.model.enums.RouteCode
import com.vaxcare.unifiedhub.core.network.model.ProductPresentationDTO
import com.vaxcare.unifiedhub.core.network.model.RouteCodeDTO
import com.vaxcare.unifiedhub.core.network.serializer.BooleanAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.Date
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideMoshi(): Moshi =
        Moshi
            .Builder()
            .add(
                PresentationDTO::class.java,
                EnumJsonAdapter
                    .create(PresentationDTO::class.java)
                    .withUnknownFallback(PresentationDTO.UNKNOWN)
            ).add(
                ProductPresentationDTO::class.java,
                EnumJsonAdapter
                    .create(ProductPresentationDTO::class.java)
                    .withUnknownFallback(ProductPresentationDTO.UNKNOWN)
            ).add(
                RouteCode::class.java,
                EnumJsonAdapter
                    .create(RouteCode::class.java)
                    .withUnknownFallback(RouteCode.UNKNOWN)
            ).add(
                RouteCodeDTO::class.java,
                EnumJsonAdapter
                    .create(RouteCodeDTO::class.java)
                    .withUnknownFallback(RouteCodeDTO.UNKNOWN)
            ).add(KotlinJsonAdapterFactory())
            .add(TypeAdapters())
            .add(TimeAdapter())
            .add(BooleanAdapter())
            .add(VaccineCountTypeAdapter())
            .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
            .build()

    @Provides
    @Singleton
    fun providesMoshiConverterFactory(moshi: Moshi): MoshiConverterFactory = MoshiConverterFactory.create(moshi)
}
