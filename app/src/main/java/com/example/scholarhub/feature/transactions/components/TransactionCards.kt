package com.example.scholarhub.feature.transactions.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.feature.payment.components.PayButton
import com.example.scholarhub.ui.theme.ErrorContainerSoft
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.OnErrorContainer
import com.example.scholarhub.ui.theme.OnWarningStrong
import com.example.scholarhub.ui.theme.OutlineMist
import com.example.scholarhub.ui.theme.OutlineSky
import com.example.scholarhub.ui.theme.PillShape
import com.example.scholarhub.ui.theme.ScholarHubTheme
import com.example.scholarhub.ui.theme.SkySoft
import com.example.scholarhub.ui.theme.Success
import com.example.scholarhub.ui.theme.SuccessContainer
import com.example.scholarhub.ui.theme.SurfaceCard
import com.example.scholarhub.ui.theme.TextMuted
import com.example.scholarhub.ui.theme.TextStrong
import com.example.scholarhub.ui.theme.TextVariant
import com.example.scholarhub.ui.theme.Warning
import com.example.scholarhub.ui.theme.WarningContainerSoft
import com.example.scholarhub.ui.theme.InterSemiBold
import com.example.scholarhub.ui.theme.nunitoSans

// Frame Riwayat Transaksi di-scale ×1.031; nilai dipakai apa adanya (lihat docs/mentee-ui.md).
private val TrxCardShape = RoundedCornerShape(12.369.dp)
private val TrxCardShadow = Color(0x0F0F2A66) // #0F2A66 @ 6%
private val TrxCardShadowSoft = Color(0x0A0F2A66) // #0F2A66 @ 4%

private val InvoiceStyle = nunitoSans(10.308f, 14.431f, FontWeight.Bold, tracking = 0.2577f)
private val TitleStyle = nunitoSans(18.554f, 25.512f, FontWeight.Bold)
private val MentorStyle = nunitoSans(14.431f, 20.615f, FontWeight.Normal)
private val SmallStyle = nunitoSans(12.369f, 16.492f, FontWeight.Normal)
internal val BadgeStyle = nunitoSans(10.308f, 14.431f, InterSemiBold, tracking = 0.4123f)

enum class TransactionStatus { Pending, Success, Cancelled }

@Immutable
data class TransactionUi(
    val id: String,
    val invoice: String,
    val serviceTitle: String,
    val mentorName: String,
    val amount: String,
    val status: TransactionStatus,
    /** Sisa waktu bayar untuk tagihan menunggu, misalnya "Sisa 14:31". */
    val timeLeft: String? = null,
    /** Waktu transaksi selesai, misalnya "18 Okt 2026, 14:15 WIB". */
    val paidAt: String? = null,
)

@Composable
private fun StatusPill(
    text: String,
    background: Color,
    content: Color,
    modifier: Modifier = Modifier,
    verticalPadding: Dp = 2.062.dp,
    leading: (@Composable () -> Unit)? = null,
    gap: Dp = 4.113.dp,
) {
    Row(
        modifier = modifier
            .clip(PillShape)
            .background(background)
            .padding(horizontal = 10.308.dp, vertical = verticalPadding),
        horizontalArrangement = Arrangement.spacedBy(gap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leading?.invoke()
        Text(text = text, style = BadgeStyle, color = content)
    }
}

@Composable
private fun IconText(@DrawableRes icon: Int, iconWidth: Dp, iconHeight: Dp, text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.185.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(painter = painterResource(icon), contentDescription = null, modifier = Modifier.size(iconWidth, iconHeight))
        Text(text = text, style = MentorStyle, color = TextVariant)
    }
}

