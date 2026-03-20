package ua.kpi.ipze.part.services.paletteApi.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ua.kpi.ipze.part.services.paletteApi.PaletteRequest

private val Tag = PaletteViewModel::class.simpleName ?: ""

class PaletteViewModel : ViewModel() {

    var colors: List<List<Int>> = emptyList()

    fun fetchColors(
        onResult: (List<List<Int>>) -> Unit,
        onError: (Throwable) -> Unit = {}
    ) {
        Logger.d(Tag) { "start" }
        viewModelScope.launch {
            try {
                Logger.d(Tag) { "inside" }
                colors = PaletteRequest.fetchPalette()
                Logger.d(Tag) { colors.toString() }
                onResult(colors)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}


// ----------------------------
// TODO Remove?

@Serializable
data class ColorSchemeRequest(val colors: List<List<Int>>)

fun colorSchemeToJson(colors: List<List<Int>>): String =
    Json.encodeToString(ColorSchemeRequest(colors))
