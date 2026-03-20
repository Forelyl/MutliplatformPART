package ua.kpi.ipze.part.services.geogetter.view

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
actual fun rememberLocationViewModel(): LocationViewModel {
    val model: LocationViewModel = viewModel()
    return model
}