package com.vaxcare.unifiedhub.core.data.extension

infix fun <A, B, C> Pair<A, B>.to(third: C) = Triple(first, second, third)
