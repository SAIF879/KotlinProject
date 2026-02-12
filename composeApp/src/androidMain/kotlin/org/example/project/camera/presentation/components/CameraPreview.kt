package org.example.project.camera.presentation.components

import android.view.ViewGroup
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.example.project.camera.data.repository.CameraRepositoryImpl

/**
 * Displays a live camera preview using [PreviewView] inside Compose.
 *
 * Binds the camera to the current [LifecycleOwner] and surface, and
 * releases resources when removed from composition.
 *
 * @param cameraRepository The data-layer repository used to bind camera use cases.
 * @param onCameraReady Callback invoked once the camera is successfully bound.
 */
@Composable
fun CameraPreview(
    cameraRepository: CameraRepositoryImpl,
    onCameraReady: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        onDispose {
            cameraRepository.release()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }.also { previewView ->
                cameraRepository.bindCamera(lifecycleOwner, previewView)
                onCameraReady()
            }
        },
    )
}
