package ua.kpi.ipze.part.services.filesystem

import java.awt.FileDialog
import java.awt.Frame
import java.io.File

actual fun writePngToGallery(name: String, pngAsByteArray: ByteArray) {
    chooseSaveLocation(name)?.writeBytes(pngAsByteArray)
}

private fun chooseSaveLocation(defaultName: String): File? {
    val dialog = FileDialog(null as Frame?, "Save Image", FileDialog.SAVE).apply {
        file = "$defaultName.png"
        directory = System.getProperty("user.home") + "/Pictures"
    }
    dialog.isVisible = true
    return if (dialog.file != null) File(dialog.directory, dialog.file) else null
}