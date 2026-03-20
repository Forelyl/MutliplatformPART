package ua.kpi.ipze.part.services.preferences

import androidx.compose.runtime.Composable


private val model by lazy { PrefManager() }

@Composable
actual fun rememberPreferenceManager(): PrefManager = model

