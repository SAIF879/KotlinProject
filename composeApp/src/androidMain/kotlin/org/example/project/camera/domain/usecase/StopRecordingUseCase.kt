package org.example.project.camera.domain.usecase

import org.example.project.camera.domain.repository.CameraRepository

/**
 * Encapsulates the "stop recording" business action.
 *
 * Extension point for future post-recording logic (e.g. file validation,
 * thumbnail generation, upload trigger).
 */
class StopRecordingUseCase(private val repository: CameraRepository) {

    suspend operator fun invoke(): Result<Unit> = repository.stopRecording()
}
