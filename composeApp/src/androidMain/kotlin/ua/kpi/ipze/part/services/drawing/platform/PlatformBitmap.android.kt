package ua.kpi.ipze.part.services.drawing.platform

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.createBitmap
import androidx.core.graphics.get
import java.io.ByteArrayOutputStream


actual class PlatformBitmap private constructor(internal val bitmap: Bitmap) {
    actual val width: Int get() = bitmap.width
    actual val height: Int get() = bitmap.height

    actual constructor (width: Int, height: Int) : this(
        bitmap = createBitmap(width, height)
    )

    actual fun getColor(x: Int, y: Int): Int = bitmap[x, y]

    actual fun toByteArray(): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    actual fun toImageBitmap(): ImageBitmap = bitmap.asImageBitmap()

    actual companion object {
        actual fun decode(bytes: ByteArray): PlatformBitmap? = runCatching {
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.copy(Bitmap.Config.ARGB_8888, true)
                ?.let { PlatformBitmap(it) }
        }.getOrNull()
    }
}