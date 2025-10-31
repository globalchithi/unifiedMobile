package com.vaxcare.unifiedhub.core.ui

import com.vaxcare.unifiedhub.core.ui.model.StockUi

/**
 * A central registry for UI test tags.
 *
 * ### An Explicit Rule of Thumb for Developers
 *
 * To make this clear and consistent, we'll adopt this explicit rule: **A `testTag` is required for the following types of UI elements:**
 *
 * 1.  **All Interactive Elements:** Anything a user can click, tap, or type into.
 *     *   **Examples:** `Buttons`, `TextFields`, clickable rows in a list, checkboxes, tabs.
 *     *   **Why:** So tests can reliably perform user actions.
 *
 * 2.  **Key Dynamic Data Displays:** Any `Text` composable that displays data that can change (state, network responses, calculations).
 *     *   **Examples:** A product name, a total price, a "last updated" timestamp, a user's name.
 *     *   **Why:** So tests can verify the correct information is shown without relying on brittle text matching.
 *
 * 3.  **State Indicators:** Elements that appear or disappear based on a condition.
 *     *   **Examples:** Loading spinners, error messages, success icons, empty state placeholders.
 *     *   **Why:** So tests can confirm the UI is in the correct state (e.g., wait for a spinner to disappear before continuing).
 *
 * 4.  **Major Scoping Containers:** Key containers that group UI, especially those that scroll.
 *     *   **Examples:** A `LazyColumn` a dialog window, or a bottom sheet.
 *     *   **Why:** To allow tests to perform actions (like scrolling) within a specific area and to speed up finding elements inside it.
 *
 * ### The Guiding Analogy
 *
 * We let the **electrician (the Developer)** install the light switch (the `testTag`), so the **building inspector (QA)** can simply walk in and test it.
 */
object TestTags {
    const val LARGE_FAB = "base_largeFloatingActionButton"

    object TopBar {
        const val CLOSE_BUTTON = "topBar_button"
    }

    object Home {
        const val CONTAINER = "home"
        const val CLINIC_LABEL = "home_clinicLabel"
        const val COUNT_BUTTON = "home_countButton"
        const val RETURNS_BUTTON = "home_returnsButton"
        const val ADJUST_BUTTON = "home_adjustButton"
    }

    object HamburgerMenu {
        const val SCROLL_CONTAINER = "hamburgerMenu_scrollContainer"
        const val COLLAPSE_BUTTON = "hamburgerMenu_collapseButton"
        const val ITEM = "hamburgerMenu_item"
    }

    object ProductSheet {
        object ProductCell {
            const val CONTAINER = "productCell_container"
            const val TITLE_LINE = "productCell_titleLine"
            const val TOP_CONTENT = "productCell_topContent"
            const val BOTTOM_CONTENT = "productCell_bottomContent"
        }

        object QuantityCell {
            const val PLUS_BTN = "quantityCell_plusBtn"
            const val MINUS_BTN = "quantityCell_minusBtn"
            const val INPUT_NUMBER = "quantityCell_inputNumber"
        }

        const val DELETE_BTN = "productSheet_deleteBtn"
        const val UNDO_BTN = "productSheet_undoBtn"
    }

    object AdminLogin {
        const val PASSWORD_FIELD = "adminLogin_pinField"
        const val LOGIN_BUTTON = "adminLogin_syncButton"
    }

    object AdminDetails {
        const val ENTER_PARTNER_ID_BUTTON = "adminDetails_enterPartnerIdButton"
        const val PARTNER_ID_LABEL = "adminDetails_partnerIdLabel"
        const val ENTER_CLINIC_ID_BUTTON = "adminDetails_enterClinicIdButton"
        const val CLINIC_ID_LABEL = "adminDetails_clinicIdLabel"
        const val PARTNER_CIRCULAR_PROGRESS_INDICATOR =
            "adminDetails_partnerCircularProgressIndicator"
    }

    object KeyPad {
        // --- Static Tags ---
        const val CONTAINER = "keyPad"
        const val CLOSE_BUTTON = "keyPad_closeButton"
        const val CLEAR_BUTTON = "keyPad_clearButton"
        const val BACKSPACE_BUTTON = "keyPad_backspaceButton"
        const val CONFIRM_BUTTON = "keyPad_confirmButton"

