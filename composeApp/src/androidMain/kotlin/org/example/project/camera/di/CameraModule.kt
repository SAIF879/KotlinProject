package org.example.project.camera.di

import org.example.project.camera.data.repository.CameraRepositoryImpl
import org.example.project.camera.domain.repository.CameraRepository
import org.example.project.camera.domain.usecase.StartRecordingUseCase
import org.example.project.camera.domain.usecase.StopRecordingUseCase
import org.example.project.camera.presentation.viewmodel.CameraViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin dependency graph for the camera feature.
 *
 * Wiring:
 *   CameraRepositoryImpl (singleton) → CameraRepository
 *   StartRecordingUseCase (factory)
 *   StopRecordingUseCase  (factory)
 *   CameraViewModel       (viewModel scope)
 */
val cameraModule = module {

    // Data layer — singleton because camera hardware is a shared resource
    single<CameraRepository> { CameraRepositoryImpl(context = get()) }

    // Domain layer — factory because use cases are stateless
    factory { StartRecordingUseCase(repository = get()) }
    factory { StopRecordingUseCase(repository = get()) }

    // Presentation layer
    viewModel {
        CameraViewModel(
            startRecordingUseCase = get(),
            stopRecordingUseCase = get(),
            cameraRepository = get(),
        )
    }
}
