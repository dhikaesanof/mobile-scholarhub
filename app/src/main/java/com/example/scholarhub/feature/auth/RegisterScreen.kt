package com.example.scholarhub.feature.auth

import androidx.annotation.DrawableRes
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scholarhub.R
import com.example.scholarhub.feature.auth.components.AuthHeader
import com.example.scholarhub.feature.auth.components.AuthInputField
import com.example.scholarhub.ui.theme.NunitoFontFamily

enum class AuthRole(val label: String) {
    Mahasiswa("Mahasiswa"),
    Mentor("Mentor"),
}

private val BackgroundColor = Color(0xFFF1F1F1)
private val NavyDark = Color(0xFF0F2A66)
private val Navy = Color(0xFF001645)
private val Blue = Color(0xFF006398)
private val MutedText = Color(0xFF444650)
private val IconGrey = Color(0xFF757681)
private val RequiredRed = Color(0xFFBA1A1A)
private val SwitcherBg = Color(0xFFE0E9EF)

private val BodyStyle = TextStyle(fontFamily = NunitoFontFamily, fontSize = 14.43.sp, lineHeight = 20.6.sp)

/** Register Mahasiswa (Figma `130:5809`) dan Register Mentor (`130:5879`); keduanya hanya beda peran terpilih. */
@Composable
fun RegisterScreen(
    initialRole: AuthRole = AuthRole.Mahasiswa,
    onRegisterSuccess: (AuthRole) -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
) {
    var selectedRole by rememberSaveable { mutableStateOf(initialRole) }
    var fullName by rememberSaveable { mutableStateOf("") }
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
                .padding(start = 16.5.dp, end = 16.5.dp, top = 16.5.dp, bottom = 33.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.7.dp),
        ) {
            Column(
                modifier = Modifier.padding(top = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = "Mulai Perjalananmu!",
                    style = TextStyle(
                        fontFamily = NunitoFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.8.sp,
                        lineHeight = 35.sp,
                        letterSpacing = (-0.67).sp,
                        color = Navy,
                        textAlign = TextAlign.Center,
                    ),
                )
                Text(
                    text = "Daftar akun ScholarHub untuk eksplorasi beasiswa\nimpian dan terhubung dengan mentor terbaik.",
                    style = BodyStyle.copy(lineHeight = 23.45.sp, color = MutedText, textAlign = TextAlign.Center),
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SwitcherBg)
                    .padding(6.2.dp),
            ) {
                AuthRole.entries.forEach { role ->
                    val selected = role == selectedRole
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) Navy else Color.Transparent)
                            .clickable { selectedRole = role }
                            .padding(vertical = 10.3.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = role.label,
                            style = BodyStyle.copy(
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.14.sp,
                                color = if (selected) Color.White else MutedText,
                            ),
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.37.dp), spotColor = Color(0x1A000000))
                    .clip(RoundedCornerShape(12.37.dp))
                    .background(Color.White)
                    .padding(16.5.dp),
                verticalArrangement = Arrangement.spacedBy(16.5.dp),
            ) {
                RegisterField(
                    label = "Nama Lengkap",
                    required = true,
                    value = fullName,
                    onValueChange = { fullName = it },
                    placeholder = "Sarah Azhari",
                    leadingIcon = R.drawable.ic_auth_user,
                    cornerRadius = 8.25.dp,
                )
                RegisterField(
                    label = "Email",
                    required = true,
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "sarahazhari@gmail.com",
                    leadingIcon = R.drawable.ic_auth_email,
                    keyboardType = KeyboardType.Email,
                )
                RegisterField(
                    label = "Kata Sandi",
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Masukkan kata sandi",
                    leadingIcon = R.drawable.ic_auth_lock,
                    keyboardType = KeyboardType.Password,
                    isPassword = true,
                    passwordVisible = passwordVisible,
                    trailingIcon = {
                        IconButton(
                            onClick = { passwordVisible = !passwordVisible },
                            modifier = Modifier.size(24.dp),
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_auth_eye),
                                contentDescription = if (passwordVisible) "Sembunyikan kata sandi" else "Tampilkan kata sandi",
                                modifier = Modifier.size(width = 18.9.dp, height = 12.9.dp),
                                colorFilter = ColorFilter.tint(if (passwordVisible) Blue else IconGrey),
                            )
                        }
                    },
                )

                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .fillMaxWidth()
                        .height(49.5.dp)
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.37.dp), spotColor = Color(0x400F2A66))
                        .clip(RoundedCornerShape(12.37.dp))
                        .background(NavyDark)
                        .clickable { onRegisterSuccess(selectedRole) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Daftar Akun Sekarang",
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

            Row(
                modifier = Modifier.padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Sudah punya akun ScholarHub?",
                    style = BodyStyle.copy(fontWeight = FontWeight.Normal, color = MutedText),
                )
                Text(
                    text = "Masuk ke Akun",
                    modifier = Modifier.clickable(onClick = onNavigateToLogin),
                    style = BodyStyle.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.14.sp, color = Blue),
                )
            }
        }
    }
}

@Composable
private fun RegisterField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    @DrawableRes leadingIcon: Int,
    required: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    cornerRadius: Dp = 12.dp,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = BodyStyle.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 0.14.sp, color = Navy),
            )
            if (required) {
                Text(
                    text = "Wajib",
                    style = TextStyle(
                        fontFamily = NunitoFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.3.sp,
                        lineHeight = 14.4.sp,
                        letterSpacing = 0.41.sp,
                        color = RequiredRed,
                    ),
                )
            }
        }
        AuthInputField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            keyboardType = keyboardType,
            isPassword = isPassword,
            passwordVisible = passwordVisible,
            cornerRadius = cornerRadius,
            trailingIcon = trailingIcon,
        )
    }
}

@Preview(name = "Register Mahasiswa", showBackground = true, widthDp = 402, heightDp = 862)
@Composable
private fun RegisterMahasiswaPreview() {
    RegisterScreen(initialRole = AuthRole.Mahasiswa)
}

@Preview(name = "Register Mentor", showBackground = true, widthDp = 402, heightDp = 862)
@Composable
private fun RegisterMentorPreview() {
    RegisterScreen(initialRole = AuthRole.Mentor)
}
