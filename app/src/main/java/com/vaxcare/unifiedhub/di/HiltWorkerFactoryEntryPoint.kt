package com.vaxcare.unifiedhub.di

import androidx.hilt.work.HiltWorkerFactory
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt EntryPoint to access the HiltWorkerFactory from non-Hilt-managed classes,
 * like our custom test rules.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface HiltWorkerFactoryEntryPoint {
    fun getHiltWorkerFactory(): HiltWorkerFactory
}
