package com.vaxcare.unifiedhub.feature.transactions.lot.data

import com.vaxcare.unifiedhub.core.model.lot.Lot
import com.vaxcare.unifiedhub.core.model.product.Presentation
import com.vaxcare.unifiedhub.core.model.product.Product

val testProducts = listOf(
    Product(
        id = 1,
        inventoryGroup = "invGroup1",
        antigen = "testantigen",
        displayName = "testname",
        presentation = Presentation.PREFILLED_SYRINGE,
        categoryId = 2,
        prettyName = "vaCCiNePreTTy",
        lossFee = null
    ),
    Product(
        id = 2,
        inventoryGroup = "invGroup2",
        antigen = "INFLUENZA",
        displayName = "testflu",
        presentation = Presentation.PREFILLED_SYRINGE,
        categoryId = 5,
        prettyName = null,
        lossFee = null
    ),
    Product(
        id = 3,
        inventoryGroup = "invGroup3",
        antigen = "larc",
        displayName = "testlarc",
        presentation = Presentation.PREFILLED_SYRINGE,
        categoryId = 3,
        prettyName = "lArCpRettY",
        lossFee = null
    )
)

val testLots = listOf(
    Lot(
        expiration = null,
        lotNumber = "ABC123",
        productId = 1,
        salesProductId = 1
    ),
    Lot(
        expiration = null,
        lotNumber = "TESTLOT",
        productId = 1,
        salesProductId = 1
    ),
    Lot(
        expiration = null,
        lotNumber = "123456",
        productId = 1,
        salesProductId = 1
    ),
    Lot(
        expiration = null,
        lotNumber = "JAMESLOT",
        productId = 1,
        salesProductId = 1
    ),
    Lot(
        expiration = null,
        lotNumber = "MAPLELEAF",
        productId = 1,
        salesProductId = 1
    )
)
