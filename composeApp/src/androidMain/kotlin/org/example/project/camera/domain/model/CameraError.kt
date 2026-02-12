package org.example.project.camera.domain.model

/**
 * Domain-level error hierarchy for camera operations.
 * Decouples the domain from platform-specific exceptions.
 */
sealed class CameraError(open val message: String) {

    /** Camera provider could not be obtained or bound. */
    data class CameraInitFailed(override val message: String = "Failed to initialize camera") : CameraError(message)

    /** An error occurred while starting or during recording. */
    data class RecordingFailed(override val message: String = "Recording failed") : CameraError(message)

    /** Catch-all for unexpected failures. */
    data class Unknown(val throwable: Throwable) : CameraError(throwable.localizedMessage ?: "Unknown error")
}
