package ua.kpi.ipze.part.pages.newProject

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.touchlab.kermit.Logger
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import mutliplatformpart.composeapp.generated.resources.Res
import mutliplatformpart.composeapp.generated.resources.app_name
import mutliplatformpart.composeapp.generated.resources.bg_fill
import mutliplatformpart.composeapp.generated.resources.init_color_scheme
import mutliplatformpart.composeapp.generated.resources.init_layers_num
import mutliplatformpart.composeapp.generated.resources.make
import mutliplatformpart.composeapp.generated.resources.name
import mutliplatformpart.composeapp.generated.resources.new_art
import mutliplatformpart.composeapp.generated.resources.qr_code_icon
import mutliplatformpart.composeapp.generated.resources.reload
import mutliplatformpart.composeapp.generated.resources.size
import mutliplatformpart.composeapp.generated.resources.x_in_width
import org.jetbrains.compose.resources.painterResource
import ua.kpi.ipze.part.database.types.Layer
import ua.kpi.ipze.part.database.types.LayersList
import ua.kpi.ipze.part.database.types.PaletteList
import ua.kpi.ipze.part.database.types.Project
import ua.kpi.ipze.part.providers.BasePageDataProvider
import ua.kpi.ipze.part.router.EditorPageData
import ua.kpi.ipze.part.router.NewProjectPageData
import ua.kpi.ipze.part.services.paletteApi.view.rememberPaletteViewModel
import ua.kpi.ipze.part.services.qrWorker.QRDialog
import ua.kpi.ipze.part.services.qrWorker.ScanQrButton
import ua.kpi.ipze.part.services.qrWorker.jsonToColors
import ua.kpi.ipze.part.ui.theme.pixelBorder
import ua.kpi.ipze.part.ui.theme.topBottomBorder
import ua.kpi.ipze.part.views.localizedStringResource
import ua.kpi.ipze.part.widgets.ColorPickerDialog


