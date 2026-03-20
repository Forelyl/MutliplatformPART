package ua.kpi.ipze.part.services.drawing.view


import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ua.kpi.ipze.part.database.types.Layer
import ua.kpi.ipze.part.database.types.PaletteList
import ua.kpi.ipze.part.database.types.Project
import ua.kpi.ipze.part.services.drawing.platform.PlatformBitmap
import ua.kpi.ipze.part.services.drawing.platform.PlatformCanvas
import ua.kpi.ipze.part.views.DatabaseProjectWithLayers
import ua.kpi.ipze.part.views.DatabaseViewModel
import java.lang.Thread.sleep
import java.time.Duration
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.abs

private val Tag = DrawingViewModel::class.simpleName ?: ""

val defaultBitmap = PlatformBitmap(100, 100)

class DrawingViewModel : IDrawingViewModel() {
    private val initialized = AtomicBoolean(false)
    private val ready = MutableStateFlow(false)

    private val cleanupScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun initialize(
        historyLength: UInt,
        pixelsPerPixelCell: UInt,
        id: Long,
        databaseViewModel: DatabaseViewModel,
        closePageOnFailure: () -> Unit
    ) {
        Logger.d(Tag) { "Alpha" }
        if (!initialized.compareAndSet(false, true)) {
            Logger.e(Tag, message = { "Got second init" })
            return
        }
        Logger.d(Tag) { "Betta" }


        this.operativeData = OperativeData(viewModelScope)
        this.historyLength = historyLength
        amountOfSteps = MutableStateFlow(DrawingAmountOfSteps(0u, 0u))

        viewModelScope.launch(Dispatchers.IO) {
            databaseView = databaseViewModel
            val data = databaseView.getProjectWithLayers(id)
            if (data == null) {
                Logger.e(Tag, message = { "Failed to get value from db for id: $id" })
                viewModelScope.launch(Dispatchers.Main) {
                    closePageOnFailure()
                }
                return@launch
            }
            project = data.project
            widthAmountPixels = project.width.toUInt()
            heightAmountPixels = project.height.toUInt()

            val bitmapResults = data.layers.mapIndexedNotNull { index, value ->
                getBitmap(
                    index,
                    value.id,
                    data,
                    pixelsPerPixelCell,
                    index == 0
                )
            }.reversed()
            if (bitmapResults.size != data.layers.size) {
                closePageOnFailure()
                return@launch
            }
            bitmaps = bitmapResults.toMutableList()
            canvases = bitmaps.map { PlatformCanvas(it) }.toMutableList()

            realPixelsPerDrawPixel = pixelsPerPixelCell
            ready.value = true
            triggerRedraw()


            palette.value = data.project.palette.paletteList.map { Color(it.toULong()) }
            layers.value = data.layers
            operativeData.setTime(data.project.drawingTime)
            activeLayerIndex.value = 0u

        }

        // blocking coroutines
        viewModelScope.launch {
            layers.combine(activeLayerIndex) { layersList, index ->
                val i = index.toInt()
                val layer = if (i in layersList.indices) layersList[i] else null

                if (layer == null) null
                else CurrentActiveLayer(layer = layer, indexInArray = index)
            }.collect({ activeLayer.value = it })
        }

        startAutoSaves(Duration.ofMinutes(1))
    }

    override fun isReady(): StateFlow<Boolean> = ready.asStateFlow()

    override fun onCleared() {
        cleanupScope.launch {
            saveStep()
            operativeData.stop()
            cleanupScope.cancel()
        }
        super.onCleared()
    }

    private fun getBitmap(
        index: Int,
        id: Long,
        data: DatabaseProjectWithLayers,
        pixelsPerPixelCell: UInt,
        isInitial: Boolean = false
    ): PlatformBitmap? {
        if (data.layers[index].imageData.isEmpty()) {
            val newBitmap = PlatformBitmap(
                (widthAmountPixels * pixelsPerPixelCell).toInt(),
                (heightAmountPixels * pixelsPerPixelCell).toInt()
            )
            PlatformCanvas(newBitmap).fill(
                if (isInitial) Color(data.project.baseColor.toULong())
                else Color.Transparent
            )
            return newBitmap
        }

        val decodedBitmap = PlatformBitmap.decode(data.layers[index].imageData)
        if (decodedBitmap == null) {
            Logger.e(Tag, message = { "Failed to decode 1st layer from array id: $id" })
            return null
        }

        return decodedBitmap
    }

