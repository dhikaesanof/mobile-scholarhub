package com.example.scholarhub.feature.payment

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.feature.mentoring.BookingDraft
import com.example.scholarhub.feature.mentoring.longSchedule
import com.example.scholarhub.feature.mentoring.rupiah
import com.example.scholarhub.feature.payment.components.PaymentCardShape
import com.example.scholarhub.feature.payment.components.PaymentMethodCard
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
import com.example.scholarhub.ui.theme.ShadowSubtle
import com.example.scholarhub.ui.theme.SkySoft
import com.example.scholarhub.ui.theme.TextStrong
import com.example.scholarhub.ui.theme.TextVariant
import com.example.scholarhub.ui.theme.nunitoSans

private val ContentPadding = 16.492.dp
private val SectionLabelGap = 8.246.dp

/**
 * Detail Pembayaran (Figma `215:6371`): ringkasan sesi dari [draft], metode QRIS & E-Wallet, lalu "Bayar Sekarang"
 * yang membuat tagihan dan membuka layar QRIS.
 */
@Composable
fun PaymentScreen(
    draft: BookingDraft,
    onBack: () -> Unit,
    onPay: () -> Unit,
) {
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
                OrderSummaryCard(draft)
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
                        Text(text = "Verifikasi otomatis", style = MaterialTheme.typography.labelSmall, color = TextVariant)
                    }
                }
                PaymentMethodCard(method = SamplePaymentMethods.first(), selected = true, onClick = {})
            }

            PayButton(text = "Bayar Sekarang", onClick = onPay)
        }
    }
}

@Composable
private fun OrderSummaryCard(draft: BookingDraft) {
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
                Box(Modifier.size(width = 53.dp, height = 50.dp).clip(RoundedCornerShape(30.dp)).background(Ocean.copy(alpha = 0.4f)))
                CroppedImage(
                    image = draft.mentor.photo,
                    width = 49.dp,
                    height = 46.dp,
                    crop = FigmaCrop(scaleY = 1.7902f, offsetY = -0.3951f),
                    contentDescription = "Foto ${draft.mentor.name}",
                    modifier = Modifier.clip(RoundedCornerShape(28.dp)),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = draft.topic,
                    modifier = Modifier.padding(bottom = 4.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = Navy,
                )
                Text(text = draft.mentor.name, modifier = Modifier.padding(top = 2.062.dp), style = MaterialTheme.typography.labelLarge, color = Ocean)
                Text(text = draft.mentor.affiliation, style = MaterialTheme.typography.labelLarge, color = TextVariant)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.246.dp)).background(SkySoft).padding(8.246.dp),
            horizontalArrangement = Arrangement.spacedBy(8.246.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(painterResource(R.drawable.ic_pay_calendar), null, Modifier.size(width = 15.462.dp, height = 16.235.dp))
            Text(text = draft.longSchedule(), style = MaterialTheme.typography.labelLarge, color = TextStrong)
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.123.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "Biaya Sesi", style = MaterialTheme.typography.labelLarge, color = TextVariant)
            Text(text = rupiah(draft.price), style = MaterialTheme.typography.titleMedium, color = Navy)
        }
    }
}
