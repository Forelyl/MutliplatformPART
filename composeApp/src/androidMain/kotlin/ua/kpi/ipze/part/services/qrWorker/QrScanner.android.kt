package ua.kpi.ipze.part.services.qrWorker

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import mutliplatformpart.composeapp.generated.resources.Res
import mutliplatformpart.composeapp.generated.resources.scanner_icon
import org.jetbrains.compose.resources.painterResource

actual const val DoesPlatformSupportQrScan: Boolean = true

@Composable
actual fun ScanQrButton(onResult: (String) -> Unit) {
    val launcher = rememberLauncherForActivityResult(ScanContract()) { result ->
        result?.contents?.let { qrJson ->
            onResult(qrJson)
        }
    }
    IconButton(
        onClick = {
            val options = ScanOptions().apply {
                setPrompt("Scan QR Code")
                setBeepEnabled(false)
                setOrientationLocked(true)
                setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                addExtra("SCAN_ORIENTATION_PORTRAIT", true)
            }
            launcher.launch(options)
        }, modifier = Modifier
            .background(Color.LightGray)
            .size(30.dp)
    ) {
        Icon(
            painter = painterResource(Res.drawable.scanner_icon),
            contentDescription = null,
            tint = Color(0xff232323)
        )
    }
}
