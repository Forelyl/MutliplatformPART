package ua.kpi.ipze.part.services.drawing.view

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
actual fun rememberDrawingViewModel(): IDrawingViewModel {
    val model: DrawingViewModel = viewModel()
    return model
}