package ua.kpi.ipze.part.services.biometry

import kotlinx.coroutines.flow.Flow

sealed interface BiometricResult {
    data object HardwareUnavailable : BiometricResult
    data object FeatureUnavailable : BiometricResult
    data class BiometryError(val error: String) : BiometricResult
    data object NotOwner : BiometricResult
    data object Owner : BiometricResult
    data object BiometryNotSet : BiometricResult
}

expect val DoesPlatformSupportBiometry: Boolean

expect class Biometry {
    val promptResult: Flow<BiometricResult>
    fun showBiometricPrompt(title: String, description: String, negative: String = "Cancel")
}