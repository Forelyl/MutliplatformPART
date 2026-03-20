package ua.kpi.ipze.part.providers

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController
import ua.kpi.ipze.part.services.geogetter.view.LocationViewModel
import ua.kpi.ipze.part.views.DatabaseViewModel
import ua.kpi.ipze.part.views.LanguageViewModel

class BasePageData(
    val innerPadding: PaddingValues, val nav: NavController,
    val languageViewModel: LanguageViewModel,
    val databaseViewModel: DatabaseViewModel,
    val locationViewModel: LocationViewModel
)

val BasePageDataProvider =
    compositionLocalOf<BasePageData> { error("${BasePageData::class.simpleName} provider not provided") }