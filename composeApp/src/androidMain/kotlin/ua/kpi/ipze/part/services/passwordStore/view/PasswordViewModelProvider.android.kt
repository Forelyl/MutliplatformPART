package ua.kpi.ipze.part.services.passwordStore.view

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
actual fun rememberPasswordViewModel(): PasswordViewModel {
    val passwordViewModel: PasswordViewModel = viewModel()
    return passwordViewModel
}