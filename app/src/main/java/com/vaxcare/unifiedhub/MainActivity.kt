package com.vaxcare.unifiedhub

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.vaxcare.unifiedhub.core.data.datasource.AppUpdateRepository
import com.vaxcare.unifiedhub.core.data.datasource.OrientationProvider
import com.vaxcare.unifiedhub.core.data.device.BatteryMonitor
import com.vaxcare.unifiedhub.core.designsystem.theme.VaxCareTheme
import com.vaxcare.unifiedhub.core.domain.ReportMetricEventUseCase
import com.vaxcare.unifiedhub.core.ui.compose.LocalAudioManager
import com.vaxcare.unifiedhub.devtools.DevToolsRoute
import com.vaxcare.unifiedhub.feature.home.navigation.HomeRoute
import com.vaxcare.unifiedhub.feature.home.ui.home.model.HomeAnalyticsEvent
import com.vaxcare.unifiedhub.library.vaxjob.model.WorkerBuilder
import com.vaxcare.unifiedhub.navigation.VaxCareNavHost
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var appUpdateLauncher: ActivityResultLauncher<IntentSenderRequest>

    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var batteryMonitor: BatteryMonitor

    @Inject
    lateinit var orientationProvider: OrientationProvider

    @Inject
    lateinit var appUpdateRepository: AppUpdateRepository

    @Inject
    lateinit var workerBuilder: WorkerBuilder

    @Inject
    lateinit var reportMetricEvent: ReportMetricEventUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        appUpdateLauncher = registerForActivityResult(StartIntentSenderForResult()) { result ->
            appUpdateRepository.checkAppUpdateInfo()
            lifecycleScope.launch {
                if (result.resultCode != RESULT_OK) {
                    Timber.e("App update failed with result code: ${result.resultCode}")
                    reportMetricEvent(HomeAnalyticsEvent.InAppUpdateFailed)
                } else {
                    reportMetricEvent(HomeAnalyticsEvent.InAppUpdateCompleted)
                }
            }
        }

        appUpdateRepository.initializeManager(this)
        batteryMonitor.initialize(this)
        orientationProvider.updateWithRotation(display.rotation)

        val audioManager = this.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

        setContent {
            val navController = rememberNavController().also { navController ->
                // NOTE: clear the user session every time the user lands on the Home route
                lifecycleScope.launch {
                    navController.currentBackStackEntryFlow.collect {
                        if (it.destination.route == HomeRoute.javaClass.name) {
                            viewModel.clearUserSession()
                        }
                    }
                }
            }

            VaxCareTheme {
                CompositionLocalProvider(LocalAudioManager provides audioManager) {
                    Box {
                        VaxCareNavHost(
                            navController = navController,
                            launchAppUpdate = ::launchAppUpdate
                        )

                        if (BuildConfig.BUILD_TYPE != "release") {
                            WaterMark(
                                onClick = {
                                    navController.navigate(DevToolsRoute) {
                                        popUpTo<HomeRoute>()
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(16.dp)
                            )
                        }
                    }
                }
            }
        }

        hideSystemUi()
    }

    override fun onResume() {
        super.onResume()

        appUpdateRepository.manager
            ?.appUpdateInfo
            ?.addOnSuccessListener { appUpdateInfo ->
                val updateInProgress =
                    appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                if (updateInProgress) {
                    Timber.i(
                        "Upon resume, update status was DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS. Resuming stalled update."
                    )
                    appUpdateRepository.manager?.startUpdateFlowForResult(
                        appUpdateInfo,
                        appUpdateLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                    )
                }
            }
    }

    private fun launchAppUpdate() {
        lifecycleScope.launch {
            val updateInfo = appUpdateRepository.appUpdateInfo.firstOrNull() ?: return@launch
            reportMetricEvent(HomeAnalyticsEvent.InAppUpdateLaunched)

            appUpdateRepository.manager?.startUpdateFlowForResult(
                updateInfo,
                appUpdateLauncher,
                AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
            )
        }
    }

    /**
     * Hides the top status bars and the bottom navigation bars
     */
    private fun hideSystemUi() {
        actionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.insetsController?.apply {
            hide(WindowInsets.Type.navigationBars())
            systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}

@Composable
private fun WaterMark(modifier: Modifier = Modifier, onClick: () -> Unit,) {
    Text(
        text = "${BuildConfig.BUILD_TYPE.uppercase()}-${BuildConfig.VERSION_CODE}",
        style = VaxCareTheme.type.bodyTypeStyle.body6,
        color = VaxCareTheme.color.container.primaryPress,
        modifier = modifier.clickable(onClick = onClick),
    )
}
