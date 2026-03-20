package ua.kpi.ipze.part.services.biometry

import androidx.compose.runtime.Composable
import ua.kpi.ipze.part.providers.BasePageData

@Composable
expect fun BiometryAuthorizationPanel(
    data: BasePageData,
    promptManager: Biometry,
    onError: () -> Unit,
    onSuccess: () -> Unit
)