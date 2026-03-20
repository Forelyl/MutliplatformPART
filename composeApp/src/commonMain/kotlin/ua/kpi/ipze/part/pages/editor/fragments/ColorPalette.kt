package ua.kpi.ipze.part.pages.editor.fragments

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import mutliplatformpart.composeapp.generated.resources.Res
import mutliplatformpart.composeapp.generated.resources.add_button_icon
import mutliplatformpart.composeapp.generated.resources.copy_button_icon
import mutliplatformpart.composeapp.generated.resources.menu_arrow_down
import mutliplatformpart.composeapp.generated.resources.share
import mutliplatformpart.composeapp.generated.resources.trash_bin
import org.jetbrains.compose.resources.painterResource
import ua.kpi.ipze.part.services.qrWorker.QRDialog
import ua.kpi.ipze.part.widgets.ColorPickerDialog

@Composable
fun ColorPaletteWithPicker(
    colors: List<Color>,
    onColorSelected: (Color) -> Unit,
    onColorAdded: (Color) -> Unit,
    onColorDeleted: (index: Int) -> Unit,
    onCloseClick: () -> Unit
) {
    var showColorPicker by remember { mutableStateOf(false) }

    Logger.d("red", message = { "redrown + ${colors.size}" })
    ColorPalette(
        colors = colors,
        onColorSelected = onColorSelected,
        onAddClick = { showColorPicker = true },
        onCopyClick = onColorAdded,
        onDeleteClick = onColorDeleted,
        onCloseClick = onCloseClick
    )

    if (showColorPicker) {
        ColorPickerDialog(
            onDismiss = { showColorPicker = false },
            onColorSelected = { color ->
                onColorAdded(color)
                showColorPicker = false
            }
        )
    }
}

@Composable
fun ColorPalette(
    colors: List<Color>,
    onColorSelected: (Color) -> Unit,
    onAddClick: () -> Unit,
    onCopyClick: (color: Color) -> Unit,
    onDeleteClick: (index: Int) -> Unit,
    onCloseClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    var selectedIndex by remember { mutableIntStateOf(0) }
    var showQrDialog by remember { mutableStateOf(false) }

    if (showQrDialog) {
        QRDialog(colors.map { color ->
            listOf(
                (color.red * 255).toInt(),
                (color.green * 255).toInt(),
                (color.blue * 255).toInt()
            )
        }
        ) {
            showQrDialog = false
        }
    }

    Surface(
        color = Color(0xff60646a),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Action buttons
            Logger.d("red", message = { "redrown2" })
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = onAddClick) {
                    Icon(
                        painter = painterResource(Res.drawable.add_button_icon),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                IconButton(onClick = {
                    onCopyClick(colors[selectedIndex])
                }) {
                    Icon(
                        painter = painterResource(Res.drawable.copy_button_icon),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                IconButton(onClick = { onDeleteClick(selectedIndex) }) {
                    Icon(
                        painter = painterResource(Res.drawable.trash_bin),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                IconButton(onClick = { showQrDialog = true }) {
                    Icon(
                        painter = painterResource(Res.drawable.share),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                IconButton(onClick = onCloseClick) {
                    Icon(
                        painter = painterResource(Res.drawable.menu_arrow_down),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            HorizontalDivider(
                color = Color(0xFF757575),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.Center
            ) {
                colors.forEachIndexed { index, color ->
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .padding(4.dp)
                            .background(color, RoundedCornerShape(4.dp))
                            .border(
                                width = if (index == selectedIndex) 3.dp else 1.dp,
                                color = if (index == selectedIndex) Color.White else Color(
                                    0xFF757575
                                ),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable {
                                onColorSelected(color)
                                selectedIndex = index
                            }
                    )
                }
            }
        }
    }
}