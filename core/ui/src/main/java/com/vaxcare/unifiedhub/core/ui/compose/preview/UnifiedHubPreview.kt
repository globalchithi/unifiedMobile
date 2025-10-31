package com.vaxcare.unifiedhub.core.ui.compose.preview

import androidx.compose.ui.tooling.preview.Preview

private const val RHINO_T105 = "spec:width=1920px,height=1200px,dpi=260"
private const val ORIENTATION_PORTRAIT = ",orientation=portrait"
private const val ORIENTATION_LANDSCAPE = ",orientation=landscape"

@Preview(name = "Portrait", showBackground = true, device = RHINO_T105 + ORIENTATION_PORTRAIT)
@Preview(name = "Landscape", showBackground = true, device = RHINO_T105 + ORIENTATION_LANDSCAPE)
annotation class FullDevicePreview

@Preview(name = "Landscape", showBackground = true, device = RHINO_T105 + ORIENTATION_LANDSCAPE)
annotation class LandscapePreview

@Preview(name = "Portrait", showBackground = true, device = RHINO_T105 + ORIENTATION_PORTRAIT)
annotation class PortraitPreview
