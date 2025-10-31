package com.vaxcare.unifiedhub.feature.home.ui.home

import com.vaxcare.unifiedhub.core.ui.model.StockUi
import com.vaxcare.unifiedhub.feature.home.ui.home.model.Notification

object HomeSampleData {
    val Default = HomeState(
        activeStock = StockUi.STATE,
        availableStocks = StockUi.entries,
        clinicName = "Sample Clinic Sample Clinic Sample Clinic Sample Clinic Sample Clinic ",
        partnerName = "Sample Partner Sample Partner Sample Partner Sample Partner ",
        notifications = setOf(
            Notification.OverdueCount(3),
            Notification.ExpiredDoses(10),
            Notification.AppUpdate
        )
    )
}
