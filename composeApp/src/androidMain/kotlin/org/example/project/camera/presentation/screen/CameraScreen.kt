package org.example.project.camera.presentation.screen

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.project.camera.data.repository.CameraRepositoryImpl
import org.example.project.camera.domain.repository.CameraRepository
import org.example.project.camera.presentation.components.CameraPreview
import org.example.project.camera.presentation.components.PermissionScreen
import org.example.project.camera.presentation.components.RecordingControls
import org.example.project.camera.presentation.components.RecordingTimer
import org.example.project.camera.presentation.viewmodel.CameraViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.compose.koinInject

private val REQUIRED_PERMISSIONS = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.RECORD_AUDIO,
)

/**
 * Top-level camera recording screen.
 *
 * This composable is the entry point for the camera feature. It:
 * 1. Gates the UI behind runtime permission checks
 * 2. Shows the camera preview with recording controls when granted
 * 3. Shows a permission rationale / settings screen when denied
 */
@Composable
fun CameraScreen(
    viewModel: CameraViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Permission state
    var permissionsGranted by rememberSaveable {
        mutableStateOf(
            REQUIRED_PERMISSIONS.all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        )
    }
    var isPermanentlyDenied by rememberSaveable { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissionsGranted = permissions.values.all { it }

        if (!permissionsGranted) {
            // Check if user selected "Don't ask again"
            val activity = context as? Activity
            isPermanentlyDenied = activity != null && REQUIRED_PERMISSIONS.any { perm ->
                !ActivityCompat.shouldShowRequestPermissionRationale(activity, perm) &&
                    ContextCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED
            }
        }
    }

    if (permissionsGranted) {
        CameraContent(viewModel = viewModel)
    } else {
        PermissionScreen(
            onRequestPermission = {
                permissionLauncher.launch(REQUIRED_PERMISSIONS)
            },
            isPermanentlyDenied = isPermanentlyDenied,
        )
    }
}

/**
 * Main camera UI: preview + recording controls + timer + error snackbar.
 */
@Composable
private fun CameraContent(
    viewModel: CameraViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cameraRepository: CameraRepository = koinInject()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .systemBarsPadding(),
    ) {
        // Live camera preview (fills the entire screen)
        CameraPreview(
            cameraRepository = cameraRepository as CameraRepositoryImpl,
            onCameraReady = viewModel::onCameraReady,
            modifier = Modifier.fillMaxSize(),
        )

        // Recording timer — top center
        RecordingTimer(
            isRecording = uiState.isRecording,
            formattedTime = uiState.formattedTime,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 24.dp),
        )

        // Record / Stop button — bottom center
        RecordingControls(
            isRecording = uiState.isRecording,
            onToggleRecording = viewModel::toggleRecording,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
        )

        // Error snackbar — bottom
        uiState.error?.let { error ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = {
                    TextButton(onClick = viewModel::dismissError) {
                        Text("Dismiss", color = Color.White)
                    }
                },
                containerColor = Color(0xFFB91C1C),
            ) {
                Text(
                    text = error.message,
                    color = Color.White,
                )
            }
        }
    }
}
