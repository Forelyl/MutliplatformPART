package ua.kpi.ipze.part.services.paletteApi.view

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
actual fun rememberPaletteViewModel(): PaletteViewModel {
    val model: PaletteViewModel = viewModel()
    return model
}