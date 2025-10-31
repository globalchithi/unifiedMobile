package com.vaxcare.unifiedhub.core.model.inventory

enum class StockType(
    val prettyName: String,
    val id: Int,
) {
    PRIVATE(
        prettyName = "Private",
        id = 1,
    ),
    VFC(
        prettyName = "VFC",
        id = 2,
    ),
    STATE(
        prettyName = "State",
        id = 3,
    ),
    THREE_SEVENTEEN(
        prettyName = "317",
        id = 4,
    ),

    // TODO: Re-evaluate inclusion of these values during Transfers implementation
    ANOTHER_LOCATION(
        prettyName = "Location",
        id = 5,
    ),
    ANOTHER_STOCK(
        prettyName = "Another Stock",
        id = 6,
    ),
    OTHER(
        prettyName = "Other",
        id = 7,
    );

    companion object {
        fun fromId(id: Int): StockType = entries.find { it.id == id } ?: PRIVATE
    }
}
