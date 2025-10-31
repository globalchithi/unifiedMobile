package com.vaxcare.unifiedhub.core.common

object VaxJobName {
    const val INSERT_LOT_NUMBERS_JOB = "InsertLotNumbersJob"
}

object VaxJobArgument {
    /**
     * Key for the VaxJob identifier
     */
    const val JOB_NAME = "JobName"

    // InsertLotNumbers
    const val LOT_NUMBER = "LotNumber"
    const val PRODUCT_ID = "ProductId"
    const val EXPIRATION = "Expiration"
    const val ADD_SOURCE_ID = "AddSourceId"
}

/**
 * Events from the backend that will trigger VaxJobs to run from the JobSelector
 */
object FirebaseEventType {
    /**
     * Fires off the LocationJob
     */
    const val SYNC_LOCATION = "VaxHub.FirebaseEvents.SyncLocation"

    /**
     * Fires off the DiagnosticJob
     */
    const val SYNC_DIAGNOSTIC = "com.vaxcare.vaxhub.firebase.DIAGNOSTIC"

    /**
     * Fires off the OrderGroupChangedJob
     *
     * Requires a payload with OrderGroupChangedEvent json string
     */
    const val SYNC_GROUP_ORM = "VaxCare.Scheduler.Partner.Clinic.OrderGroupChangedEvent"

    /**
     * Fires off the AppointmentChangedJob
     *
     * Requires a payload with AppointmentChangedEvent json string
     */
    const val SYNC_APPOINTMENT =
        "VaxCare.Scheduler.Partner.Clinic.AppointmentChangedEvent"

    /**
     * Fires off the SimpleOnHandInventoryJob
     */
    const val INVENTORY_ADJUSTMENT_EVENT =
        "VaxApi.Cqrs.Command.CreateAdjustmentsCommandHandler+AdjustmentVaxHubEvent"

    const val SYNC_COUNTS = "VaxHub.FirebaseEvents.CountConfirmationEvent"

    const val SYNC_LOT_NUMBERS = "com.vaxcare.vaxhub.firebase.SYNC.LOT_NUMBERS"
}
