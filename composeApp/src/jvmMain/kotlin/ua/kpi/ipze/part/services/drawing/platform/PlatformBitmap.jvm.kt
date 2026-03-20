package ua.kpi.ipze.part.services.drawing.platform

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo


actual class PlatformBitmap private constructor(internal val bitmap: Bitmap) {
    actual val width: Int get() = bitmap.width
    actual val height: Int get() = bitmap.height

    actual constructor (width: Int, height: Int) : this(
        bitmap = Bitmap().apply {
            allocPixels(ImageInfo.makeN32Premul(width, height))
        })

    actual fun getColor(x: Int, y: Int): Int = bitmap.getColor(x, y)

    // TODO("Unsafe !!")
    actual fun toByteArray(): ByteArray = Image.makeFromBitmap(bitmap)
        .encodeToData(EncodedImageFormat.PNG)!!.bytes

    actual fun toImageBitmap(): ImageBitmap = bitmap.asComposeImageBitmap()

    actual companion object {
        actual fun decode(bytes: ByteArray): PlatformBitmap? = runCatching {
            PlatformBitmap(Bitmap.makeFromImage(Image.makeFromEncoded(bytes)))
        }.getOrNull()
    }
}