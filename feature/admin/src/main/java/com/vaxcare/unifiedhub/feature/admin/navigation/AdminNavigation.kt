package com.vaxcare.unifiedhub.feature.admin.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.navOptions
import androidx.navigation.navigation
import kotlinx.serialization.Serializable

@Serializable
data object AdminSectionRoute

@Serializable
data object AdminLoginRoute

@Serializable
data object AdminDetailsRoute

@Serializable
data object AdminInfoRoute

fun NavController.navigateToAdmin(
    navOptions: NavOptions = navOptions {
    }
) = navigate(route = AdminSectionRoute, navOptions)

fun NavController.navigateToAdminDetails(navOptions: NavOptionsBuilder.() -> Unit = {}) {
    navigate(route = AdminDetailsRoute) {
        navOptions()
    }
}

fun NavController.navigateToAdminInfo(navOptions: NavOptionsBuilder.() -> Unit = {}) {
    navigate(route = AdminInfoRoute) {
        navOptions()
    }
}

fun NavGraphBuilder.adminSection(builder: NavGraphBuilder.() -> Unit) {
    navigation<AdminSectionRoute>(startDestination = AdminLoginRoute, builder = builder)
}