@OptIn(ExperimentalStdlibApi::class)
@Composable
fun NewProjectPage() {

    val data = BasePageDataProvider.current

    val paletteViewModel = rememberPaletteViewModel()
    val scrollStateV = rememberScrollState()
    val scrollStateH = rememberScrollState()

    var name by remember { mutableStateOf("New project") }
    var width by remember { mutableStateOf("25") }
    var height by remember { mutableStateOf("25") }
    var numLayers by remember { mutableStateOf("1") }
    var colorScheme by remember {
        mutableStateOf<List<List<Int>>>(
            listOf(
                listOf(255, 255, 255)
            )
        )
    }
    var selectedBg by remember { mutableStateOf<Color>(Color(0xffffffff)) }
    var showQrDialog by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    if (showQrDialog) {
        QRDialog(colorScheme) {
            showQrDialog = false
        }
    }

    if (showColorPicker) {
        ColorPickerDialog(
            onDismiss = { showColorPicker = false },
            onColorSelected = { color ->
                selectedBg = color
                showColorPicker = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xff232323))
            .padding(15.dp)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .windowInsetsPadding(WindowInsets.ime)
            .verticalScroll(scrollStateV),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = localizedStringResource(Res.string.app_name, data.languageViewModel),
            color = Color(0xffffffff),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 20.sp
        )

        Text(
            text = localizedStringResource(Res.string.new_art, data.languageViewModel),
            fontSize = 30.sp,
            color = Color(0xffffffff),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Column {
            Text(
                text = localizedStringResource(Res.string.name, data.languageViewModel),
                fontSize = 20.sp,
                color = Color(0xffffffff),
                modifier = Modifier.padding(start = 4.dp)
            )
            BasicTextField(
                value = name, onValueChange = { name = it },
                modifier = Modifier
                    .pixelBorder(
                        backgroundColor = Color(0xff53565A),
                        borderWidth = 4.dp
                    )
                    .padding(4.dp)
                    .fillMaxWidth(),
                textStyle = TextStyle(color = Color(0xffffffff))
            )
        }
        Column {
            Text(
                text = localizedStringResource(Res.string.size, data.languageViewModel),
                fontSize = 20.sp,
                color = Color(0xffffffff),
                modifier = Modifier.padding(start = 4.dp)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = width, onValueChange = { width = it },
                    modifier = Modifier
                        .pixelBorder(
                            backgroundColor = Color(0xff53565A),
                            borderWidth = 4.dp
                        )
                        .padding(4.dp)
                        .weight(0.2f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = TextStyle(color = Color(0xffffffff))
                )
                Icon(
                    painter = painterResource(Res.drawable.x_in_width), contentDescription = null,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .size(15.dp)
                )
                BasicTextField(
                    value = height, onValueChange = { height = it },
                    modifier = Modifier
                        .pixelBorder(
                            backgroundColor = Color(0xff53565A),
                            borderWidth = 4.dp
                        )
                        .padding(4.dp)
                        .weight(0.2f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = TextStyle(color = Color(0xffffffff))
                )
            }
        }

        Column {
            Text(
                text = localizedStringResource(
                    Res.string.init_color_scheme,
                    data.languageViewModel
                ),
                fontSize = 20.sp,
                color = Color(0xffffffff),
                modifier = Modifier.padding(start = 4.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                colorScheme.forEach { item ->
                    Box(
                        modifier = Modifier
                            .pixelBorder(backgroundColor = Color(0xffffffff), borderWidth = 4.dp)
                            .size(30.dp)
                            .background(
                                Color(
                                    red = item[0],
                                    green = item[1],
                                    blue = item[2]
                                )
                            )
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .horizontalScroll(scrollStateH),
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                ScanQrButton { qrJson ->
                    runCatching {
                        val jsonArray = Json.parseToJsonElement(qrJson)
                        colorScheme = jsonToColors(jsonArray.jsonObject["colors"].toString())
                    }
                }

                IconButton(
                    onClick = {
                        paletteViewModel.fetchColors({
                            colorScheme = it
                        })
                    }, modifier = Modifier
                        .background(Color.LightGray)
                        .size(30.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.reload),
                        contentDescription = null,
                        tint = Color(0xff232323)
                    )
                }

                // Show QR Code button
                IconButton(
                    onClick = { showQrDialog = true },
                    modifier = Modifier
                        .background(Color.LightGray)
                        .size(30.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.qr_code_icon),
                        contentDescription = null,
                        tint = Color(0xff232323)
                    )
                }
            }
        }

        Column {
            Text(
                text = localizedStringResource(Res.string.init_layers_num, data.languageViewModel),
                fontSize = 20.sp,
                color = Color(0xffffffff),
                modifier = Modifier.padding(start = 4.dp)
            )
            BasicTextField(
                value = numLayers, onValueChange = { numLayers = it },
                modifier = Modifier
                    .pixelBorder(
                        backgroundColor = Color(0xff53565A),
                        borderWidth = 4.dp
                    )
                    .padding(4.dp)
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = TextStyle(color = Color(0xffffffff))
            )
        }

        Column {
            Text(
                text = localizedStringResource(Res.string.bg_fill, data.languageViewModel),
                fontSize = 20.sp,
                color = Color(0xffffffff),
                modifier = Modifier.padding(start = 4.dp)
            )
            Box(
                modifier = Modifier
                    .pixelBorder(
                        backgroundColor = Color(0xff53565A),
                        borderWidth = 4.dp
                    )
                    .padding(4.dp)
                    .fillMaxWidth()
                    .clickable(onClick = {
                        showColorPicker = true
                    })
            ) {
                Text(
                    text = selectedBg.value.toHexString(HexFormat {
                        upperCase = false
                        number.prefix = "#"
                        number.minLength = 8
                        number.removeLeadingZeros = true
                    }).slice(0..8),
                    modifier = Modifier
                        .background(color = selectedBg)
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp),
                    fontSize = 20.sp, textAlign = TextAlign.Center, color = Color(0xff000000)
                )
            }
        }

        val city by data.locationViewModel.location.collectAsState()

        Spacer(modifier = Modifier.weight(0.8f))
        Box(
            modifier = Modifier
                .topBottomBorder(4.dp, Color(0xffffffff), Color(0xff53565A))
                .clickable(onClick = {
                    val listLayer = List(numLayers.toIntOrNull() ?: 1) { index ->
                        Layer(
                            name = "Layer $index",
                            visibility = true,
                            lock = false,
                            imageData = ByteArray(0)
                        )
                    }

                    val project = Project(
                        layers = LayersList(emptyList()),
                        width = width.toIntOrNull() ?: 1,
                        height = height.toIntOrNull() ?: 1,
                        name = name,
                        lastSettlement = city.toString(),
                        palette = PaletteList(colorScheme.map {
                            Color(it[0], it[1], it[2]).value.toLong()
                        }),
                        timer = 0,
                        lastModified = System.currentTimeMillis(),
                        baseColor = selectedBg.value.toLong(),
                        previewImageData = ByteArray(0),
                        drawingTime = 0
                    )
                    Logger.d("db_test", message = { "Palette ${project.palette}" })

                    scope.launch {
                        val id: Long = data.databaseViewModel.saveProject(project, listLayer) ?: 0
                        data.nav.navigate(
                            EditorPageData(
                                historyLength = 0,
                                id = id
                            )
                        ) {
                            popUpTo(NewProjectPageData) {
                                inclusive = true
                            }
                        }
                    }
                })

        ) {
            Text(
                text = localizedStringResource(Res.string.make, data.languageViewModel),
                color = Color(0xffffffff),
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
            )
        }
        Spacer(modifier = Modifier.weight(0.1f))
    }
}