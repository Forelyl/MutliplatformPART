package ua.kpi.ipze.part.services.drawing.platform

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toSkiaRect
import org.jetbrains.skia.BlendMode
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Image
import org.jetbrains.skia.Paint

actual class PlatformCanvas actual constructor(platformBitmap: PlatformBitmap) {
    private var canvas: Canvas = Canvas(platformBitmap.bitmap)

    actual fun fill(color: Color) {
        canvas.clear(
            color.toArgb()
        )
    }


    // TODO - optimize with having drawRects function or/and drawPixelizedLine to remove repeated paint construction
    actual fun drawRect(rect: Rect, color: Color) {
        canvas.drawRect(
            rect.toSkiaRect(), Paint().also {
                it.color = color.toArgb()
                it.isAntiAlias = false
                it.isDither = false
            }
        )
    }


    actual fun drawBitmap(platformBitmap: PlatformBitmap) {
        canvas.drawImage(
            Image.makeFromBitmap(platformBitmap.bitmap), 0f, 0f
        )
    }

    // TODO - add clearRects function or/and clearPixelizedLine
    actual fun clearRect(rect: Rect) {
        canvas.drawRect(
            rect.toSkiaRect(),
            clearingPaint
        )
    }

    // ----------------------------------------------------------

    companion object {
        private val clearingPaint = Paint().apply {
            blendMode = BlendMode.CLEAR
            isAntiAlias = false
        }

    }
}