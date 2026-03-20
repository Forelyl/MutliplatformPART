package ua.kpi.ipze.part.services.drawing


import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import ua.kpi.ipze.part.services.drawing.platform.PlatformBitmap
import ua.kpi.ipze.part.services.drawing.platform.PlatformCanvas
import ua.kpi.ipze.part.services.drawing.view.CachedBitmapImage
import ua.kpi.ipze.part.services.drawing.view.IDrawingViewModel

@Composable
private fun IDrawingViewModel.rememberImage(): List<CachedBitmapImage> {
    val changed by this.__INTERNAL_bitmapVersion.collectAsState()
    return remember(changed) { this.__INTERNAL_getCachedBitmapImage() }
}

@Composable
fun DrawCanvas(
    modifier: Modifier = Modifier,
    view: IDrawingViewModel,
    handleDrawLine: (start: Offset, end: Offset) -> Unit
) {

    val images = view.rememberImage()
    val scaling = remember { mutableFloatStateOf(1f) }
    val offset = remember { mutableStateOf(Offset.Zero) }
    var wasCentered by remember { mutableStateOf(false) }
    var canvasSize by remember { mutableStateOf(Size(1f, 1f)) }

    val firstOrNull = images.getOrNull(0)
    LaunchedEffect(firstOrNull?.image?.width, firstOrNull?.image?.height) {
        wasCentered = false
    }

    val realPixelsPerDrawingPixel = view.getRealPixelsPerDrawingPixel()
    val chessBoardBitmap = remember(realPixelsPerDrawingPixel, firstOrNull?.image) {
        val backgroundSize =
            IntSize(firstOrNull?.image?.width ?: 1, firstOrNull?.image?.height ?: 1)
        drawBackground(realPixelsPerDrawingPixel, backgroundSize)
    }


    if (images.isEmpty()) return
    val anyImage = images[0].image

    Canvas(
        modifier = modifier
            .clipToBounds()
            .pointerInput(Unit) {
                drawAndPanPointerInput(offset, scaling, anyImage, canvasSize, handleDrawLine)
            }
            .onSizeChanged {
                canvasSize = it.toSize()
                if (wasCentered) return@onSizeChanged
                wasCentered = true // correct code, ignore linter

                val scaleX = it.width.toFloat() / anyImage.width
                val scaleY = it.height.toFloat() / anyImage.height
                scaling.floatValue = minOf(scaleX, scaleY)

                val scaledWidth = anyImage.width * scaling.floatValue
                val scaledHeight = anyImage.height * scaling.floatValue
                offset.value = Offset(
                    x = (it.width - scaledWidth) / 2f,
                    y = (it.height - scaledHeight) / 2f
                )
            }
    ) {
        val drawBitmapImage = { image: ImageBitmap ->
            drawImage(
                image = image,
                srcOffset = IntOffset.Zero,
                srcSize = IntSize(
                    image.width,
                    image.height
                ),
                dstOffset = IntOffset(
                    offset.value.x.toInt(),
                    offset.value.y.toInt()
                ),
                dstSize = IntSize(
                    (anyImage.width * scaling.floatValue).toInt(),
                    (anyImage.height * scaling.floatValue).toInt()
                ),
                filterQuality = FilterQuality.None
            )
        }

        drawBitmapImage(chessBoardBitmap.toImageBitmap())
        images.asReversed().forEach {
            if (it.isVisible) drawBitmapImage(it.image)
        }
    }
}

private fun drawBackground(
    realPixelsPerDrawingPixel: UInt = 0u,
    backgroundPixelAmount: IntSize,
): PlatformBitmap {
    val result = PlatformBitmap(backgroundPixelAmount.width, backgroundPixelAmount.height)
    val drawer = PlatformCanvas(result)

    val realPixelAmount = realPixelsPerDrawingPixel.coerceAtLeast(1u).toInt()
    val cellsPerRow = backgroundPixelAmount.width / realPixelAmount
    val cellsPerColumn = backgroundPixelAmount.width / realPixelAmount

    // Chessboard pattern
    val grayColor = Color(0xFF9E9E9E)
    val whiteColor = Color(0xFFBDBDBD)

    for (row in 0 until cellsPerColumn) {
        for (col in 0 until cellsPerRow) {
            val isGray = (row + col) % 2 == 0
            val rectangle = Rect(
                (row * realPixelAmount).toFloat(),
                (col * realPixelAmount).toFloat(),
                ((row + 1) * realPixelAmount).toFloat(),
                ((col + 1) * realPixelAmount).toFloat()
            )
            drawer.drawRect(
                rectangle,
                if (isGray) grayColor else whiteColor
            )
        }
    }

    return result
}

// Draw grid
//    for (i in 0..cellsPerRow) {
//        drawLine(
//            color = Color(0xFF757575),
//            start = offset + Offset(i * pixelSize, 0f),
//            end = offset + Offset(i * pixelSize, size.height),
//            strokeWidth = 3f
//        )
//    }
//
//    for (i in 0..cellsPerColumn) {
//        drawLine(
//            color = Color(0xFF757575),
//            start = offset + Offset(0f, i * pixelSize),
//            end = offset + Offset(size.width, i * pixelSize),
//            strokeWidth = 3f
//        )
//    }

internal data class ScreenToBitmapResult(
    val lastValidOffset: IntOffset,
    val isOvershot: Boolean
)

internal fun screenToBitmap(
    screenPos: Offset,
    canvasOffset: Offset,
    scaling: Float,
    image: ImageBitmap
): ScreenToBitmapResult {
    val xReal = ((screenPos.x - canvasOffset.x) / scaling).toInt()
    val yReal = ((screenPos.y - canvasOffset.y) / scaling).toInt()

    val xClipped = xReal.coerceIn(0, image.width - 1)
    val yClipped = yReal.coerceIn(0, image.height - 1)

    return ScreenToBitmapResult(
        lastValidOffset = IntOffset(
            xClipped,
            yClipped
        ), isOvershot = xClipped != xReal || yClipped != yReal
    )
}

expect suspend fun PointerInputScope.drawAndPanPointerInput(
    canvasOffset: MutableState<Offset>,
    scaling: MutableState<Float>,
    image: ImageBitmap,
    canvasSize: Size,
    handleDrawLine: (start: Offset, end: Offset) -> Unit
)
