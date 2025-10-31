package com.vaxcare.unifiedhub.feature.home.ui.homeroute.hamburgerMenu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.color
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.measurement
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.type
import com.vaxcare.unifiedhub.core.ui.TestTags
import com.vaxcare.unifiedhub.core.ui.ext.verticalFadingEdge
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.hamburgerMenu.model.HamburgerItem
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.hamburgerMenu.model.HamburgerSection
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.hamburgerMenu.model.ResIdHamburgerItem
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.hamburgerMenu.model.ResIdHamburgerSection
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.hamburgerMenu.model.StringHamburgerItem
import com.vaxcare.unifiedhub.feature.home.ui.homeroute.hamburgerMenu.model.StringHamburgerSection
import com.vaxcare.unifiedhub.core.designsystem.R as designR
import com.vaxcare.unifiedhub.feature.home.R as homeR

@Composable
fun HamburgerMenu(
    menuItems: List<HamburgerSection>,
    drawerState: DrawerState,
    onMenuClose: () -> Unit,
    content: @Composable () -> Unit
) {
    val lazyListState = rememberLazyListState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier,
                drawerState = drawerState,
                drawerContainerColor = color.surface.surfaceBright
            ) {
                Column(modifier = Modifier.padding(horizontal = measurement.spacing.small)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(
                                horizontal = measurement.spacing.small,
                                vertical = measurement.spacing.xSmall,
                            )
                    ) {
                        Text(
                            text = stringResource(homeR.string.main_menu),
                            style = type.bodyTypeStyle.body4Bold,
                        )
                        Spacer(Modifier.weight(1f))
                        IconButton(
                            modifier = Modifier.testTag(TestTags.HamburgerMenu.COLLAPSE_BUTTON),
                            onClick = onMenuClose
                        ) {
                            Icon(
                                painter = painterResource(designR.drawable.ic_chevron_left),
                                contentDescription = stringResource(homeR.string.collapse_menu),
                            )
                        }
                    }
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier
                            .verticalFadingEdge(lazyListState = lazyListState, 32.dp)
                            .testTag(TestTags.HamburgerMenu.SCROLL_CONTAINER)
                    ) {
                        menuItems.forEachIndexed { sectionIndex, section ->
                            stickyHeader {
                                HamburgerMenuHeader(section)
                            }
                            itemsIndexed(section.items) { index, item ->
                                HamburgerMenuItem(item)
                                if (index == section.items.lastIndex && sectionIndex != menuItems.lastIndex) {
                                    HorizontalDivider(
                                        Modifier
                                            .width(328.dp)
                                            .height(1.dp)
                                            .padding(start = 16.dp, end = 16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) {
        content()
    }
}

@Composable
private fun HamburgerMenuItem(item: HamburgerItem) {
    val text = when (item) {
        is StringHamburgerItem -> {
            item.itemText
        }

        is ResIdHamburgerItem -> {
            stringResource(item.itemRes)
        }
    }
    NavigationDrawerItem(
        modifier = Modifier.testTag(TestTags.HamburgerMenu.ITEM + "_text"),
        label = {
            Text(
                text = text,
                style = type.bodyTypeStyle.body5,
                modifier = Modifier
                    .width(328.dp)
                    .height(56.dp)
                    .padding(
                        start = 8.dp,
                        top = measurement.spacing.small,
                        bottom = measurement.spacing.small,
                    )
            )
        },
        selected = false,
        onClick = item.action
    )
}

@Composable
private fun HamburgerMenuHeader(item: HamburgerSection) {
    val text = when (item) {
        is StringHamburgerSection -> {
            item.headerText
        }

        is ResIdHamburgerSection -> {
            stringResource(item.headerRes)
        }
    }
    Text(
        text = text.uppercase(),
        style = type.bodyTypeStyle.label,
        modifier = Modifier
            .width(328.dp)
            .background(color.surface.surfaceBright)
            .padding(
                horizontal = measurement.spacing.small,
                vertical = 18.dp,
            )
    )
}