        /*
            An example of what definitely avoid:
                const val ONE_BUTTON = "keyPad_oneButton"
                const val TWO_BUTTON = "keyPad_twoButton"
                const val THREE_BUTTON = "keyPad_threeButton"
                const val FOUR_BUTTON = "keyPad_fourButton"
                const val FIVE_BUTTON = "keyPad_fiveButton"
                const val SIX_BUTTON = "keyPad_sixButton"
                const val SEVEN_BUTTON = "keyPad_sevenButton"
                const val EIGHT_BUTTON = "keyPad_eightButton"
                const val NINE_BUTTON = "keyPad_nineButton"
         */

        // --- Dynamic Tags ---
        fun digitButton(digit: Char) = "keyPad_digitButton_$digit"
    }

    object ConfirmStock {
        // --- Static Tags ---
        const val NEXT_BUTTON = "confirmStock_next"

        /*
          An example of what is preferable to avoid:
            const val VACCINES_PRIVATE_BUTTON = "confirmStock_vaccinesPrivateButton"
            const val VACCINES_VFC_BUTTON = "confirmStock_vaccinesVFCButton"
            const val VACCINES_STATE_BUTTON = "confirmStock_vaccinesStateButton"
            const val VACCINES_317_BUTTON = "confirmStock_vaccines317Button"
         */

        // --- Dynamic Tags ---
        fun stockButton(stock: StockUi) = "confirmStock_button_${stock.prettyName.lowercase()}"
    }

    object Counts {
        object Home {
            // --- Static Tags ---
            // Good to have a tag for the scrollable list
            const val PRODUCT_SHEET_CONTAINER = "countsHome_productSheetContainer"

            // --- Dynamic Tags ---
            // Functions to generate dynamic tags
            fun productItem(productId: Int) = "countsHome_productItem_$productId"

            fun sectionHeader(title: String) = "countsHome_sectionHeader_$title"

            fun sectionTab(sectionIdentifier: String) = "countsHome_sectionTab_${sectionIdentifier.lowercase()}"

            fun productItemQuantity(productId: Int) = "countsHome_productItem_quantity_$productId"

            fun productItemEditButton(productId: Int) = "countsHome_productItem_editButton_$productId"

            fun productItemConfirmButton(productId: Int) = "countsHome_productItem_confirmButton_$productId"

            fun productItemConfirmedIcon(productId: Int) = "countsHome_productItem_confirmedIcon_$productId"
        }

        object LotInteraction {
            // --- Static Tags ---
            const val CONTAINER = "lotInteraction_container"
            const val CONFIRM_BUTTON = "lotInteraction_confirmButton"
            const val FOOTER_TOTAL_QUANTITY_LABEL = "lotInteraction_footerTotalQuantityLabel"

            const val DISCARD_CHANGES_DIALOG_CONFIRM_BUTTON = "lotInteraction_discardDialog_confirmButton"
            const val DISCARD_CHANGES_DIALOG_CANCEL_BUTTON = "lotInteraction_discardDialog_cancelButton"

            // --- Dynamic Tags ---
            fun lotItem(lotNumber: String) = "lotInteraction_item_$lotNumber"

            fun lotItemNumber(lotNumber: String) = "lotInteraction_item_number_$lotNumber"

            fun lotItemMinusButton(lotNumber: String) = "lotInteraction_item_minusButton_$lotNumber"

            fun lotItemPlusButton(lotNumber: String) = "lotInteraction_item_plusButton_$lotNumber"

            fun lotItemQuantityInput(lotNumber: String) = "lotInteraction_item_quantityInput_$lotNumber"

            fun lotItemDeleteButton(lotNumber: String) = "lotInteraction_item_deleteButton_$lotNumber"

            fun lotItemUndoButton(lotNumber: String) = "lotInteraction_item_undoButton_$lotNumber"
        }

