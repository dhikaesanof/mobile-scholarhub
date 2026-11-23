package com.example.scholarhub.feature.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scholarhub.R
import com.example.scholarhub.feature.auth.components.AuthHeader
import com.example.scholarhub.feature.auth.components.AuthInputField
import com.example.scholarhub.ui.theme.NunitoFontFamily

private val BackgroundColor = Color(0xFFF1F1F1)
private val NavyDark = Color(0xFF0F2A66)
private val Blue = Color(0xFF006398)
private val LabelColor = Color(0xFF141D21)
private val MutedText = Color(0xFF444650)
private val DividerColor = Color(0xFFE0E9EF)

private val LabelStyle = TextStyle(
    fontFamily = NunitoFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 12.37.sp,
    lineHeight = 16.5.sp,
    letterSpacing = 0.25.sp,
    color = LabelColor,
)

/** Login (Figma `130:5758`). */
@Composable
fun LoginScreen(
    onLoginSuccess: (email: String, isMentor: Boolean) -> Unit = { _, _ -> },
    onNavigateToRegister: () -> Unit = {},
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        containerColor = BackgroundColor,
        topBar = { AuthHeader() },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.auth_mascot),
                contentDescription = "Maskot ScholarHub",
                modifier = Modifier.size(width = 192.5.dp, height = 273.6.dp),
                contentScale = ContentScale.Fit,
            )

            Column(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth()
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(16.5.dp),
                        spotColor = Color(0x140F2A66),
                        ambientColor = Color(0x140F2A66),
                    )
                    .clip(RoundedCornerShape(16.5.dp))
                    .background(Color.White)
                    .padding(16.5.dp),
                verticalArrangement = Arrangement.spacedBy(24.7.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.5.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.2.dp)) {
                        Text(text = "Email", style = LabelStyle)
                        AuthInputField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = "Masukkan email Anda",
                            leadingIcon = R.drawable.ic_auth_email,
                            keyboardType = KeyboardType.Email,
                            cornerRadius = 12.37.dp,
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.2.dp)) {
                        Text(text = "Kata Sandi", style = LabelStyle)
                        AuthInputField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = "Masukkan kata sandi akun",
                            leadingIcon = R.drawable.ic_auth_lock,
                            isPassword = true,
                            passwordVisible = passwordVisible,
                            keyboardType = KeyboardType.Password,
                            cornerRadius = 12.37.dp,
                            trailingIcon = {
                                IconButton(
                                    onClick = { passwordVisible = !passwordVisible },
                                    modifier = Modifier.size(37.dp),
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.ic_auth_eye),
                                        contentDescription = if (passwordVisible) "Sembunyikan kata sandi" else "Tampilkan kata sandi",
                                        modifier = Modifier.size(width = 18.9.dp, height = 12.9.dp),
                                        colorFilter = ColorFilter.tint(if (passwordVisible) Blue else MutedText),
                                    )
                                }
                            },
                        )
                    }

                    Box(
                        modifier = Modifier
                            .padding(top = 8.25.dp)
                            .fillMaxWidth()
                            .height(49.5.dp)
                            .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.37.dp), spotColor = Color(0x400F2A66))
                            .clip(RoundedCornerShape(12.37.dp))
                            .background(NavyDark)
                            .clickable { onLoginSuccess(email, email.contains("mentor", ignoreCase = true)) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Masuk ke Akun",
                            style = TextStyle(
                                fontFamily = NunitoFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp,
                                lineHeight = 19.5.sp,
                                color = Color.White,
                            ),
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(DividerColor),
                )
            }

            Row(
                modifier = Modifier.padding(16.5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                val footerStyle = TextStyle(fontFamily = NunitoFontFamily, fontSize = 12.37.sp, lineHeight = 16.5.sp)
                Text(
                    text = "Belum punya akun ScholarHub?",
                    style = footerStyle.copy(fontWeight = FontWeight.Normal, color = MutedText),
                )
                Text(
                    text = "Daftar Sekarang",
                    modifier = Modifier.clickable(onClick = onNavigateToRegister),
                    style = footerStyle.copy(fontWeight = FontWeight.Bold, color = Blue),
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 862)
@Composable
private fun LoginScreenPreview() {
    LoginScreen()
}
