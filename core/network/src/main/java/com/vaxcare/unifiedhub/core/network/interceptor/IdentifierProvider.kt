package com.vaxcare.unifiedhub.core.network.interceptor

interface IdentifierProvider {
    fun header(): String

    fun correlationId(): String

    fun isCellular(): Boolean

    fun sessionId(): String
}
