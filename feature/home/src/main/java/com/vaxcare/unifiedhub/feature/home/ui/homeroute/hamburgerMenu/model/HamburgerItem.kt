package com.vaxcare.unifiedhub.feature.home.ui.homeroute.hamburgerMenu.model

import androidx.annotation.StringRes

sealed interface HamburgerItem {
    val action: () -> Unit
}

data class StringHamburgerItem(
    val itemText: String,
    override val action: () -> Unit
) : HamburgerItem

data class ResIdHamburgerItem(
    @StringRes val itemRes: Int,
    override val action: () -> Unit
) : HamburgerItem
