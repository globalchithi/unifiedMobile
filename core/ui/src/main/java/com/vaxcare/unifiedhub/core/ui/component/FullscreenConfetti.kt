package com.vaxcare.unifiedhub.core.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun rememberConfettiComposition() =
    rememberLottieComposition(
        spec = LottieCompositionSpec.Asset(("lottie/animation_vaxcare_confetti.json")),
        imageAssetsFolder = "lottie/animationImages"
    )

@Composable
fun VaxCareConfetti(modifier: Modifier = Modifier) {
    val composition by rememberConfettiComposition()
    LottieAnimation(
        composition = composition,
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}
