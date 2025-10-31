package com.vaxcare.unifiedhub.worker

import android.content.Context
import androidx.work.WorkManager
import com.squareup.moshi.Moshi
import com.vaxcare.unifiedhub.core.common.FirebaseEventType.INVENTORY_ADJUSTMENT_EVENT
import com.vaxcare.unifiedhub.core.common.FirebaseEventType.SYNC_APPOINTMENT
import com.vaxcare.unifiedhub.core.common.FirebaseEventType.SYNC_COUNTS
import com.vaxcare.unifiedhub.core.common.FirebaseEventType.SYNC_DIAGNOSTIC
import com.vaxcare.unifiedhub.core.common.FirebaseEventType.SYNC_GROUP_ORM
import com.vaxcare.unifiedhub.core.common.FirebaseEventType.SYNC_LOCATION
import com.vaxcare.unifiedhub.core.common.FirebaseEventType.SYNC_LOT_NUMBERS
import com.vaxcare.unifiedhub.core.datastore.datasource.LocationPreferenceDataSource
import com.vaxcare.unifiedhub.core.network.model.FirebaseClinicEventDTO
import com.vaxcare.unifiedhub.library.vaxjob.service.JobSelector
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber

class JobSelectorImpl(
    context: Context,
    private val moshi: Moshi,
    private val locationPrefs: LocationPreferenceDataSource
) : JobSelector {
    private val wm: WorkManager by lazy { WorkManager.getInstance(context.applicationContext) }

    /**
     * Queues up a worker based on the [eventType]
     *
     * @param eventType The type of event
     * @param payload Optional payload, currently used for Appointment Change Events
     */
    override suspend fun queueJob(eventType: String, payload: String?) {
        val sameClinic = if (payload.isNullOrBlank()) {
            true // If no payload, we assume is a global event, not specific to a clinic
        } else {
            try {
                moshi
                    .adapter(FirebaseClinicEventDTO::class.java)
                    .fromJson(payload)
                    ?.isSameClinic() != false
            } catch (e: Exception) {
                Timber.e(e, "Problem parsing FirebaseClinicEvent. Defaulting to same clinic.")
                true
            }
        }

        if (sameClinic) {
            when (eventType) {
                SYNC_LOCATION -> executeLocationJob()
                SYNC_GROUP_ORM -> executeOrderGroupChangedJob(payload)
                SYNC_APPOINTMENT -> executeAppointmentChangedJob(payload)
                INVENTORY_ADJUSTMENT_EVENT, SYNC_COUNTS -> executeLotInventoryJob()
                SYNC_DIAGNOSTIC -> executeDiagnosticJob()
                SYNC_LOT_NUMBERS -> executeLotNumbersJob()
            }
        }
    }

    private fun executeDiagnosticJob() = Unit

    private fun executeLocationJob() {
        OneTimeWorker.buildOneTimeUniqueWorker(
            wm = wm,
            parameters = OneTimeParams.Location
        )
    }

    private fun executeAppointmentChangedJob(payload: String?) = Unit

    private fun executeOrderGroupChangedJob(payload: String?) = Unit

    private fun executeLotInventoryJob() {
        OneTimeWorker.buildOneTimeUniqueWorker(
            wm = wm,
            parameters = OneTimeParams.LotInventoryJob
        )
    }

    private fun executeLotNumbersJob() {
        OneTimeWorker.buildOneTimeUniqueWorker(
            wm = wm,
            parameters = OneTimeParams.LotNumbersJob
        )
    }

    /**
     * If there's no temp clinic, the FCM event passes along the `parentClinicId` as `clinicId`,
     * and leaves `parentClinicId` as null.
     *
     * @return true if clinic ids match
     */
    private suspend fun FirebaseClinicEventDTO?.isSameClinic(): Boolean =
        this?.let { event ->

            val localParentClinicId = locationPrefs.parentClinicId
                .map {
                    if (it == 0L) {
                        null
                    } else {
                        it
                    }
                }.first()
            val localClinicId = locationPrefs.getCurrentClinicId()
            val eventParentClinicId = parentClinicId?.toLong()
            val eventClinicId = clinicId?.toLong()
            val parentClinicIdsMatch = eventParentClinicId == null || eventParentClinicId == localParentClinicId
            val clinicIdsMatch = eventClinicId == localClinicId
            parentClinicIdsMatch && clinicIdsMatch
        } ?: true
}
