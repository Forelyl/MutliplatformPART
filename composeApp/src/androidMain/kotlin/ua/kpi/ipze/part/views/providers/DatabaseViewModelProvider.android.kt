package ua.kpi.ipze.part.views.providers

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import ua.kpi.ipze.part.views.DatabaseViewModel

@Composable
actual fun rememberDatabaseViewModel(): DatabaseViewModel {
    val databaseViewModel: DatabaseViewModel = viewModel()
    return databaseViewModel
}