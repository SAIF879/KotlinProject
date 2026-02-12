package org.example.project.camera.domain.model

/**
 * Represents the current state of video recording.
 * Designed as a sealed interface for exhaustive when-expressions and extensibility.
 */
sealed interface RecordingState {

    /** Camera is idle — no active recording. */
    data object Idle : RecordingState

    /** Camera is actively recording. */
    data class Recording(val elapsedSeconds: Long = 0L) : RecordingState

    /** An error occurred during camera operation. */
    data class Error(val error: CameraError) : RecordingState
}
