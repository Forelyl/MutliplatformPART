package ua.kpi.ipze.part.services.qrWorker

import androidx.compose.runtime.Composable

expect val DoesPlatformSupportQrScan: Boolean

@Composable
expect fun ScanQrButton(onResult: (String) -> Unit)

