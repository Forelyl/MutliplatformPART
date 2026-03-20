package ua.kpi.ipze.part.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import mutliplatformpart.composeapp.generated.resources.Res
import mutliplatformpart.composeapp.generated.resources.pixel_font_7
import org.jetbrains.compose.resources.Font

@Composable
fun getAppTypography(): Typography {
    val appFont = FontFamily(
        Font(Res.font.pixel_font_7)
    )
    val typography = Typography(
        bodyLarge = TextStyle(
            fontFamily = appFont
        ),
        bodyMedium = TextStyle(
            fontFamily = appFont
        ),
        bodySmall = TextStyle(
            fontFamily = appFont
        ),
        titleLarge = TextStyle(
            fontFamily = appFont
        ),
        titleMedium = TextStyle(
            fontFamily = appFont
        ),
        titleSmall = TextStyle(
            fontFamily = appFont
        )
    )
    return typography
}
