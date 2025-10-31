package com.vaxcare.unifiedhub.feature.transactions.analytics

import com.vaxcare.unifiedhub.core.domain.analytics.ValidateScanAnalytics
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.interaction.model.metric.ExpiredProductPromptMetric
import com.vaxcare.unifiedhub.feature.transactions.ui.counts.interaction.model.metric.WrongProductPromptMetric
import com.vaxcare.unifiedhub.library.analytics.core.AnalyticsRepository
import com.vaxcare.unifiedhub.library.analytics.events.common.LotAddMetric
import javax.inject.Inject

class ValidateScanAnalyticsImpl @Inject constructor(
    private val repository: AnalyticsRepository
) : ValidateScanAnalytics {
    override suspend fun trackWrongProduct(ndc: String, displayedMessage: String) {
        repository.track(WrongProductPromptMetric(ndc, displayedMessage))
    }

    override suspend fun trackExpiredProduct(lotNumber: String, expiration: String) {
        repository.track(ExpiredProductPromptMetric(lotNumber, expiration))
    }

    override suspend fun trackLotAdded(
        screenSource: String,
        symbologyScanned: String,
        productSource: String,
        productId: Int,
        productName: String,
        ndc: String,
        lotNumber: String,
        expirationDate: String,
        rawBarcodeData: String
    ) {
        repository.track(
            LotAddMetric(
                screenSource = screenSource,
                symbologyScanned = symbologyScanned,
                productSource = productSource,
                productId = productId,
                productName = productName,
                ndc = ndc,
                lotNumber = lotNumber,
                expirationDate = expirationDate,
                rawBarcodeData = rawBarcodeData
            )
        )
    }
}
