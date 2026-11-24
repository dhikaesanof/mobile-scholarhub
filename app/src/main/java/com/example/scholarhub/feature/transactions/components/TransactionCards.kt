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
import com.example.scholarhub.ui.components.PrimaryButton
import com.example.scholarhub.ui.theme.ErrorContainer
import com.example.scholarhub.ui.theme.ErrorRed
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
private val SuccessSoft = Color(0xFFD7F5E5)
private val SuccessInk = Color(0xFF065F46)
internal val BadgeStyle = nunitoSans(10.308f, 14.431f, InterSemiBold, tracking = 0.4123f)

enum class TransactionStatus { Pending, Success, Failed }

@Immutable
data class TransactionUi(
    val id: String,
    /** ID pesanan sesi; null untuk pembelian dokumen. */
    val bookingId: String?,
    val invoice: String,
    val title: String,
    /** Baris di bawah judul, mis. "Mentor: Samantha Dira" atau "Premium · Akses selamanya". */
    val subtitle: String,
    @DrawableRes val subtitleIcon: Int,
    val amount: String,
    val status: TransactionStatus,
    /** Sisa waktu bayar untuk tagihan menunggu, misalnya "Sisa 14:31". */
    val timeLeft: String? = null,
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

/** Badge "Menunggu Pembayaran" (Riwayat Transaksi dan Bayar dengan QRIS). */
@Composable
fun AwaitingPaymentBadge(modifier: Modifier = Modifier) {
    StatusPill(
        text = "Menunggu Pembayaran",
        background = WarningContainerSoft,
        content = OnWarningStrong,
        modifier = modifier,
        verticalPadding = 4.123.dp,
        gap = 6.185.dp,
        leading = { Box(Modifier.size(8.246.dp).clip(CircleShape).background(Warning)) },
    )
}

/** Badge sisa waktu bayar, misalnya "Sisa 14:31". */
@Composable
fun TimeLeftBadge(text: String, modifier: Modifier = Modifier) {
    StatusPill(
        text = text,
        background = ErrorContainerSoft,
        content = OnErrorContainer,
        modifier = modifier,
        leading = {
            Image(
                painter = painterResource(R.drawable.ic_trx_timer),
                contentDescription = null,
                modifier = Modifier.size(width = 12.807.dp, height = 11.815.dp),
            )
        },
    )
}

@Composable
private fun IconText(@DrawableRes icon: Int, iconWidth: Dp, iconHeight: Dp, text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.185.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(painter = painterResource(icon), contentDescription = null, modifier = Modifier.size(iconWidth, iconHeight))
        Text(text = text, style = MentorStyle, color = TextVariant)
    }
}

/**
 * Kartu transaksi (Figma `Article - Pending Card` `215:6984`/`266:11853`/`266:11878`): badge status,
 * invoice, judul, baris mentor/dokumen, total tagihan, lalu "Lanjut Bayar" (menunggu) atau "Lihat Invoice".
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TransactionCard(item: TransactionUi, onAction: () -> Unit, modifier: Modifier = Modifier) {
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
            when (item.status) {
                TransactionStatus.Pending -> AwaitingPaymentBadge()
                TransactionStatus.Success -> DotBadge("Pembayaran Berhasil", SuccessSoft, SuccessInk)
                TransactionStatus.Failed -> DotBadge("Pembayaran Gagal", ErrorContainer, ErrorRed)
            }
            item.timeLeft?.let { TimeLeftBadge(it, Modifier.align(Alignment.CenterVertically)) }
        }
        Column(modifier = Modifier.padding(bottom = 14.431.dp), verticalArrangement = Arrangement.spacedBy(5.566.dp)) {
            Text(text = item.invoice, style = InvoiceStyle, color = TextMuted)
            Text(text = item.title, style = TitleStyle, color = Navy)
            IconText(item.subtitleIcon, 12.dp, 12.dp, item.subtitle)
        }
        HorizontalDivider(color = OutlineMist, thickness = 1.031.dp)
        Column(modifier = Modifier.padding(top = 14.431.dp)) {
            Text(text = "Total Tagihan", style = SmallStyle, color = TextMuted)
            Text(text = item.amount, style = nunitoSans(20.615f, 28.862f, FontWeight.Bold, tracking = -0.5154f), color = Navy)
        }
        if (item.status == TransactionStatus.Pending) {
            PayButton(text = "Lanjut Bayar", onClick = onAction, modifier = Modifier.padding(top = 20.615.dp))
        } else {
            PrimaryButton(
                text = "Lihat Invoice",
                onClick = onAction,
                modifier = Modifier.padding(top = 20.615.dp).fillMaxWidth().height(41.dp),
                solidShadow = Color(0xFF08193D),
                shadowOffset = 3.092.dp,
            )
        }
    }
}

@Composable
private fun DotBadge(text: String, background: Color, content: Color) {
    StatusPill(
        text = text,
        background = background,
        content = content,
        verticalPadding = 4.123.dp,
        gap = 6.185.dp,
        leading = { Box(Modifier.size(8.246.dp).clip(CircleShape).background(content)) },
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF1F1F1, widthDp = 402)
@Composable
private fun TransactionCardsPreview() {
    ScholarHubTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            TransactionStatus.entries.forEach { status ->
                TransactionCard(
                    TransactionUi("1", null, "INV/SH/20261121/089", "Review Esai Beasiswa", "Mentor: Samantha Dira", R.drawable.ic_trx_person, "Rp 50.000", status, if (status == TransactionStatus.Pending) "Sisa 14:31" else null),
                    onAction = {},
                )
            }
        }
    }
}
