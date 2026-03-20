package ua.kpi.ipze.part.services.drawing.platform

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toAndroidRectF
import androidx.compose.ui.graphics.toArgb

actual class PlatformCanvas actual constructor(platformBitmap: PlatformBitmap) {
    private var canvas: Canvas = Canvas(platformBitmap.bitmap)

    actual fun fill(color: Color) = canvas.drawPaint(Paint().also {
        it.color = color.toArgb()
    })


    // TODO - optimize with having drawRects function or/and drawPixelizedLine to remove repeated paint construction
    actual fun drawRect(rect: Rect, color: Color) =
        canvas.drawRect(
            rect.toAndroidRectF(), Paint().also {
                it.color = color.toArgb()
                it.isAntiAlias = false
                it.isFilterBitmap = false
                it.isDither = false
            }
        )


    actual fun drawBitmap(platformBitmap: PlatformBitmap) = canvas.drawBitmap(
        platformBitmap.bitmap, 0f, 0f, bitmapPaint
    )

    // TODO - add clearRects function or/and clearPixelizedLine
    actual fun clearRect(rect: Rect) =
        canvas.drawRect(
            rect.toAndroidRectF(),
            clearingPaint
        )


    // ----------------------------------------------------------

    companion object {
        private val clearingPaint = Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            isAntiAlias = false
        }

        private val bitmapPaint = Paint().apply {
            isFilterBitmap = false
            isAntiAlias = false
        }
    }
}