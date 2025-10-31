package com.vaxcare.unifiedhub.feature.home.ui.onhand.util

object OnHandTokens {
    private const val FLU_ANTIGEN = "INFLUENZA"
    private const val COVID_ANTIGEN = "COVID"
    private const val COVID19_ANTIGEN = "COVID-19"

    internal val seasonalAntigens = listOf(
        FLU_ANTIGEN,
        COVID_ANTIGEN,
        COVID19_ANTIGEN
    )
}
