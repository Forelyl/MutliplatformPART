package ua.kpi.ipze.part.services.preferences

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
actual fun rememberPreferenceManager(): PrefManager {
    val model: PrefManager = viewModel()
    return model
}

