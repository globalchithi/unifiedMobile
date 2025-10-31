package com.vaxcare.unifiedhub.feature.transactions.returns

import com.vaxcare.unifiedhub.core.model.PickupAvailability
import com.vaxcare.unifiedhub.core.model.product.Presentation
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.model.ProductLotUi
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.model.ProductUi
import java.time.LocalDate
import java.time.LocalTime

object ReturnsSharedTestData {
    val mockPickupAvailability = PickupAvailability(
        date = LocalDate.of(2025, 11, 11),
        startTime = LocalTime.of(8, 0),
        endTime = LocalTime.of(16, 0),
    )
    val mockProductsUi = ProductUi.Sample
    val mockProductLotsUi = listOf(
        ProductLotUi(
            antigen = "Antigen 1",
            prettyName = "Pretty Name 1",
            quantity = 5,
            lotNumber = "VXC01",
            expiration = "06/06/2025",
            presentation = Presentation.PREFILLED_SYRINGE
        ),
        ProductLotUi(
            antigen = "Antigen 2",
            prettyName = "Pretty Name 2",
            quantity = 10,
            lotNumber = "VXC02",
            expiration = "06/06/2025",
            presentation = Presentation.NASAL_SYRINGE
        ),
        ProductLotUi(
            antigen = "Antigen 3",
            prettyName = "Pretty Name 3",
            quantity = 15,
            lotNumber = "VXC03",
            expiration = "06/06/2025",
            presentation = Presentation.NASAL_SYRINGE,
            isDeleted = true
        ),
    )
}
