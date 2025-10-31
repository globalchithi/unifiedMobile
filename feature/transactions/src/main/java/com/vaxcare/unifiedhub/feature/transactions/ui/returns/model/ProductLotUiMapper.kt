package com.vaxcare.unifiedhub.feature.transactions.ui.returns.model

import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.data.repository.ProductRepository
import com.vaxcare.unifiedhub.core.domain.SafeExpirationDateUseCase
import com.vaxcare.unifiedhub.feature.transactions.ui.returns.session.ReturnsSession
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ofPattern
import javax.inject.Inject

class ProductLotUiMapper @Inject constructor(
    private val lotRepository: LotRepository,
    private val productRepository: ProductRepository,
    private val safeExpirationDate: SafeExpirationDateUseCase
) {
    suspend fun sessionToUi(lotState: Map<String, ReturnsSession.LotState>): List<ProductLotUi> {
        val lotNumbers = lotState.keys.toList()
        val products = productRepository
            .getProductsByLotNumber(lotNumbers)
            .associateBy { it.id }
        val lots = lotRepository
            .getLotsByNumber(lotNumbers)
            .associateBy { it.lotNumber }

        return lotState.mapNotNull { (lotNumber, state) ->
            val lot = lots[lotNumber] ?: return@mapNotNull null
            val product = products[lot.productId] ?: return@mapNotNull null
            val (expiration, isExpired) = safeExpirationDate(lot.expiration).let {
                val isExpired = it.isBefore(LocalDateTime.now())
                val dateFmt = if (isExpired) {
                    "MM/yyyy"
                } else {
                    "MM/dd/yyy"
                }.let(DateTimeFormatter::ofPattern)

                it.format(dateFmt) to isExpired
            }

            ProductLotUi(
                antigen = product.antigen,
                prettyName = product.prettyName ?: product.displayName,
                quantity = state.count,
                lotNumber = lotNumber,
                expiration = expiration,
                presentation = product.presentation,
                isExpired = isExpired,
                isDeleted = state.isDeleted,
            )
        }
    }
}
