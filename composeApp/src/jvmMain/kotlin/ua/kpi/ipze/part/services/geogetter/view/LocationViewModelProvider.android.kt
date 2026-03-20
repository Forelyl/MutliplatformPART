package ua.kpi.ipze.part.services.geogetter.view

import androidx.compose.runtime.Composable

private val model by lazy { LocationViewModel() }

@Composable
actual fun rememberLocationViewModel(): LocationViewModel = model