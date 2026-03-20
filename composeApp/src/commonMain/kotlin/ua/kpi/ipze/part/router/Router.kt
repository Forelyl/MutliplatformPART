package ua.kpi.ipze.part.router

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import ua.kpi.ipze.part.pages.editor.EditorPage
import ua.kpi.ipze.part.pages.gallery.GalleryPage
import ua.kpi.ipze.part.pages.login.LoginPage
import ua.kpi.ipze.part.pages.login.NewPasswordPage
import ua.kpi.ipze.part.pages.newProject.NewProjectPage
import ua.kpi.ipze.part.providers.BasePageData
import ua.kpi.ipze.part.providers.BasePageDataProvider
import ua.kpi.ipze.part.services.biometry.Biometry
import ua.kpi.ipze.part.services.geogetter.view.LocationViewModel
import ua.kpi.ipze.part.services.passwordStore.view.PasswordViewModel
import ua.kpi.ipze.part.views.DatabaseViewModel
import ua.kpi.ipze.part.views.LanguageViewModel

@Composable
fun AppRouter(
    innerPadding: PaddingValues,
    languageViewModel: LanguageViewModel,
    passwordViewModel: PasswordViewModel,
    databaseViewModel: DatabaseViewModel,
    promptManager: Biometry,
    locationViewModel: LocationViewModel
) {
    val navController = rememberNavController()
    val basicPageData = remember {
        BasePageData(
            innerPadding,
            navController,
            languageViewModel,
            databaseViewModel,
            locationViewModel
        )
    }

    CompositionLocalProvider(BasePageDataProvider provides basicPageData) {
        NavHost(
            navController = navController,
            startDestination = LoginPageData,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                )
            }
        ) {
            composable<LoginPageData> {
                if (passwordViewModel.passwordExists) LoginPage(passwordViewModel, promptManager)
                else NewPasswordPage(passwordViewModel)
            }
            composable<NewProjectPageData> { NewProjectPage() }
            composable<GalleryPageData> { GalleryPage(passwordViewModel) }
            composable<EditorPageData> {
                val data = it.toRoute<EditorPageData>()
                EditorPage(data)
            }
        }
    }
}
