package com.vaxcare.unifiedhub.feature.home.ui.homeroute.hamburgerMenu.model

import androidx.annotation.StringRes

sealed interface HamburgerSection {
    val items: List<HamburgerItem>
}

data class StringHamburgerSection(
    val headerText: String,
    override val items: List<HamburgerItem>
) : HamburgerSection

data class ResIdHamburgerSection(
    @StringRes val headerRes: Int,
    override val items: List<HamburgerItem>
) : HamburgerSection
