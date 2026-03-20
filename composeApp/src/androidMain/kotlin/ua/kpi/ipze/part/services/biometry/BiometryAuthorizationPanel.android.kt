package ua.kpi.ipze.part.services.biometry

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mutliplatformpart.composeapp.generated.resources.Res
import mutliplatformpart.composeapp.generated.resources.fingerprint_description
import mutliplatformpart.composeapp.generated.resources.fingerprint_icon
import mutliplatformpart.composeapp.generated.resources.fingerprint_title
import org.jetbrains.compose.resources.painterResource
import ua.kpi.ipze.part.providers.BasePageData
import ua.kpi.ipze.part.views.localizedStringResource

@Composable
actual fun BiometryAuthorizationPanel(
    data: BasePageData,
    promptManager: Biometry,
    onError: () -> Unit,
    onSuccess: () -> Unit
) {
    val biometricResult by promptManager.promptResult.collectAsState(initial = null)
    val addNewBiometricLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { }
    )

    LaunchedEffect(biometricResult) {
        if (biometricResult is BiometricResult.BiometryNotSet) {
            if (Build.VERSION.SDK_INT >= 30) {
                val addNewBiometric =
                    Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                        putExtra(
                            Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                        )
                    }
                addNewBiometricLauncher.launch(addNewBiometric)
            }
        }
    }

    val bioTitle =
        localizedStringResource(Res.string.fingerprint_title, data.languageViewModel)
    val bioDesc =
        localizedStringResource(Res.string.fingerprint_description, data.languageViewModel)
    IconButton(
        onClick = {
            promptManager.showBiometricPrompt(
                title = bioTitle,
                description = bioDesc
            )
            biometricResult?.let { result ->
                when (result) {
                    is BiometricResult.BiometryError,
                    BiometricResult.BiometryNotSet,
                    BiometricResult.FeatureUnavailable,
                    BiometricResult.HardwareUnavailable,
                    BiometricResult.NotOwner -> onError()

                    BiometricResult.Owner -> onSuccess()
                }
            }
        }, modifier = Modifier
            .background(Color.Transparent)
            .size(120.dp)
    ) {
        Image(
            painter = painterResource(Res.drawable.fingerprint_icon),
            contentDescription = null
        )
    }
}