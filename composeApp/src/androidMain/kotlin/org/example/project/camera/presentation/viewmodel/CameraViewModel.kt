package org.example.project.camera.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.camera.domain.model.CameraError
import org.example.project.camera.domain.model.RecordingState
import org.example.project.camera.domain.repository.CameraRepository
import org.example.project.camera.domain.usecase.StartRecordingUseCase
import org.example.project.camera.domain.usecase.StopRecordingUseCase
import org.example.project.camera.presentation.state.CameraUiState

/**
 * ViewModel for the camera recording screen.
 *
 * Responsibilities:
 * - Observe [CameraRepository.recordingState] and map to [CameraUiState]
 * - Manage the recording elapsed-time timer
 * - Provide actions for the UI: [toggleRecording], [dismissError], [onCameraReady]
 *
 * All dependencies are injected via constructor (Koin-managed).
 */
class CameraViewModel(
    private val startRecordingUseCase: StartRecordingUseCase,
    private val stopRecordingUseCase: StopRecordingUseCase,
    private val cameraRepository: CameraRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        observeRecordingState()
    }

    // ──────────────────────────────────────────────────────────────────────
    // Public actions for the UI
    // ──────────────────────────────────────────────────────────────────────

    /** Called by the composable once the camera preview is attached and ready. */
    fun onCameraReady() {
        _uiState.update { it.copy(isCameraReady = true) }
    }

    /** Toggles between start and stop recording. */
    fun toggleRecording() {
        viewModelScope.launch {
            if (_uiState.value.isRecording) {
                stopRecording()
            } else {
                startRecording()
            }
        }
    }

    /** Clears the current error so the UI can return to normal state. */
    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    // ──────────────────────────────────────────────────────────────────────
    // Internals
    // ──────────────────────────────────────────────────────────────────────

    private fun observeRecordingState() {
        cameraRepository.recordingState
            .onEach { state ->
                when (state) {
                    is RecordingState.Idle -> {
                        stopTimer()
                        _uiState.update {
                            it.copy(isRecording = false, elapsedSeconds = 0L)
                        }
                    }

                    is RecordingState.Recording -> {
                        _uiState.update { it.copy(isRecording = true) }
                        startTimer()
                    }

                    is RecordingState.Error -> {
                        stopTimer()
                        _uiState.update {
                            it.copy(
                                isRecording = false,
                                elapsedSeconds = 0L,
                                error = state.error,
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private suspend fun startRecording() {
        startRecordingUseCase().onFailure { throwable ->
            _uiState.update {
                it.copy(error = CameraError.RecordingFailed(
                    throwable.localizedMessage ?: "Failed to start recording"
                ))
            }
        }
    }

    private suspend fun stopRecording() {
        stopRecordingUseCase().onFailure { throwable ->
            _uiState.update {
                it.copy(error = CameraError.RecordingFailed(
                    throwable.localizedMessage ?: "Failed to stop recording"
                ))
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var elapsed = 0L
            while (true) {
                _uiState.update { it.copy(elapsedSeconds = elapsed) }
                delay(1_000L)
                elapsed++
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
        cameraRepository.release()
    }
}