    private fun startAutoSaves(interval: Duration) {
        viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                saveStep()
                sleep(interval.toMillis())
            }
        }
    }

    // ----------------------------------------------------
    // data

    private lateinit var project: Project
    private lateinit var bitmaps: MutableList<PlatformBitmap>
    private lateinit var canvases: MutableList<PlatformCanvas>
    private lateinit var amountOfSteps: MutableStateFlow<DrawingAmountOfSteps>
    private var historyLength: UInt = 0u
    private var widthAmountPixels: UInt = 0u
    private var heightAmountPixels: UInt = 0u
    private var realPixelsPerDrawPixel: UInt = 0u
    private lateinit var operativeData: OperativeData
    private lateinit var databaseView: DatabaseViewModel

    private val palette = MutableStateFlow<List<Color>>(emptyList())

    private val layers = MutableStateFlow<List<Layer>>(emptyList())
    private val activeLayerIndex = MutableStateFlow<UInt>(0u)

    private val activeLayer = MutableStateFlow<CurrentActiveLayer?>(null)

    // ----------------------------------------------------
    override fun getLayers(): StateFlow<List<Layer>> = layers.asStateFlow()
    override fun getCurrentActiveLayerIndex(): StateFlow<UInt> = activeLayerIndex.asStateFlow()
    override fun getRealPixelsPerDrawingPixel(): UInt = realPixelsPerDrawPixel

    override fun getCurrentActiveLayer(): MutableStateFlow<CurrentActiveLayer?> = activeLayer

    override fun getProjectName(): String {
        if (!ready.value) return ""
        return project.name
    }

    override fun addLayer(layer: Layer) {
        if (!ready.value) return

        layers.update {
            bitmaps.add(
                0,
                PlatformBitmap(
                    project.width * realPixelsPerDrawPixel.toInt(),
                    project.height * realPixelsPerDrawPixel.toInt()
                )

            )
            canvases.add(0, PlatformCanvas(bitmaps.first()))
            return@update listOf(layer) + it
        }
        triggerRedraw()
    }

    override fun deleteLayer(index: UInt) {
        if (!ready.value) return
        if (index.toInt() >= bitmaps.size) return

        layers.update {
            bitmaps.removeAt(index.toInt())
            canvases.removeAt(index.toInt())
            it.toMutableList().filterIndexed { indexFilter, _ -> indexFilter != index.toInt() }
                .toList()
        }
        triggerRedraw()
    }

    override fun swapLayers(a: UInt, b: UInt) {
        if (!ready.value) return
        layers.update {
            val layersAmount = it.size.toUInt()
            if (a >= layersAmount || b >= layersAmount || a == b) {
                return@update it
            }
            val tempBitmap = bitmaps[a.toInt()]
            bitmaps[a.toInt()] = bitmaps[b.toInt()]
            bitmaps[b.toInt()] = tempBitmap

            val tempCanvas = canvases[a.toInt()]
            canvases[a.toInt()] = canvases[b.toInt()]
            canvases[b.toInt()] = tempCanvas

            return@update it.toMutableList().apply {
                val tmp = this[a.toInt()]
                this[a.toInt()] = this[b.toInt()]
                this[b.toInt()] = tmp
            }.toList()
        }
        triggerRedraw()

    }

    override fun setActiveLayer(index: UInt) {
        if (!ready.value) return
        activeLayerIndex.value = index
    }

    override fun setVisibilityOfLayer(index: UInt, isVisible: Boolean) {
        if (!ready.value) return

        val i = index.toInt()
        layers.value = layers.value.mapIndexed { idx, layer ->
            if (idx == i) layer.copy(visibility = isVisible)
            else layer
        }
        triggerRedraw()
    }

    override fun setLockOnLayer(index: UInt, isLocked: Boolean) {
        if (!ready.value) return

        val i = index.toInt()
        layers.value = layers.value.mapIndexed { idx, layer ->
            if (idx == i) layer.copy(lock = isLocked)
            else layer
        }
    }

    override fun setLayerName(index: UInt, name: String) {
        if (!ready.value) return

        val i = index.toInt()
        layers.value = layers.value.mapIndexed { idx, layer ->
            if (idx == i) layer.copy(name = name)
            else layer
        }
    }

    override fun getPalette(): StateFlow<List<Color>> = palette.asStateFlow()

    override fun setPalette(colors: List<Color>) {
        palette.value = colors
    }

    // ----------------------------------------------------

    override fun saveProject() {
        cleanupScope.launch {
            saveStep()
        }
    }

    private suspend fun saveStep() {
        if (!ready.value) return
        databaseView.saveProject(
            project.copy(
                palette = PaletteList(
                    palette.value.map { it.value.toLong() },
                ), previewImageData = toPng(),
                drawingTime = operativeData.time.value
            ),
            layers.value.mapIndexed { index, layer ->
                val bitmap = bitmaps[index]
                layer.copy(
                    imageData = bitmap.toByteArray()
                )
            }.toList()
        )
    }

    private fun triggerRedraw() {
        // wrap-overflow is desirable
        __INTERNAL_bitmapVersion.value += 1u
    }

    // Internal

    override fun __INTERNAL_getCachedBitmapImage(): List<CachedBitmapImage> {
        if (!ready.value) return listOf(
            CachedBitmapImage(
                defaultBitmap.toImageBitmap(),
                true
            )
        )
        return bitmaps.mapIndexed { index, it ->
            CachedBitmapImage(
                it.toImageBitmap(),
                layers.value.getOrNull(index)?.visibility ?: false
            )
        }
    }

    override val __INTERNAL_bitmapVersion = MutableStateFlow(0u)

    // ----------------------------------------------------f
    // Draw

    private fun bresenhamLine(
        x0: Int, y0: Int,
        x1: Int, y1: Int,
    ): List<IntOffset> {
        val result = mutableListOf<IntOffset>()
        var x = x0
        var y = y0

        val dx = abs(x1 - x0)
        val dy = abs(y1 - y0)

        val sx = if (x0 < x1) 1 else -1
        val sy = if (y0 < y1) 1 else -1

        var err = dx - dy

        while (true) {
            result.add(IntOffset(x, y))
            if (x == x1 && y == y1) break

            val e2 = 2 * err
            if (e2 > -dy) {
                err -= dy
                x += sx
            }
            if (e2 < dx) {
                err += dx
                y += sy
            }
        }

        return result.toList()
    }


    override fun drawLine(start: Offset, end: Offset, color: Color) {
        if (!ready.value) return
        if (activeLayerIndex.value.toInt() >= canvases.size) {
            Logger.e(
                Tag,
                message = {
                    "Skipping drawing line, because queried canvas " +
                            "(index: ${activeLayerIndex.value}) is out of bounds of array of canvases"
                }
            )
        }
        if (layers.value[activeLayerIndex.value.toInt()].lock) return


        val startScaled = Offset(
            start.x / this.realPixelsPerDrawPixel.toInt(),
            start.y / this.realPixelsPerDrawPixel.toInt()
        )
        val endScaled = Offset(
            end.x / this.realPixelsPerDrawPixel.toInt(),
            end.y / this.realPixelsPerDrawPixel.toInt()
        )

        val scaledPixels = bresenhamLine(
            startScaled.x.toInt(),
            startScaled.y.toInt(),
            endScaled.x.toInt(),
            endScaled.y.toInt()
        )

        scaledPixels.forEach {
            val topLeft =
                Offset(
                    it.x * realPixelsPerDrawPixel.toFloat(),
                    it.y * realPixelsPerDrawPixel.toFloat()
                )
            canvases[activeLayerIndex.value.toInt()].drawRect(
                Rect(
                    topLeft.x,
                    topLeft.y,
                    topLeft.x + realPixelsPerDrawPixel.toFloat(),
                    topLeft.y + realPixelsPerDrawPixel.toFloat(),
                ), color
            )
        }
        triggerRedraw()
    }

    override fun clearLine(start: Offset, end: Offset) {
        if (!ready.value) return
        if (activeLayerIndex.value.toInt() >= canvases.size) {
            Logger.i(
                Tag,
                message = {
                    "Skipping cleaning line, because queried canvas " +
                            "(index: ${activeLayerIndex.value}) is out of bounds of array of canvases"
                }
            )
        }

        val startScaled = Offset(
            start.x / realPixelsPerDrawPixel.toInt(),
            start.y / realPixelsPerDrawPixel.toInt()
        )
        val endScaled = Offset(
            end.x / realPixelsPerDrawPixel.toInt(),
            end.y / realPixelsPerDrawPixel.toInt()
        )

        val scaledPixels = bresenhamLine(
            startScaled.x.toInt(),
            startScaled.y.toInt(),
            endScaled.x.toInt(),
            endScaled.y.toInt()
        )

        scaledPixels.forEach {
            val topLeft = Offset(
                it.x * realPixelsPerDrawPixel.toFloat(),
                it.y * realPixelsPerDrawPixel.toFloat()
            )

            canvases[activeLayerIndex.value.toInt()].clearRect(
                Rect(
                    topLeft.x,
                    topLeft.y,
                    topLeft.x + realPixelsPerDrawPixel.toInt(),
                    topLeft.y + realPixelsPerDrawPixel.toInt()
                )
            )
        }

        triggerRedraw()
    }

    // ----------------------------------------------------


    override fun pickColorAt(offset: IntOffset): Color {
        if (!ready.value) return Color.Transparent
        if (activeLayerIndex.value.toInt() >= canvases.size) {
            Logger.i(
                Tag,
                message = {
                    "Skipping picking color, because queried canvas " +
                            "(index: ${activeLayerIndex.value}) is out of bounds of array of canvases"
                }
            )
        }

        Logger.d(
            Tag,
            message = {
                "Get colour from ${offset.x}, ${offset.y} and bitmap size is ${bitmaps[activeLayerIndex.value.toInt()].width}, ${bitmaps[activeLayerIndex.value.toInt()].height}"
            }
        )

        if ((widthAmountPixels * realPixelsPerDrawPixel).toInt() <= offset.x || offset.x < 0) return Color.Transparent
        if ((heightAmountPixels * realPixelsPerDrawPixel).toInt() <= offset.y || offset.y < 0) return Color.Transparent
        return Color(bitmaps[activeLayerIndex.value.toInt()].getColor(offset.x, offset.y))
    }

    // ----------------------------------------------------

    override fun startMovePixels(selectArea: Rect) {
        if (!ready.value) return

    }

    override fun movePixels(deltaPath: Offset) {
        if (!ready.value) return

    }

    override fun endMovePixels() {
        if (!ready.value) return

    }

    // ----------------------------------------------------


    override fun stepBack() {
        if (!ready.value) return

    }

    override fun stepForward() {
        if (!ready.value) return

    }

    override fun getAmountOfSteps(): StateFlow<DrawingAmountOfSteps> {
        if (!ready.value) return TODO("a")
        return TODO("Provide the return value")
    }

    // ----------------------------------------------------

    override fun clearImage() {
        if (!ready.value) return

    }

    override fun toPng(): ByteArray {
        if (!ready.value) return ByteArray(0)
        val resultBitmap = PlatformBitmap(
            (widthAmountPixels * realPixelsPerDrawPixel).toInt(),
            (heightAmountPixels * realPixelsPerDrawPixel).toInt()
        )
        val canvas = PlatformCanvas(resultBitmap)


        bitmaps.reversed().forEach {
            canvas.drawBitmap(it)
        }

        return resultBitmap.toByteArray()
    }

    // ----------------------------------------------------


    override fun getWidthAmountPixels(): UInt {
        if (!ready.value) return 0u

        return widthAmountPixels
    }

    override fun getHeightAmountPixels(): UInt {
        if (!ready.value) return 0u

        return heightAmountPixels
    }

    override fun getOperativeData(): OperativeData {
        return operativeData
    }
}