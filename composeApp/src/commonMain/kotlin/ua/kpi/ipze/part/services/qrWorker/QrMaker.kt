package ua.kpi.ipze.part.services.qrWorker

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.circle
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.options.solid
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import ua.kpi.ipze.part.services.paletteApi.view.colorSchemeToJson
import ua.kpi.ipze.part.ui.theme.pixelBorder
import ua.kpi.ipze.part.ui.theme.topBottomBorder

@Composable
fun rememberGenerateQrCode(json: String, size: Int = 256): Painter {
    val qrCode = rememberQrCodePainter(json) {
        shapes {
            ball = QrBallShape.circle()
            darkPixel = QrPixelShape.roundCorners()
            frame = QrFrameShape.roundCorners(.25f)
        }
        colors {
            dark = QrBrush.solid(Color.Black)
            light = QrBrush.solid(Color.White)
            frame = QrBrush.solid(Color(100, 192, 117))
        }
    }
    return qrCode
}

fun jsonToColors(json: String): List<List<Int>> {
    val jsonArray = Json.parseToJsonElement(json).jsonArray
    return List(jsonArray.size) { i ->
        val inner = jsonArray[i].jsonArray
        List(inner.size) { j -> inner[j].jsonPrimitive.int }
    }
}

@Composable
fun QRDialog(colorScheme: List<List<Int>>, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .pixelBorder(backgroundColor = Color(0xff232323), borderWidth = 4.dp)
                .background(Color.White)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Color Scheme QR Code",
                    fontSize = 20.sp,
                    color = Color.Black
                )
                Image(
                    painter = rememberGenerateQrCode(colorSchemeToJson(colorScheme)),
                    contentDescription = "QR Code",
                    modifier = Modifier.size(300.dp)
                )
                Box(
                    modifier = Modifier
                        .topBottomBorder(4.dp, Color(0xff53565a), Color(0xff53565A))
                        .clickable { onDismiss() }
                        .padding(horizontal = 32.dp)
                ) {
                    Text(
                        text = "Close",
                        color = Color(0xffffffff),
                        fontSize = 18.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}