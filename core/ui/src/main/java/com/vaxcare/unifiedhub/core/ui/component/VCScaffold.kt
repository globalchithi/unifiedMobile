package com.vaxcare.unifiedhub.core.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme.color

@Composable
fun VCScaffold(
    topBar: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    fab: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Scaffold(
        topBar = topBar,
        containerColor = color.surface.surface,
        modifier = modifier
    ) { innerPadding ->

        if (fab != null) {
            LargeFabContainer(
                fab = fab,
                modifier = Modifier.padding(innerPadding),
                content = content
            )
        } else {
            Box(Modifier.padding(innerPadding)) {
                content()
            }
        }
    }
}
