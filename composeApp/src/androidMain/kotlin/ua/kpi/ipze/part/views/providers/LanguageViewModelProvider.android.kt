package ua.kpi.ipze.part.views.providers

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import ua.kpi.ipze.part.services.preferences.PrefManager
import ua.kpi.ipze.part.views.LanguageViewModel

@Composable
actual fun rememberLanguageViewModel(prefManager: PrefManager): LanguageViewModel {
    val languageViewModel: LanguageViewModel = viewModel {
        LanguageViewModel(prefManager)
    }
    return languageViewModel
}