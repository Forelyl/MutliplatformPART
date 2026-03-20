package ua.kpi.ipze.part

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.crossfade
import ua.kpi.ipze.part.database.ArtDatabaseProvider
import ua.kpi.ipze.part.router.AppRouter
import ua.kpi.ipze.part.services.biometry.Biometry
import ua.kpi.ipze.part.services.geogetter.view.LocationViewModel
import ua.kpi.ipze.part.services.geogetter.view.rememberLocationViewModel
import ua.kpi.ipze.part.services.passwordStore.view.PasswordViewModel
import ua.kpi.ipze.part.services.passwordStore.view.rememberPasswordViewModel
import ua.kpi.ipze.part.services.preferences.rememberPreferenceManager
import ua.kpi.ipze.part.ui.theme.PARTTheme
import ua.kpi.ipze.part.views.DatabaseViewModel
import ua.kpi.ipze.part.views.LanguageViewModel
import ua.kpi.ipze.part.views.ProvideLanguageState
import ua.kpi.ipze.part.views.providers.rememberDatabaseViewModel
import ua.kpi.ipze.part.views.providers.rememberLanguageViewModel

@Composable
fun App(promptManager: Biometry, locationViewModel: LocationViewModel?) {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .crossfade(true)
            .build()
    }

    val passwordViewModel: PasswordViewModel = rememberPasswordViewModel()
    val databaseViewModel: DatabaseViewModel = rememberDatabaseViewModel()
    databaseViewModel.initialize(ArtDatabaseProvider.database)
    val languageViewModel: LanguageViewModel =
        rememberLanguageViewModel(rememberPreferenceManager())
    val locationViewModelNonNull = locationViewModel ?: rememberLocationViewModel()

    PARTTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            languageViewModel.ProvideLanguageState {
                AppRouter(
                    innerPadding,
                    languageViewModel,
                    passwordViewModel,
                    databaseViewModel,
                    promptManager,
                    locationViewModelNonNull
                )
            }
        }
    }
}