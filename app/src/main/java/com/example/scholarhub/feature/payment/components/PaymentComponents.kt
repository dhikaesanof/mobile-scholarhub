package com.example.scholarhub.feature.payment.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scholarhub.R
import com.example.scholarhub.ui.components.PrimaryButton
import com.example.scholarhub.ui.components.cardSurface
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.NavyStrong
import com.example.scholarhub.ui.theme.Ocean
import com.example.scholarhub.ui.theme.OutlineCool
import com.example.scholarhub.ui.theme.OutlineMist
import com.example.scholarhub.ui.theme.OutlineSky
import com.example.scholarhub.ui.theme.ScholarHubTheme
import com.example.scholarhub.ui.theme.SurfaceCard
import com.example.scholarhub.ui.theme.TextStrong
import com.example.scholarhub.ui.theme.TextVariant
import com.example.scholarhub.ui.theme.InterSemiBold
import com.example.scholarhub.ui.theme.nunitoSans

// Frame Detail Pembayaran di-scale ×1.031 dari grid; nilai dipakai apa adanya (lihat docs/mentee-ui.md).
// Teks Inter di Figma dipetakan ke Nunito Sans dengan ukuran, line height, dan tracking yang sama.
internal val PaymentCardShape = RoundedCornerShape(12.369.dp)

/** Label section huruf kapital, misalnya "RINGKASAN SESI" (Semi Bold 12.37, tracking 0.62, #006398). */
@Composable
fun PaymentSectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        modifier = modifier,
        style = nunitoSans(12.369f, 16.492f, InterSemiBold, tracking = 0.6185f),
        color = Ocean,
    )
}

@Immutable
data class PaymentMethodUi(
    val id: String,
    val title: String,
    val description: String,
    @DrawableRes val icon: Int,
    val iconWidth: Dp = 20.615.dp,
    val iconHeight: Dp = 20.615.dp,
    val providers: List<String> = emptyList(),
)

/**
 * Kartu metode pembayaran (Figma `Label - Opsi`). Terpilih: radio #006398 dengan titik putih,
 * judul Navy, badge provider #E0E9EF tebal. Tidak terpilih: opacity 90%, radio #DBE4EA,
 * judul #141D21, badge #E6EFF5 tipis.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PaymentMethodCard(
    method: PaymentMethodUi,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (selected) 1f else 0.9f)
            .cardSurface(shape = PaymentCardShape)
            .clickable(role = Role.RadioButton, onClick = onClick)
            .padding(16.492.dp),
        verticalArrangement = Arrangement.spacedBy(8.246.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.246.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(20.615.dp)
                        .clip(CircleShape)
                        .background(if (selected) Ocean else OutlineCool),
                    contentAlignment = Alignment.Center,
                ) {
                    if (selected) {
                        Box(Modifier.size(8.246.dp).clip(CircleShape).background(SurfaceCard))
                    }
                }
                Column {
                    Text(
                        text = method.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.554.sp, lineHeight = 24.738.sp),
                        color = if (selected) Navy else TextStrong,
                    )
                    Text(
                        text = method.description,
                        style = nunitoSans(12.369f, 16.492f, FontWeight.Normal),
                        color = TextVariant,
                    )
                }
            }
            Image(
                painter = painterResource(method.icon),
                contentDescription = null,
                modifier = Modifier.size(method.iconWidth, method.iconHeight),
            )
        }
        if (method.providers.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.padding(top = 4.123.dp),
                horizontalArrangement = Arrangement.spacedBy(4.123.dp),
                verticalArrangement = Arrangement.spacedBy(4.123.dp),
            ) {
                method.providers.forEach { provider ->
                    Text(
                        text = provider,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.123.dp))
                            .background(if (selected) OutlineMist else OutlineSky)
                            .then(
                                if (selected) {
                                    Modifier.padding(horizontal = 10.308.dp, vertical = 4.123.dp)
                                } else {
                                    Modifier.padding(horizontal = 8.246.dp, vertical = 2.062.dp)
                                },
                            ),
                        style = nunitoSans(
                            10.308f,
                            14.431f,
                            if (selected) InterSemiBold else FontWeight.Normal,
                            tracking = 0.4123f,
                        ),
                        color = if (selected) TextStrong else TextVariant,
                    )
                }
            }
        }
    }
}

/**
 * Tombol bayar (Figma: #0F2A66, tinggi 41, shadow solid 3.09 dp #08193D, ikon dompet).
 * Dipakai "Bayar Sekarang" di Detail Pembayaran dan "Lanjut Bayar" di Riwayat Transaksi.
 */
@Composable
fun PayButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    PrimaryButton(
        text = text,
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(41.dp),
        containerColor = Navy,
        solidShadow = PayButtonShadow,
        shadowOffset = 3.092.dp,
        contentPadding = PaddingValues(horizontal = 20.81.dp),
        textStyle = MaterialTheme.typography.labelLarge,
        leadingContent = {
            Image(
                painter = painterResource(R.drawable.ic_pay_wallet),
                contentDescription = null,
                modifier = Modifier.size(width = 14.688.dp, height = 13.915.dp),
            )
        },
        iconGap = 6.185.dp,
    )
}

private val PayButtonShadow = Color(0xFF08193D)

/** Figma `215:6371` hanya menyediakan QRIS & E-Wallet. */
val SamplePaymentMethods = listOf(
    PaymentMethodUi(
        id = "qris",
        title = "QRIS & E-Wallet",
        description = "Bebas biaya admin",
        icon = R.drawable.ic_pay_qris,
        providers = listOf("QRIS", "GoPay", "OVO", "ShopeePay", "DANA"),
    ),
)

@Preview(showBackground = true, backgroundColor = 0xFFF1F1F1, widthDp = 402)
@Composable
private fun PaymentMethodCardPreview() {
    ScholarHubTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            PaymentMethodCard(SamplePaymentMethods[0], selected = true, onClick = {})
        }
    }
}
