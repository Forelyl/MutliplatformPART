package ua.kpi.ipze.part.services.drawing

import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.unit.dp
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
            val pointers = event.changes.filter { it.pressed }

            when (pointers.size) {
                1 -> { // Single-finger drawing
                    val change = pointers.first()
                    when (event.type) {
                        PointerEventType.Press -> {
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
                        }

                        PointerEventType.Move -> {
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


                        PointerEventType.Release -> {
                            isDrawing = false
                            lastBitmapPos = null
                            change.consume()
                        }
                    }
                }

                2 -> { // Two-finger pan
                    isDrawing = false
                    lastBitmapPos = null

                    val p1 = pointers[0]
                    val p2 = pointers[1]
                    p1.consume()
                    p2.consume()

                    if ((p1.position - p2.position).getDistance() < 72.dp.toPx()) {
                        break
                    }

                    // Move: midpoint delta
                    val prevMid = (p1.previousPosition + p2.previousPosition) / 2f
                    val currentMid = (p1.position + p2.position) / 2f
                    val delta = currentMid - prevMid
                    canvasOffset.value += delta

                    // Zoom: distance ratio
                    val prevDistance = (p1.previousPosition - p2.previousPosition).getDistance()
                    val currentDistance = (p1.position - p2.position).getDistance()
                    val zoomFactor = currentDistance / prevDistance

                    val oldScale = scaling.value
                    scaling.value = (oldScale * zoomFactor).coerceIn(0.1f, 40f)
                    val effectiveZoom = scaling.value / oldScale
                    canvasOffset.value += (currentMid - canvasOffset.value) * (1 - effectiveZoom)

                    // Normalizing
                    val realWidth = image.width * scaling.value
                    val realHeight = image.height * scaling.value
                    val negativeMultiple = 0.8f
                    val positiveMultiple = 1 - negativeMultiple


                    canvasOffset.value =
                        Offset(
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

                else -> {
                    isDrawing = false
                    lastBitmapPos = null
                }
            }
        }
    }
}


