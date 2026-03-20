package ua.kpi.ipze.part.services.drawing.platform

import androidx.compose.ui.graphics.ImageBitmap


expect class PlatformBitmap(width: Int, height: Int) {
    val width: Int
    val height: Int

    fun getColor(x: Int, y: Int): Int
    fun toByteArray(): ByteArray
    fun toImageBitmap(): ImageBitmap

    companion object {
        fun decode(bytes: ByteArray): PlatformBitmap?
    }
}