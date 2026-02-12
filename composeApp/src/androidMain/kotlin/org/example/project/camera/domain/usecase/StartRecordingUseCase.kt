package org.example.project.camera.domain.usecase

import org.example.project.camera.domain.repository.CameraRepository

/**
 * Encapsulates the "start recording" business action.
 *
 * Currently a thin delegate, but provides a stable extension point for future
 * cross-cutting concerns (analytics, validation of max duration, storage checks, etc.).
 */
class StartRecordingUseCase(private val repository: CameraRepository) {

    suspend operator fun invoke(): Result<Unit> = repository.startRecording()
}
