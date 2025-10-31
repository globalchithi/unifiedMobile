package com.vaxcare.unifiedhub.core.data.functionality

import com.squareup.moshi.Moshi
import com.vaxcare.unifiedhub.core.data.mock.MockRequests
import com.vaxcare.unifiedhub.core.data.network.interceptor.OfflineRequestValidator
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.RelaxedMockK
import org.junit.Before
import org.junit.Test

class OfflineValidatorTest {
    @RelaxedMockK
    lateinit var moshi: Moshi

    private lateinit var offlineRequestValidator: OfflineRequestValidator

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        offlineRequestValidator = OfflineRequestValidator(moshi)
    }

    @Test
    fun `Validate Invalid Request`() {
        val savedOfflineRequest =
            offlineRequestValidator.validateRequest(MockRequests.postLotNumberIgnoreHeader)
        assert(savedOfflineRequest == null)
    }

    @Test
    fun `Validate Valid Request`() {
        val savedOfflineRequest =
            offlineRequestValidator.validateRequest(MockRequests.postLotNumber)
        assert(savedOfflineRequest != null)
    }
}
