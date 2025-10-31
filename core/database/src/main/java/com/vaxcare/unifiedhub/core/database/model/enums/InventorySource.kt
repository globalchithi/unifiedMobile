package com.vaxcare.unifiedhub.core.database.model.enums

enum class InventorySource(
    val desc: String,
    val displayName: String,
    val id: Int,
) {
    PRIVATE(
        desc = "Provided by VaxCare for privately insured",
        displayName = "Private",
        id = 1,
    ),
    VFC(
        desc = "Provided by the CDC for the VFC program",
        displayName = "VFC",
        id = 2,
    ),
    STATE(
        desc = "Other state funded vaccines",
        displayName = "State",
        id = 3,
    ),
    THREE_SEVENTEEN(
        desc = "Purchased with Section 317 grant funding",
        displayName = "317",
        id = 4,
    ),
    ANOTHER_LOCATION(
        desc = "",
        displayName = "Location",
        id = 5,
    ),
    ANOTHER_STOCK(
        desc = "",
        displayName = "Another Stock",
        id = 6,
    ),
    OTHER(
        desc = "",
        displayName = "Other",
        id = 7,
    );

    companion object {
        fun fromId(id: Int): InventorySource = entries.find { it.id == id } ?: PRIVATE
    }
}
