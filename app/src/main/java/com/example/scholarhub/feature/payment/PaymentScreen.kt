package com.example.scholarhub.feature.payment

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scholarhub.R
import com.example.scholarhub.feature.payment.components.PaymentCardShape
import com.example.scholarhub.feature.payment.components.PaymentMethodCard
import com.example.scholarhub.feature.payment.components.PaymentMethodUi
import com.example.scholarhub.feature.payment.components.PaymentSectionLabel
import com.example.scholarhub.feature.payment.components.SamplePaymentMethods
import com.example.scholarhub.feature.payment.components.PayButton
import com.example.scholarhub.ui.components.CroppedImage
import com.example.scholarhub.ui.components.FigmaCrop
import com.example.scholarhub.ui.components.ScholarHubDetailTopBar
import com.example.scholarhub.ui.components.cardSurface
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.Ocean
import com.example.scholarhub.ui.theme.OutlineSky
import com.example.scholarhub.ui.theme.ScholarHubTheme
import com.example.scholarhub.ui.theme.ShadowSubtle
import com.example.scholarhub.ui.theme.SkySoft
import com.example.scholarhub.ui.theme.TextStrong
import com.example.scholarhub.ui.theme.TextVariant
import com.example.scholarhub.ui.theme.InterSemiBold
import com.example.scholarhub.ui.theme.nunitoSans

/** Ringkasan sesi yang dibayar. Masih contoh statis sampai data sesi/transaksi tersedia. */
data class PaymentUiState(
    val serviceTitle: String,
    val mentorName: String,
    val mentorAffiliation: String,
    @DrawableRes val mentorPhoto: Int,
    val schedule: String,
    val price: String,
    val methods: List<PaymentMethodUi>,
)

val SamplePaymentUiState = PaymentUiState(
    serviceTitle = "Simulasi Wawancara",
    mentorName = "Samantha Dira, S.Pd",
    mentorAffiliation = "Awardee LPDP University of Oxford",
    mentorPhoto = R.drawable.mentor_avatar,
    schedule = "Sabtu, 25 Oktober 2026 • 19:30 - 20:30 WIB",
    price = "Rp 150.000",
    methods = SamplePaymentMethods,
)

private val ContentPadding = 16.492.dp
private val SectionLabelGap = 8.246.dp

/** Detail Pembayaran (Figma 846:4385): ringkasan sesi, pilihan metode, tombol "Bayar Sekarang". */
@Composable
fun PaymentScreen(
    state: PaymentUiState = SamplePaymentUiState,
    onBack: () -> Unit = {},
    onPay: (PaymentMethodUi) -> Unit = {},
) {
    var selectedId by rememberSaveable { mutableStateOf(state.methods.firstOrNull()?.id) }

    Scaffold(
        containerColor = Base,
        topBar = { ScholarHubDetailTopBar(title = "Pembayaran", onBack = onBack) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(start = ContentPadding, end = ContentPadding, top = ContentPadding, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(SectionLabelGap)) {
                PaymentSectionLabel("Ringkasan Sesi")
                OrderSummaryCard(state)
            }

            Column(verticalArrangement = Arrangement.spacedBy(SectionLabelGap)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    PaymentSectionLabel("Metode Pembayaran")
                    Row(horizontalArrangement = Arrangement.spacedBy(4.123.dp), verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.ic_pay_verified),
                            contentDescription = null,
                            modifier = Modifier.size(width = 10.308.dp, height = 12.885.dp),
                        )
                        Text(
                            text = "Otomatis Terverifikasi",
                            style = nunitoSans(10.308f, 14.431f, InterSemiBold, tracking = 0.4123f),
                            color = TextVariant,
                        )
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.246.dp)) {
                    state.methods.forEach { method ->
                        PaymentMethodCard(
                            method = method,
                            selected = method.id == selectedId,
                            onClick = { selectedId = method.id },
                        )
                    }
                }
            }

            PayButton(
                text = "Bayar Sekarang",
                onClick = { state.methods.firstOrNull { it.id == selectedId }?.let(onPay) },
            )
        }
    }
}

@Composable
private fun OrderSummaryCard(state: PaymentUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .cardSurface(shape = PaymentCardShape)
            .padding(16.492.dp),
        verticalArrangement = Arrangement.spacedBy(16.492.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.492.dp)) {
            // Figma: wadah 49.48 radius 12.37 #E6EFF5, foto 49 × 46 di dalamnya (crop 179% / −39.5%).
            Box(
                modifier = Modifier
                    .size(49.477.dp)
                    .shadow(1.dp, RoundedCornerShape(12.369.dp), ambientColor = ShadowSubtle, spotColor = ShadowSubtle)
                    .clip(RoundedCornerShape(12.369.dp))
                    .background(OutlineSky),
                contentAlignment = Alignment.Center,
            ) {
                // Cincin 2 dp #006398 @40% di sekeliling foto; terpotong sebagian oleh wadah seperti di Figma.
                Box(
                    modifier = Modifier
                        .size(width = 53.dp, height = 50.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(Ocean.copy(alpha = 0.4f)),
                )
                CroppedImage(
                    image = state.mentorPhoto,
                    width = 49.dp,
                    height = 46.dp,
                    crop = FigmaCrop(scaleY = 1.7902f, offsetY = -0.3951f),
                    contentDescription = "Foto ${state.mentorName}",
                    modifier = Modifier.clip(RoundedCornerShape(28.dp)),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.fillMaxWidth().height(51.023.dp), contentAlignment = Alignment.CenterStart) {
                    Text(
                        text = state.serviceTitle,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.554.sp, lineHeight = 25.512.sp),
                        color = Navy,
                        maxLines = 2,
                    )
                }
                Text(
                    text = state.mentorName,
                    modifier = Modifier.padding(top = 2.062.dp),
                    style = nunitoSans(12.369f, 16.492f, InterSemiBold, tracking = 0.2474f),
                    color = Ocean,
                )
                Text(
                    text = state.mentorAffiliation,
                    style = nunitoSans(12.369f, 16.492f, FontWeight.Normal),
                    color = TextVariant,
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.246.dp))
                .background(SkySoft)
                .padding(8.246.dp),
            horizontalArrangement = Arrangement.spacedBy(8.246.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_pay_calendar),
                contentDescription = null,
                modifier = Modifier.size(width = 15.462.dp, height = 16.235.dp),
            )
            Text(
                text = state.schedule,
                style = nunitoSans(12.369f, 16.492f, InterSemiBold, tracking = 0.2474f),
                color = TextStrong,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.123.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "Biaya Sesi", style = nunitoSans(14.431f, 20.615f, FontWeight.Normal), color = TextVariant)
            Text(
                text = state.price,
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.615.sp, lineHeight = 28.862.sp),
                color = Navy,
            )
        }
    }
}

@Preview(widthDp = 402, heightDp = 931)
@Composable
private fun PaymentScreenPreview() {
    ScholarHubTheme { PaymentScreen() }
}
