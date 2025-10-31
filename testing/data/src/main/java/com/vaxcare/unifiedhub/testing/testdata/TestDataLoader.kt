package com.vaxcare.unifiedhub.testing.testdata

object TestDataLoader {
    fun loadJson(relativePath: String): String =
        requireNotNull(javaClass.classLoader?.getResource(relativePath)) {
            "Asset not found: $relativePath"
        }.readText(Charsets.UTF_8)
}
