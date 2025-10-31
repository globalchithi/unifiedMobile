package com.vaxcare.unifiedhub.app.test.robots.admin.details

import androidx.compose.ui.test.junit4.ComposeTestRule
import com.vaxcare.unifiedhub.app.test.arch.BaseRobot
import com.vaxcare.unifiedhub.app.test.arch.SystemRobot
import com.vaxcare.unifiedhub.app.test.robots.common.KeyPadRobot
import com.vaxcare.unifiedhub.app.test.robots.home.HomeRobot
import com.vaxcare.unifiedhub.app.test.rule.TestServerRule
import com.vaxcare.unifiedhub.core.ui.TestTags

class AdminDetailsRobot(
    compose: ComposeTestRule,
    server: TestServerRule
) : BaseRobot<AdminDetailsRobot>(compose, server) {
    fun enterPartnerAndClinicIds(partnerId: String, clinicId: String) =
        apply {
            waitAndClick(TestTags.AdminDetails.ENTER_PARTNER_ID_BUTTON)

            KeyPadRobot(this, compose, server)
                .typeSequence(partnerId)
                .clickConfirm()

            KeyPadRobot(this, compose, server)
                .typeSequence(clinicId)
                .clickConfirm()

            waitForLoadingIndicator(TestTags.AdminDetails.PARTNER_CIRCULAR_PROGRESS_INDICATOR)

            SystemRobot().performClinicInitialDataSync()
        }

    fun verifyPartnerIds(partnerId: String) =
        apply {
            waitAndAssertTextContains(
                TestTags.AdminDetails.PARTNER_ID_LABEL,
                partnerId.toString()
            )
        }

    fun verifyClinicIds(clinicId: String) =
        apply {
            waitAndAssertTextContains(
                TestTags.AdminDetails.CLINIC_ID_LABEL,
                clinicId.toString()
            )
        }

    fun clickCloseAndReturnHome(): HomeRobot {
        waitAndClick(TestTags.TopBar.CLOSE_BUTTON)

        return HomeRobot(compose, server)
    }

    // ---------- STUBS ----------
    fun stubSuccessfulLocationSync(partnerId: String, clinicId: String) =
        apply {
            server.get("/api/setup/checkData?partnerId=$partnerId&clinicId=$clinicId") {
                server.bodyJson("setup/checkData_200.json")
            }

            server.get("/api/setup/LocationData?clinicId=$clinicId") {
                server.bodyJson("setup/getLocation_200.json")
            }
        }

    fun stubHubSetup(partnerId: String) =
        apply {
            server.get("/api/setup/config?isOffline=true") {
                server.bodyJson("setup/getConfig_200.json")
            }
            server.get("/api/setup/usersPartnerLevel?partnerId=$partnerId") {
                server.bodyJson("setup/listUsers_200.json")
            }
            server.get("/api/ping") { server.bodyJson("system/ping_204.json") }
        }

    // Default workers after hub sync
    fun stubInventoryJobs() =
        apply {
            apply {
                server.get("/api/inventory/lotInventory") {
                    server.bodyJson("inventory/listLots_200.json")
                }
                server.get("/api/inventory/lotnumbers?maximumExpirationAgeInDays=365") {
                    server.bodyJson("inventory/listLotNumbers_200_maxAge365d.json")
                }
                server.get("/api/inventory/product/mappings") {
                    server.bodyJson("product/listMappings_200.json")
                }
                server.get("/api/inventory/product/v2") {
                    server.bodyJson("product/list_200.json")
                }
                server.post("/api/inventory/vaccinecountconfirmation/get") {
                    server.bodyJson("inventory/listCountConfirmations_200.json")
                }
                server.get("/api/ndc/blacklisted") {
                    server.bodyJson("ndc/blacklisted_200.json")
                }
            }
        }
}
