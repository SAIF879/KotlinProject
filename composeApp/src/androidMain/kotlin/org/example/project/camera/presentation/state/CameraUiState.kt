package org.example.project.camera.presentation.state

import org.example.project.camera.domain.model.CameraError

/**
 * Immutable snapshot of the camera screen's UI state.
 *
 * The ViewModel produces new instances of this class on every state change;
 * Compose recomposes only the parts of the UI that actually changed.
 */
data class CameraUiState(
    val isCameraReady: Boolean = false,
    val isRecording: Boolean = false,
    val elapsedSeconds: Long = 0L,
    val error: CameraError? = null,
) {
    /** Formatted timer string: "MM:SS". */
    val formattedTime: String
        get() {
            val minutes = elapsedSeconds / 60
            val seconds = elapsedSeconds % 60
            return "%02d:%02d".format(minutes, seconds)
        }
}
