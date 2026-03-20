package ua.kpi.ipze.part.services.drawing

import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.unit.toOffset

actual suspend fun PointerInputScope.drawAndPanPointerInput(
    canvasOffset: MutableState<Offset>,
    scaling: MutableState<Float>,
    image: ImageBitmap,
    canvasSize: Size,
    handleDrawLine: (start: Offset, end: Offset) -> Unit
) {
    awaitPointerEventScope {
        var isDrawing = false
        var lastBitmapPos: Offset? = null

        while (true) {
            val event = awaitPointerEvent()
            val change = event.changes.first()

            // ── Scroll wheel → zoom ──────────────────────────────────────────
            val scrollDelta = change.scrollDelta
            if (scrollDelta != Offset.Zero) {
                val zoomFactor = if (scrollDelta.y < 0) 1.1f else 0.9f
                val mousePos = change.position

                val oldScale = scaling.value
                scaling.value = (oldScale * zoomFactor).coerceIn(0.1f, 40f)
                val effectiveZoom = scaling.value / oldScale
                canvasOffset.value += (mousePos - canvasOffset.value) * (1 - effectiveZoom)

                // Normalizing
                val realWidth = image.width * scaling.value
                val realHeight = image.height * scaling.value
                val negativeMultiple = 0.8f
                val positiveMultiple = 1 - negativeMultiple

                canvasOffset.value = Offset(
                    canvasOffset.value.x.coerceIn(
                        -realWidth * negativeMultiple,
                        canvasSize.width - realWidth * positiveMultiple
                    ),
                    canvasOffset.value.y.coerceIn(
                        -realHeight * negativeMultiple,
                        canvasSize.height - realHeight * positiveMultiple
                    )
                )
                change.consume()
                continue
            }

            // ── Right-click drag → pan ───────────────────────────────────────

            if (event.buttons.isSecondaryPressed) {
                isDrawing = false
                lastBitmapPos = null

                val delta = change.position - change.previousPosition
                if (delta != Offset.Zero) {
                    canvasOffset.value += delta

                    // Normalizing
                    val realWidth = image.width * scaling.value
                    val realHeight = image.height * scaling.value
                    val negativeMultiple = 0.8f
                    val positiveMultiple = 1 - negativeMultiple

                    canvasOffset.value = Offset(
                        canvasOffset.value.x.coerceIn(
                            -realWidth * negativeMultiple,
                            canvasSize.width - realWidth * positiveMultiple
                        ),
                        canvasOffset.value.y.coerceIn(
                            -realHeight * negativeMultiple,
                            canvasSize.height - realHeight * positiveMultiple
                        )
                    )
                }
                change.consume()
                continue
            }

            // ----------------------- Left-click → draw -----------------------
            if (event.buttons.isPrimaryPressed) {
                change.consume()
                val bitmapPos = screenToBitmap(
                    change.position,
                    canvasOffset.value,
                    scaling.value,
                    image
                )
                isDrawing = !bitmapPos.isOvershot
                lastBitmapPos = bitmapPos.lastValidOffset.toOffset()

                if (isDrawing) {
                    handleDrawLine(
                        lastBitmapPos,
                        lastBitmapPos
                    )
                }
                continue
            } else {
                isDrawing = false
                lastBitmapPos = null
                change.consume()
            }

            if (event.type == PointerEventType.Move) {
                change.consume()
                val bitmapPos = screenToBitmap(
                    change.position,
                    canvasOffset.value,
                    scaling.value,
                    image
                )
                val wasDrawing = isDrawing
                isDrawing = !bitmapPos.isOvershot

                if ((isDrawing || wasDrawing) && lastBitmapPos != null) {
                    handleDrawLine(
                        lastBitmapPos,
                        bitmapPos.lastValidOffset.toOffset()
                    )
                }
                lastBitmapPos = bitmapPos.lastValidOffset.toOffset()
            }
        }
    }
}