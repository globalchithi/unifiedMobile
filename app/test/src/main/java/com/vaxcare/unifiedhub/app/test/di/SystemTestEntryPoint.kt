// file: app/src/androidTest/java/com/vaxcare/unifiedhub/app/test/di/SystemTestEntryPoint.kt
package com.vaxcare.unifiedhub.app.test.di

import com.vaxcare.unifiedhub.library.vaxjob.service.FcmMessageHandler
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * An Hilt EntryPoint to allow test-only classes (like Robots) to access
 * dependencies from the SingletonComponent graph.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface SystemTestEntryPoint {
    fun getFcmMessageHandler(): FcmMessageHandler
}
