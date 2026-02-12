package org.example.project.camera.presentation.screen

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.project.camera.data.repository.CameraRepositoryImpl
import org.example.project.camera.domain.repository.CameraRepository
import org.example.project.camera.presentation.components.CameraOverlay
import org.example.project.camera.presentation.components.CameraPreview
import org.example.project.camera.presentation.components.PermissionScreen
import org.example.project.camera.presentation.components.RecordingControls
import org.example.project.camera.presentation.components.RecordingTimer
import org.example.project.camera.presentation.theme.SurveillanceColors
import org.example.project.camera.presentation.viewmodel.CameraViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.compose.koinInject

private val REQUIRED_PERMISSIONS = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.RECORD_AUDIO,
)

/**
 * Top-level camera recording screen with surveillance theme.
 *
 * This composable is the entry point for the camera feature. It:
 * 1. Gates the UI behind runtime permission checks
 * 2. Shows the camera preview with CCTV overlay and controls when granted
 * 3. Shows an "ACCESS DENIED" screen when permissions are denied
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
 * Main camera UI: preview + CCTV overlay + recording controls + flip button + error snackbar.
 */
@Composable
private fun CameraContent(
    viewModel: CameraViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cameraRepository: CameraRepository = koinInject()

    // Flip animation
    var flipCount by remember { mutableIntStateOf(0) }
    val flipRotation by animateFloatAsState(
        targetValue = flipCount * 180f,
        animationSpec = tween(durationMillis = 300),
        label = "flip_rotation",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurveillanceColors.Background)
            .systemBarsPadding(),
    ) {
        // Live camera preview
        CameraPreview(
            cameraRepository = cameraRepository as CameraRepositoryImpl,
            onCameraReady = viewModel::onCameraReady,
            modifier = Modifier.fillMaxSize(),
        )

        // CCTV overlay (corner brackets, grid, labels)
        CameraOverlay(
            isFrontCamera = uiState.isFrontCamera,
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

        // Flip camera button — bottom right (hidden while recording)
        if (!uiState.isRecording) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 60.dp, end = 32.dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(SurveillanceColors.SurfaceVariant.copy(alpha = 0.7f))
                    .rotate(flipRotation)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ) {
                        flipCount++
                        viewModel.switchCamera()
                    },
                contentAlignment = Alignment.Center,
            ) {
                FlipCameraIconCanvas()
            }
        }

        // Record / Stop button — bottom center
        RecordingControls(
            isRecording = uiState.isRecording,
            onToggleRecording = viewModel::toggleRecording,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
        )

        // Error snackbar — bottom
        uiState.error?.let { error ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = {
                    TextButton(onClick = viewModel::dismissError) {
                        Text(
                            text = "[ DISMISS ]",
                            color = SurveillanceColors.NeonGreen,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp,
                        )
                    }
                },
                containerColor = SurveillanceColors.RecRed.copy(alpha = 0.9f),
            ) {
                Text(
                    text = "⚠ ${error.message.uppercase()}",
                    color = Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                )
            }
        }
    }
}

/**
 * Canvas-drawn flip camera icon — two curved arrows.
 */
@Composable
private fun FlipCameraIconCanvas() {
    Canvas(modifier = Modifier.size(22.dp)) {
        val w = size.width
        val h = size.height
        val color = SurveillanceColors.NeonGreen

        // Top arrow arc
        drawArc(
            color = color,
            startAngle = 200f,
            sweepAngle = 140f,
            useCenter = false,
            style = Stroke(width = 2f, cap = StrokeCap.Round),
        )

        // Bottom arrow arc
        drawArc(
            color = color,
            startAngle = 20f,
            sweepAngle = 140f,
            useCenter = false,
            style = Stroke(width = 2f, cap = StrokeCap.Round),
        )

        // Arrow heads
        // Top arrow head (at ~340 degrees = top-right)
        val ax1 = w * 0.85f
        val ay1 = h * 0.15f
        drawLine(color, Offset(ax1, ay1), Offset(ax1 - 5f, ay1 + 2f), 2f, StrokeCap.Round)
        drawLine(color, Offset(ax1, ay1), Offset(ax1 - 1f, ay1 + 5f), 2f, StrokeCap.Round)

        // Bottom arrow head (at ~160 degrees = bottom-left)
        val ax2 = w * 0.15f
        val ay2 = h * 0.85f
        drawLine(color, Offset(ax2, ay2), Offset(ax2 + 5f, ay2 - 2f), 2f, StrokeCap.Round)
        drawLine(color, Offset(ax2, ay2), Offset(ax2 + 1f, ay2 - 5f), 2f, StrokeCap.Round)
    }
}
