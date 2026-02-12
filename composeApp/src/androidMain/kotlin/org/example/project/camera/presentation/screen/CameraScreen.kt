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

@Composable
fun CameraScreen(
    viewModel: CameraViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
            onRequestPermission = { permissionLauncher.launch(REQUIRED_PERMISSIONS) },
            isPermanentlyDenied = isPermanentlyDenied,
        )
    }
}

@Composable
private fun CameraContent(
    viewModel: CameraViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cameraRepository: CameraRepository = koinInject()

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
        // Camera preview
        CameraPreview(
            cameraRepository = cameraRepository as CameraRepositoryImpl,
            onCameraReady = viewModel::onCameraReady,
            modifier = Modifier.fillMaxSize(),
        )

        // Status overlay
        CameraOverlay(
            isFrontCamera = uiState.isFrontCamera,
            modifier = Modifier.fillMaxSize(),
        )

        // Recording timer — top center (below overlay bar)
        RecordingTimer(
            isRecording = uiState.isRecording,
            formattedTime = uiState.formattedTime,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 52.dp),
        )

        // Flip camera — bottom end
        if (!uiState.isRecording) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 56.dp, end = 40.dp)
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(SurveillanceColors.Background.copy(alpha = 0.5f))
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
                FlipIcon()
            }
        }

        // Record button — bottom center
        RecordingControls(
            isRecording = uiState.isRecording,
            onToggleRecording = viewModel::toggleRecording,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
        )

        // Error snackbar
        uiState.error?.let { error ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = {
                    TextButton(onClick = viewModel::dismissError) {
                        Text("Dismiss", color = SurveillanceColors.White)
                    }
                },
                containerColor = SurveillanceColors.RecRed,
            ) {
                Text(
                    text = error.message,
                    color = SurveillanceColors.White,
                    fontSize = 13.sp,
                )
            }
        }
    }
}

@Composable
private fun FlipIcon() {
    Canvas(modifier = Modifier.size(20.dp)) {
        val color = SurveillanceColors.White

        // Two arcs forming a refresh/flip symbol
        drawArc(
            color = color,
            startAngle = 200f,
            sweepAngle = 140f,
            useCenter = false,
            style = Stroke(width = 2f, cap = StrokeCap.Round),
        )
        drawArc(
            color = color,
            startAngle = 20f,
            sweepAngle = 140f,
            useCenter = false,
            style = Stroke(width = 2f, cap = StrokeCap.Round),
        )

        // Arrow heads
        val w = size.width
        val h = size.height
        drawLine(color, Offset(w * 0.85f, h * 0.15f), Offset(w * 0.85f - 4f, h * 0.15f + 3f), 2f, StrokeCap.Round)
        drawLine(color, Offset(w * 0.85f, h * 0.15f), Offset(w * 0.85f - 1f, h * 0.15f + 5f), 2f, StrokeCap.Round)
        drawLine(color, Offset(w * 0.15f, h * 0.85f), Offset(w * 0.15f + 4f, h * 0.85f - 3f), 2f, StrokeCap.Round)
        drawLine(color, Offset(w * 0.15f, h * 0.85f), Offset(w * 0.15f + 1f, h * 0.85f - 5f), 2f, StrokeCap.Round)
    }
}
