package ua.kpi.ipze.part.views.providers

import androidx.compose.runtime.Composable
import ua.kpi.ipze.part.views.DatabaseViewModel

private val model by lazy { DatabaseViewModel() }

@Composable
actual fun rememberDatabaseViewModel(): DatabaseViewModel = model