/** Kartu tagihan menunggu pembayaran (Figma `Article - Pending Card`). */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PendingTransactionCard(item: TransactionUi, onPayClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, TrxCardShape, ambientColor = TrxCardShadow, spotColor = TrxCardShadow)
            .clip(TrxCardShape)
            .background(SurfaceCard)
            .padding(16.492.dp),
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.369.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.spacedBy(8.246.dp),
        ) {
            StatusPill(
                text = "Menunggu Pembayaran",
                background = WarningContainerSoft,
                content = OnWarningStrong,
                verticalPadding = 4.123.dp,
                gap = 6.185.dp,
                leading = { Box(Modifier.size(8.246.dp).clip(CircleShape).background(Warning)) },
            )
            item.timeLeft?.let {
                StatusPill(
                    text = it,
                    background = ErrorContainerSoft,
                    content = OnErrorContainer,
                    modifier = Modifier.align(Alignment.CenterVertically),
                    leading = {
                        Image(
                            painter = painterResource(R.drawable.ic_trx_timer),
                            contentDescription = null,
                            modifier = Modifier.size(width = 12.807.dp, height = 11.815.dp),
                        )
                    },
                )
            }
        }
        Column(modifier = Modifier.padding(bottom = 14.431.dp), verticalArrangement = Arrangement.spacedBy(5.566.dp)) {
            Text(text = item.invoice, style = InvoiceStyle, color = TextMuted)
            Text(text = item.serviceTitle, style = TitleStyle, color = Navy)
            IconText(R.drawable.ic_trx_person, 10.995.dp, 10.995.dp, "Mentor: ${item.mentorName}")
        }
        HorizontalDivider(color = OutlineMist, thickness = 1.031.dp)
        Column(modifier = Modifier.padding(top = 14.431.dp)) {
            Text(text = "Total Tagihan", style = SmallStyle, color = TextMuted)
            Text(
                text = item.amount,
                style = nunitoSans(20.615f, 28.862f, FontWeight.Bold, tracking = -0.5154f),
                color = Navy,
            )
        }
        PayButton(text = "Lanjut Bayar", onClick = onPayClick, modifier = Modifier.padding(top = 20.615.dp))
    }
}

/** Kartu transaksi selesai (Figma `Article - Transaction Card`). */
@Composable
fun CompletedTransactionCard(item: TransactionUi, onDetailClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(3.dp, TrxCardShape, ambientColor = TrxCardShadowSoft, spotColor = TrxCardShadowSoft)
            .clip(TrxCardShape)
            .background(SurfaceCard)
            .padding(16.492.dp),
        verticalArrangement = Arrangement.spacedBy(11.751.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = item.invoice, style = InvoiceStyle.copy(letterSpacing = BadgeStyle.letterSpacing), color = TextMuted)
            StatusPill(
                text = "Berhasil",
                background = SuccessContainer,
                content = Success,
                leading = {
                    Image(
                        painter = painterResource(R.drawable.ic_trx_success),
                        contentDescription = null,
                        modifier = Modifier.size(12.026.dp),
                    )
                },
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(3.608.dp)) {
            Text(text = item.serviceTitle, style = TitleStyle, color = Navy)
            IconText(R.drawable.ic_trx_mentor, 15.118.dp, 12.369.dp, "Mentor: ${item.mentorName}")
            item.paidAt?.let {
                Text(text = it, modifier = Modifier.padding(top = 2.062.dp), style = SmallStyle, color = TextMuted)
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.246.dp))
                .background(SkySoft)
                .padding(start = 12.369.dp, end = 12.369.dp, top = 8.865.dp, bottom = 8.246.dp),
        ) {
            Text(text = "Total Bayar", style = SmallStyle, color = TextMuted)
            Text(
                text = item.amount,
                style = nunitoSans(14.431f, 20.615f, InterSemiBold, tracking = 0.1443f),
                color = Navy,
            )
        }
        Row(
            modifier = Modifier
                .padding(top = 4.742.dp)
                .fillMaxWidth()
                .height(37.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(OutlineSky)
                .clickable(onClick = onDetailClick),
            horizontalArrangement = Arrangement.spacedBy(4.123.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_trx_eye),
                contentDescription = null,
                modifier = Modifier.size(width = 15.118.dp, height = 10.308.dp),
            )
            Text(
                text = "Detail Sesi",
                style = nunitoSans(12.369f, 16.492f, InterSemiBold, tracking = 0.2474f),
                color = TextStrong,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF1F1F1, widthDp = 402)
@Composable
private fun TransactionCardsPreview() {
    ScholarHubTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            PendingTransactionCard(SampleTransactions[0], onPayClick = {})
            CompletedTransactionCard(SampleTransactions[1], onDetailClick = {})
        }
    }
}

val SampleTransactions = listOf(
    TransactionUi(
        id = "INV/SCH-202610/089",
        invoice = "INV/SCH-202610/089",
        serviceTitle = "Simulasi Wawancara",
        mentorName = "Samantha Dira, S.Pd",
        amount = "Rp 150.000",
        status = TransactionStatus.Pending,
        timeLeft = "Sisa 14:31",
    ),
    TransactionUi(
        id = "INV/SCH-202610/062",
        invoice = "INV/SCH-202610/062",
        serviceTitle = "Review Esai",
        mentorName = "Kevin Sanjaya",
        amount = "Rp 85.000",
        status = TransactionStatus.Success,
        paidAt = "18 Okt 2026, 14:15 WIB",
    ),
)
