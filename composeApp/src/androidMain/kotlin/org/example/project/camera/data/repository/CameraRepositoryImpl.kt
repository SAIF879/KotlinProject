package org.example.project.camera.data.repository

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.example.project.camera.domain.model.CameraError
import org.example.project.camera.domain.model.RecordingState
import org.example.project.camera.domain.repository.CameraRepository
import java.io.File

/**
 * CameraX-backed implementation of [CameraRepository].
 *
 * Manages the camera lifecycle (preview + video capture), records to a temp
 * file, and exposes recording state as a reactive [StateFlow].
 *
 * Call [bindCamera] from the presentation layer to attach the camera to a
 * [PreviewView] and [LifecycleOwner]. This is intentionally outside the
 * domain contract because it requires Android View references.
 */
class CameraRepositoryImpl(
    private val context: Context,
) : CameraRepository {

    companion object {
        private const val TAG = "CameraRepositoryImpl"
    }

    private val _recordingState = MutableStateFlow<RecordingState>(RecordingState.Idle)
    override val recordingState: StateFlow<RecordingState> = _recordingState.asStateFlow()

    private val _isFrontCamera = MutableStateFlow(false)
    override val isFrontCamera: StateFlow<Boolean> = _isFrontCamera.asStateFlow()

    private var currentCameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var cameraProvider: ProcessCameraProvider? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var activeRecording: Recording? = null
    private var preview: Preview? = null
    private var boundLifecycleOwner: LifecycleOwner? = null
    private var boundPreviewView: PreviewView? = null

    // ──────────────────────────────────────────────────────────────────────
    // Camera binding (called from presentation layer)
    // ──────────────────────────────────────────────────────────────────────

    /**
     * Binds the camera preview and video capture use cases to the given
     * [lifecycleOwner] and [previewView].
     *
     * Must be called before [startRecording].
     */
    fun bindCamera(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
    ) {
        boundLifecycleOwner = lifecycleOwner
        boundPreviewView = previewView
        bindCameraInternal(lifecycleOwner, previewView)
    }

    /**
     * Switches between front and back camera.
     * Cannot switch while actively recording.
     */
    override fun switchCamera() {
        if (_recordingState.value is RecordingState.Recording) {
            Log.w(TAG, "Cannot switch camera while recording")
            return
        }

        currentCameraSelector = if (currentCameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        _isFrontCamera.value = currentCameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA

        val owner = boundLifecycleOwner
        val view = boundPreviewView
        if (owner != null && view != null) {
            bindCameraInternal(owner, view)
        }
    }

    private fun bindCameraInternal(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener(
            {
                try {
                    val provider = cameraProviderFuture.get()
                    cameraProvider = provider

                    // Preview use case
                    preview = Preview.Builder()
                        .build()
                        .also { it.surfaceProvider = previewView.surfaceProvider }

                    // Recorder → VideoCapture use case
                    val recorder = Recorder.Builder()
                        .setQualitySelector(QualitySelector.from(Quality.HD))
                        .build()
                    videoCapture = VideoCapture.withOutput(recorder)

                    // Unbind previous use cases, then bind with current selector
                    provider.unbindAll()
                    provider.bindToLifecycle(
                        lifecycleOwner,
                        currentCameraSelector,
                        preview,
                        videoCapture,
                    )

                    Log.d(TAG, "Camera bound successfully (front=${_isFrontCamera.value})")
                } catch (e: Exception) {
                    Log.e(TAG, "Camera binding failed", e)
                    _recordingState.value = RecordingState.Error(
                        CameraError.CameraInitFailed(e.localizedMessage ?: "Camera init failed")
                    )
                }
            },
            ContextCompat.getMainExecutor(context),
        )
    }

    // ──────────────────────────────────────────────────────────────────────
    // Recording operations (domain contract)
    // ──────────────────────────────────────────────────────────────────────

    override suspend fun startRecording(): Result<Unit> {
        val capture = videoCapture
            ?: return Result.failure(
                IllegalStateException("VideoCapture not initialized. Call bindCamera() first.")
            )

        // Temp file — not persisted
        val tempFile = File.createTempFile("recording_", ".mp4", context.cacheDir)
        val outputOptions = FileOutputOptions.Builder(tempFile).build()

        try {
            val pendingRecording = capture.output
                .prepareRecording(context, outputOptions)

            // Add audio if permission is granted
            if (PermissionChecker.checkSelfPermission(
                    context, Manifest.permission.RECORD_AUDIO
                ) == PermissionChecker.PERMISSION_GRANTED
            ) {
                pendingRecording.withAudioEnabled()
            }

            activeRecording = pendingRecording.start(
                ContextCompat.getMainExecutor(context)
            ) { event ->
                handleRecordingEvent(event, tempFile)
            }

            _recordingState.value = RecordingState.Recording()
            Log.d(TAG, "Recording started → ${tempFile.absolutePath}")
            return Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start recording", e)
            _recordingState.value = RecordingState.Error(
                CameraError.RecordingFailed(e.localizedMessage ?: "Failed to start recording")
            )
            return Result.failure(e)
        }
    }

    override suspend fun stopRecording(): Result<Unit> {
        return try {
            activeRecording?.stop()
            activeRecording = null
            // State will be set to Idle in the Finalize event callback
            Log.d(TAG, "Recording stop requested")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop recording", e)
            _recordingState.value = RecordingState.Error(
                CameraError.RecordingFailed(e.localizedMessage ?: "Failed to stop recording")
            )
            Result.failure(e)
        }
    }

    override fun release() {
        activeRecording?.stop()
        activeRecording = null
        cameraProvider?.unbindAll()
        cameraProvider = null
        videoCapture = null
        preview = null
        _recordingState.value = RecordingState.Idle
        Log.d(TAG, "Camera resources released")
    }

    // ──────────────────────────────────────────────────────────────────────
    // Internal helpers
    // ──────────────────────────────────────────────────────────────────────

    private fun handleRecordingEvent(event: VideoRecordEvent, tempFile: File) {
        when (event) {
            is VideoRecordEvent.Finalize -> {
                if (event.hasError()) {
                    Log.e(TAG, "Recording finalized with error: ${event.error}")
                    _recordingState.value = RecordingState.Error(
                        CameraError.RecordingFailed("Recording error code: ${event.error}")
                    )
                } else {
                    Log.d(TAG, "Recording finalized successfully")
                }

                // Clean up temp file — we don't persist yet
                if (tempFile.exists()) {
                    tempFile.delete()
                    Log.d(TAG, "Temp file deleted")
                }

                activeRecording = null
                _recordingState.value = RecordingState.Idle
            }

            is VideoRecordEvent.Status -> {
                // Could extract duration from event.recordingStats if needed
                Log.v(TAG, "Recording status update")
            }
        }
    }
}
