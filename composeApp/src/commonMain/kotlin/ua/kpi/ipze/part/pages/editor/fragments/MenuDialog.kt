package ua.kpi.ipze.part.pages.editor.fragments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import mutliplatformpart.composeapp.generated.resources.Res
import mutliplatformpart.composeapp.generated.resources.app_name
import mutliplatformpart.composeapp.generated.resources.close_x_icon
import mutliplatformpart.composeapp.generated.resources.export
import mutliplatformpart.composeapp.generated.resources.language
import mutliplatformpart.composeapp.generated.resources.projects
import mutliplatformpart.composeapp.generated.resources.save
import org.jetbrains.compose.resources.painterResource
import ua.kpi.ipze.part.providers.BasePageDataProvider
import ua.kpi.ipze.part.router.GalleryPageData
import ua.kpi.ipze.part.services.drawing.view.IDrawingViewModel
import ua.kpi.ipze.part.services.filesystem.writePngToGallery
import ua.kpi.ipze.part.ui.theme.topBottomBorder
import ua.kpi.ipze.part.views.localizedStringResource

@Composable
fun MenuDialog(drawingViewModel: IDrawingViewModel, onDismiss: () -> Unit) {
    val data = BasePageDataProvider.current
//    val activity = MainActivityDataProvider.current
    val languageViewModel = BasePageDataProvider.current.languageViewModel
    var language by remember { mutableStateOf(languageViewModel.localeState.toLanguageTag()) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF303030),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(30.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = localizedStringResource(Res.string.app_name, data.languageViewModel),
                        color = Color(0xffffffff),
                        textAlign = TextAlign.Center,
                        fontSize = 30.sp,
                        modifier = Modifier
                            .padding(start = 30.dp)
                            .weight(0.9f)
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(30.dp)
                            .weight(0.1f)
                            .padding(bottom = 1.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.close_x_icon),
                            contentDescription = null,
                            tint = Color(0xffffffff),
                        )
                    }
                }

//                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .clickable {
                            writePngToGallery(
                                drawingViewModel.getProjectName(),
                                drawingViewModel.toPng()
                            )
                            onDismiss()
                        }
                        .topBottomBorder(
                            strokeWidth = 4.dp,
                            borderColor = Color(0xffffffff),
                            backgroundColor = Color.Transparent
                        )
                        .padding(vertical = 10.dp)
                ) {
                    Text(
                        text = localizedStringResource(Res.string.export, data.languageViewModel),
                        color = Color(0xffffffff),
                        fontSize = 16.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column(
                    modifier = Modifier
//                        .fillMaxWidth()
                        .clickable {
                            drawingViewModel.saveProject()
                            onDismiss()
                        }
                        .topBottomBorder(
                            strokeWidth = 4.dp,
                            borderColor = Color(0xffffffff),
                            backgroundColor = Color.Transparent
                        )
                        .padding(vertical = 10.dp)
                ) {
                    Text(
                        text = localizedStringResource(Res.string.save, data.languageViewModel),
                        color = Color(0xffffffff),
                        fontSize = 16.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column(
                    modifier = Modifier
                        .clickable {
                            val currentDestination = data.nav.currentBackStackEntry?.destination
                            data.nav.navigate(GalleryPageData) {
                                popUpTo(currentDestination?.id ?: 0) { inclusive = true }
                            }
                        }
                        .topBottomBorder(
                            strokeWidth = 4.dp,
                            borderColor = Color(0xffffffff),
                            backgroundColor = Color.Transparent
                        )
                        .padding(vertical = 10.dp)
                ) {
                    Text(
                        text = localizedStringResource(Res.string.projects, data.languageViewModel),
                        color = Color(0xffffffff),
                        fontSize = 16.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Row(
                    modifier = Modifier
                        .clickable {
                            language =
                                if (languageViewModel.localeState.toLanguageTag() == "uk") {
                                    "en"
                                } else {
                                    "uk"
                                }
                            languageViewModel.setAppLanguage(language)
                        }
                        .topBottomBorder(
                            strokeWidth = 4.dp,
                            borderColor = Color(0xffffffff),
                            backgroundColor = Color.Transparent
                        )
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        text = localizedStringResource(Res.string.language, data.languageViewModel),
                        color = Color(0xffffffff),
                        fontSize = 16.sp,
                        modifier = Modifier.weight(0.9f)
                    )
                    Text(
                        text = language.uppercase(),
                        color = Color(0xffffffff),
                        fontSize = 16.sp,
                        modifier = Modifier.weight(0.1f)
                    )
                }

                val location by data.locationViewModel.location.collectAsState()
                Text(
                    text = location.toString(),
                    color = Color(0xFF9E9E9E),
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}