package com.vaxcare.unifiedhub.permissions

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.vaxcare.unifiedhub.MainActivity
import com.vaxcare.unifiedhub.R
import com.vaxcare.unifiedhub.permissions.PermissionsViewModel.PermissionsState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

private const val MOBILE_BRIDGE_APP_PACKAGE = "com.vaxcare.mobilebridge"

@AndroidEntryPoint
class PermissionsActivity : AppCompatActivity() {
    private val viewModel: PermissionsViewModel by viewModels()

    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    private val bridgePackageName by lazy {
        "$MOBILE_BRIDGE_APP_PACKAGE.${Build.MODEL.lowercase()}"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
            ::onPermissionResult
        )
        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ::onActivityResult
        )
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is PermissionsState.NeedsPermission -> requestPermission(state.permission)
                    PermissionsState.Loading -> Unit
                    PermissionsState.PermissionsGranted -> checkSettings()
                    PermissionsState.DeviceInfoUnavailable -> openBridgeApp()
                    PermissionsState.DeviceInfoCaptured -> navigateToMain()
                }
            }
        }
        viewModel.checkPermissions(baseContext)
    }

    private fun requestPermission(permission: String) {
        permissionLauncher.launch(permission)
    }

    private fun checkSettings() {
        when {
            !Settings.canDrawOverlays(this) -> {
                launcher.launch(
                    Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION
                    )
                )
            }

            !packageManager.canRequestPackageInstalls() -> {
                launcher.launch(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES))
            }

            !Settings.System.canWrite(this) -> {
                launcher.launch(Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS))
            }

            !isMobileBridgeInstalled() -> {
                AlertDialog
                    .Builder(this)
                    .setCancelable(false)
                    .setTitle(R.string.bridge_required_title)
                    .setMessage(R.string.bridge_required_message)
                    .setPositiveButton(R.string.install) { dialog, _ ->
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=$bridgePackageName")
                            )
                        )

                        dialog.dismiss()
                        finish()
                    }.setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                        finish()
                    }.create()
                    .show()
            }

            else -> viewModel.getDeviceInfoFromContentResolver(contentResolver)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun isMobileBridgeInstalled(): Boolean {
        val installedPackages =
            packageManager
                .getInstalledPackages(PackageManager.PackageInfoFlags.of(0))
                .map { it.packageName }
        return installedPackages.any { it == bridgePackageName }
    }

    private fun onPermissionResult(granted: Boolean) {
        viewModel.checkPermissions(baseContext)
    }

    private fun onActivityResult(activityResult: ActivityResult) {
        Timber.d("onActivityResult: ${activityResult.resultCode} | ${activityResult.data}")
        viewModel.checkPermissions(baseContext)
    }

    // TODO detect for emulator and bypass these
    private fun openBridgeApp() {
        packageManager.getLaunchIntentForPackage(bridgePackageName)?.let { intent ->
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            launcher.launch(intent)
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
