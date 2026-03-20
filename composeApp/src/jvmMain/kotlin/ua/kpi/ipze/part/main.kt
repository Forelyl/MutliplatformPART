package ua.kpi.ipze.part

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ua.kpi.ipze.part.services.biometry.Biometry

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "MutliplatformPART",
    ) {
        App(promptManager = Biometry(), locationViewModel = null)
    }
}