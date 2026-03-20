package ua.kpi.ipze.part.services.drawing.platform

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color

/**
 * Drawing operations are pixel-first (no antialiasing, no filtering)
 */
expect class PlatformCanvas(platformBitmap: PlatformBitmap) {
    fun fill(color: Color)
    fun drawRect(rect: Rect, color: Color)
    fun clearRect(rect: Rect)
    fun drawBitmap(platformBitmap: PlatformBitmap)
}