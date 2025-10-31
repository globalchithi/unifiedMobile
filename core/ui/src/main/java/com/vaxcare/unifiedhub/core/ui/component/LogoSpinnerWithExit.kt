package com.vaxcare.unifiedhub.core.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import com.vaxcare.unifiedhub.core.designsystem.R
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

@Composable
fun LogoSpinnerWithExit(
    isLoading: Boolean,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val rotation = remember {
        Animatable(0f)
    }
    val translationY = remember {
        Animatable(0f)
    }
    val alpha = remember {
        Animatable(100f)
    }

    LaunchedEffect(isLoading) {
        // indefinite spin
        while (isLoading) {
            rotation.snapTo(0f)
            rotation.animateTo(
                targetValue = 360f,
                animationSpec = tween(
                    durationMillis = 1000,
                    delayMillis = 200
                )
            )
            ensureActive()
        }

        // after loading is finished, give the spinner a smooth exit
        rotation.snapTo(0f)
        val translateJob = launch {
            translationY.animateTo(
                targetValue = -100f,
                animationSpec = tween(durationMillis = 100),
            )
        }
        val fadeJob = launch {
            alpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = 100,
                    easing = EaseOut
                )
            )
        }
        joinAll(translateJob, fadeJob)

        // animation has completed, signal the consumer
        onFinish()
    }

    Image(
        painter = painterResource(R.drawable.ic_vaxcare_logo),
        contentDescription = null,
        modifier = modifier
            .graphicsLayer {
                this.rotationZ = rotation.value
                this.translationY = translationY.value
                this.alpha = alpha.value
            }
    )
}
