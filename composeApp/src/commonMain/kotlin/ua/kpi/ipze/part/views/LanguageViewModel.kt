package ua.kpi.ipze.part.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import ua.kpi.ipze.part.services.preferences.PrefManager
import ua.kpi.ipze.part.services.preferences.rememberPreferenceManager
import ua.kpi.ipze.part.views.providers.rememberLanguageViewModel
import java.util.Locale

class LanguageViewModel(private val prefManager: PrefManager) : ViewModel() {
    var localeState: Locale by mutableStateOf(
        Locale.forLanguageTag(PrefManager.DEFAULT_LANGUAGE)
    )
        private set

    init {
        getPersistedLocale()
    }

    private fun getPersistedLocale() {
        viewModelScope.launch {
            val languageCode =
                prefManager.getLanguagePreference().first()
            localeState = Locale.forLanguageTag(languageCode)
        }
    }

    fun setAppLanguage(languageCode: String) {
        val newLocale = Locale.forLanguageTag(languageCode)
        viewModelScope.launch {
            prefManager.setLanguagePreference(languageCode)
        }
        localeState = newLocale
    }
}

@Composable
fun LanguageViewModel.ProvideLanguageState(content: @Composable () -> Unit) {
    this.ApplyLanguageState(content)
}

@Composable
internal fun LanguageViewModel.ApplyLanguageState(content: @Composable () -> Unit) {
    Locale.setDefault(localeState)
    content()
}


@Composable
fun localizedStringResource(
    id: StringResource,
    viewModel: LanguageViewModel,
    vararg formatArgs: Array<out Any>
): String {
    val localeTag =
        rememberLanguageViewModel(rememberPreferenceManager()).localeState.toLanguageTag()
    key(localeTag) {
        return stringResource(id, *formatArgs)
    }
}
