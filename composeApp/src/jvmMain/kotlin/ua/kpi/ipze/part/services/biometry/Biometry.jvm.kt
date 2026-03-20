package ua.kpi.ipze.part.services.biometry

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

actual val DoesPlatformSupportBiometry: Boolean = false

actual class Biometry {
    private val resultChannel = Channel<BiometricResult>()
    actual val promptResult: Flow<BiometricResult> = resultChannel.receiveAsFlow()

    actual fun showBiometricPrompt(
        title: String,
        description: String,
        negative: String
    ) {
        resultChannel.trySend(BiometricResult.FeatureUnavailable)
    }
}