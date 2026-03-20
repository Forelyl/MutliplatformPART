package ua.kpi.ipze.part.views.providers

import androidx.compose.runtime.Composable
import ua.kpi.ipze.part.services.preferences.PrefManager
import ua.kpi.ipze.part.views.LanguageViewModel

@Composable
expect fun rememberLanguageViewModel(prefManager: PrefManager): LanguageViewModel