        object Submit {
            // --- Static Tags ---
            const val CONTAINER = "countsSubmit_container"
            const val CONFIRM_BUTTON = "countsSubmit_confirmButton"
            const val LOADING_SPINNER = "countsSubmit_loadingSpinner"
            const val PRODUCT_LIST = "countsSubmit_productList"
            const val SUBTOTAL_LABEL = "countsSubmit_subtotalLabel"
            const val INVOICE_WARNING_CARD = "countsSubmit_invoiceWarningCard"

            const val SUBMISSION_FAILED_DIALOG_RETRY_BUTTON = "countsSubmit_retryButton"
            const val SUBMISSION_FAILED_DIALOG_CANCEL_BUTTON = "countsSubmit_cancelButton"

            fun productItem(productId: Int) = "countsSubmit_item_$productId"

            fun productItemName(productId: Int) = "countsSubmit_itemName_$productId"

            fun productItemQuantity(productId: Int) = "countsSubmit_itemQuantity_$productId"

            fun productItemAdjustment(productId: Int) = "countsSubmit_itemAdjustment_$productId"

            fun productItemImpact(productId: Int) = "countsSubmit_itemImpact_$productId"
        }

        object Complete {
            const val INVENTORY_BALANCE_LABEL = "countsComplete_inventoryBalanceLabel"

            const val TOTAL_PRODUCTS_VALUE = "countsComplete_totalProductsValue"
            const val TOTAL_UNITS_VALUE = "countsComplete_totalUnitsValue"

            const val ADDED_UNITS_VALUE = "countsComplete_addedUnitsValue"
            const val ADDED_IMPACT_VALUE = "countsComplete_addedImpactValue"
            const val MISSING_UNITS_VALUE = "countsComplete_missingUnitsValue"
            const val MISSING_IMPACT_VALUE = "countsComplete_missingImpactValue"
            const val TOTAL_IMPACT_VALUE = "countsComplete_totalImpactValue"

            const val DISCLAIMER_TEXT = "countsComplete_disclaimerText"

            const val BACK_TO_HOME_BUTTON = "countsComplete_backToHomeButton"
            const val LOG_OUT_BUTTON = "countsComplete_logOutButton"
        }
    }

    object AddPublic {
        object Complete {
            const val HOME_BUTTON = "addPublic_homeButton"
            const val LOG_OUT_BUTTON = "addPublic_logOutButton"
        }
    }

    object LogWaste {
        object Complete {
            const val HOME_BUTTON = "logWasteComplete_homeButton"
            const val LOG_OUT_BUTTON = "logWasteComplete_logOutButton"
        }
    }

    object Returns {
        object Reasons {
            // --- Static Tags ---
            const val NEXT_BUTTON = "returns_reasons_nextButton"

            // --- Dynamic Tags ---
            fun reasonButton(reasonIdentifier: String) = "returns_reason_button_${reasonIdentifier.lowercase()}"
        }

        object ProductInteraction {
            const val PRODUCT_SHEET_CONTAINER = "returnsProduct_productSheetContainer"
            const val FOOTER_CONTAINER = "returnsProduct_footerContainer"

            fun productSheetRow(lotNumber: String) = "returnsProduct_productSheetRow_$lotNumber"
        }

        object Summary {
            const val PRODUCT_SHEET_CONTAINER = "returnsSummary_productSheetContainer"
            const val FOOTER_CONTAINER = "returnsSummary_footerContainer"

            fun productSheetRow(antigen: String) = "returnsSummary_productSheetRow_$antigen"
        }

        object Window {
            const val SHIPPING_DATE_CONTAINER = "returns_shippingDateContainer"

            fun shippingDateRow(date: String) = "returns_shippingDateRow"
        }

        object Complete {
            const val PRODUCT_SHEET_CONTAINER = "returnsComplete_productSheetContainer"
            const val HOME_BUTTON = "returnsComplete_homeButton"
            const val LOG_OUT_BUTTON = "returnsComplete_logOutButton"

            fun productSheetRow(antigen: String) = "returnsComplete_productSheetRow_$antigen"
        }
    }

    object ScannerWithSearch {
        const val SEARCH_BUTTON = "scanner_searchButton"
    }
}
