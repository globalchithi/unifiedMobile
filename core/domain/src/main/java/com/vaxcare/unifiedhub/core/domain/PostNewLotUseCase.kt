package com.vaxcare.unifiedhub.core.domain

import com.vaxcare.unifiedhub.core.common.VaxJobArgument
import com.vaxcare.unifiedhub.core.common.VaxJobName.INSERT_LOT_NUMBERS_JOB
import com.vaxcare.unifiedhub.core.data.repository.LotRepository
import com.vaxcare.unifiedhub.core.model.lot.LotNumberSource
import com.vaxcare.unifiedhub.library.vaxjob.provider.VaxJobProvider
import java.time.LocalDate
import javax.inject.Inject

class PostNewLotUseCase @Inject constructor(
    private val vaxJobProvider: VaxJobProvider,
    private val lotRepository: LotRepository
) {
    /**
     * Trigger an [INSERT_LOT_NUMBERS_JOB] with the provided Lot information.
     *
     * @param lotNumber
     * @param productId
     * @param expiration
     * @param source
     */
    suspend operator fun invoke(
        lotNumber: String,
        productId: Int?,
        expiration: LocalDate,
        source: LotNumberSource,
    ) {
        // Insert a new temporary lot immediately to allow screen to react
        lotRepository.insertTemporaryLot(
            expirationDate = expiration,
            lotNumber = lotNumber,
            productId = productId ?: -1,
            source = source.id
        )

        // Use VaxJob to post to the backend
        vaxJobProvider
            .withArguments(
                (VaxJobArgument.JOB_NAME to INSERT_LOT_NUMBERS_JOB),
                (VaxJobArgument.LOT_NUMBER to lotNumber),
                (VaxJobArgument.PRODUCT_ID to productId),
                (VaxJobArgument.EXPIRATION to expiration),
                (VaxJobArgument.ADD_SOURCE_ID to source.id)
            ).runJob()
    }
}
