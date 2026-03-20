package ua.kpi.ipze.part.pages.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mutliplatformpart.composeapp.generated.resources.Res
import mutliplatformpart.composeapp.generated.resources.app_name
import mutliplatformpart.composeapp.generated.resources.login
import mutliplatformpart.composeapp.generated.resources.login_background_narrow
import mutliplatformpart.composeapp.generated.resources.or
import mutliplatformpart.composeapp.generated.resources.password
import mutliplatformpart.composeapp.generated.resources.run
import org.jetbrains.compose.resources.painterResource
import ua.kpi.ipze.part.pages.login.fragments.PasswordInput
import ua.kpi.ipze.part.providers.BasePageDataProvider
import ua.kpi.ipze.part.router.GalleryPageData
import ua.kpi.ipze.part.services.biometry.Biometry
import ua.kpi.ipze.part.services.biometry.BiometryAuthorizationPanel
import ua.kpi.ipze.part.services.biometry.DoesPlatformSupportBiometry
import ua.kpi.ipze.part.services.passwordStore.view.PasswordViewModel
import ua.kpi.ipze.part.ui.theme.topBottomBorder
import ua.kpi.ipze.part.views.localizedStringResource

@Composable
fun LoginPage(passwordViewModel: PasswordViewModel, promptManager: Biometry) {
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val data = BasePageDataProvider.current


    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                painter = painterResource(Res.drawable.login_background_narrow),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.35f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = localizedStringResource(Res.string.app_name, data.languageViewModel),
                    fontSize = 62.sp,
                    color = Color(0xffffffff),
                    letterSpacing = 10.sp
                )
            }

            Surface(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .windowInsetsPadding(WindowInsets.ime)
                    .verticalScroll(scrollState)
                    .fillMaxWidth()
                    .weight(0.8f),
                color = Color(0xff53565a),
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 48.dp)
                        .padding(top = 25.dp, bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = localizedStringResource(Res.string.login, data.languageViewModel),
                        color = Color(0xFFFEF3C7),
                        fontSize = 25.sp,
                        letterSpacing = 6.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 15.dp)
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PasswordInput(
                            label = localizedStringResource(
                                Res.string.password,
                                data.languageViewModel
                            ),
                            value = password,
                            onValueChange = {
                                password = it
                                showError = false
                            },
                            placeholder = "",
                            borderColor = if (showError) listOf(
                                Color(0xffe53e2e),
                                Color(0xff9e5f59)
                            )
                            else listOf(Color(0xffedb768), Color(0xff987d55))
                        )

                        Spacer(modifier = Modifier.height(50.dp))

                        Box(
                            modifier = Modifier
                                .topBottomBorder(4.dp, Color(0xffedb768), Color.Transparent)
                                .clickable(onClick = {
                                    passwordViewModel.passwordInput = password
                                    passwordViewModel.handleLogin()
                                    showError = !passwordViewModel.isAuthenticated
                                    if (passwordViewModel.isAuthenticated) {
                                        data.nav.navigate(GalleryPageData)
                                    }
                                })
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0x03edb768),
                                            Color(0x99edb768),
                                            Color(0x03edb768)
                                        )
                                    )
                                )
                        ) {
                            Text(
                                text = localizedStringResource(
                                    Res.string.run,
                                    data.languageViewModel
                                ),
                                color = Color(0xffffffff),
                                textAlign = TextAlign.Center,
                                fontSize = 30.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            )
                        }

                        if (DoesPlatformSupportBiometry) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(2.dp)
                                        .background(Color(0xffffffff))
                                )
                                Text(
                                    text = localizedStringResource(
                                        Res.string.or,
                                        data.languageViewModel
                                    ),
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(horizontal = 18.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(2.dp)
                                        .background(Color(0xffffffff))
                                )
                            }

                            BiometryAuthorizationPanel(
                                data,
                                promptManager,
                                onError = { showError = true },
                                onSuccess = {
                                    showError = false
                                    data.nav.navigate(GalleryPageData)
                                })
                        }
                    }
                }
            }
        }
    }
}