package ua.kpi.ipze.part.services.paletteApi.view

import androidx.compose.runtime.Composable

private val model by lazy { PaletteViewModel() }

@Composable
actual fun rememberPaletteViewModel(): PaletteViewModel = model