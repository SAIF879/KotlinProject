package org.example.project.camera.domain.repository

import kotlinx.coroutines.flow.StateFlow
import org.example.project.camera.domain.model.RecordingState

/**
 * Abstraction over camera hardware operations.
 *
 * The interface is deliberately narrow: only recording lifecycle methods are
 * exposed here. Preview binding is handled separately at the data/presentation
 * boundary because it requires Android View references that don't belong in
 * the domain layer.
 */
interface CameraRepository {

    /** Observable stream of the current recording state. */
    val recordingState: StateFlow<RecordingState>

    /** Begin video recording. Returns [Result.success] or a domain error. */
    suspend fun startRecording(): Result<Unit>

    /** Stop the active recording. Returns [Result.success] or a domain error. */
    suspend fun stopRecording(): Result<Unit>

    /** Release all camera resources. Should be called when camera is no longer needed. */
    fun release()
}
