package ua.kpi.ipze.part.services.passwordStore.view

import androidx.compose.runtime.Composable

private val model by lazy { PasswordViewModel() }

@Composable
actual fun rememberPasswordViewModel(): PasswordViewModel = model
