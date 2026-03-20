package ua.kpi.ipze.part.services.qrWorker

import androidx.compose.runtime.Composable

actual const val DoesPlatformSupportQrScan: Boolean = false

@Composable
actual fun ScanQrButton(onResult: (String) -> Unit) {
    return
}

