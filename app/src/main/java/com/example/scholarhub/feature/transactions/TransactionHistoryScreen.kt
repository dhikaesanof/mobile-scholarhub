package com.example.scholarhub.feature.transactions

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.feature.transactions.components.BadgeStyle
import com.example.scholarhub.feature.transactions.components.CompletedTransactionCard
import com.example.scholarhub.feature.transactions.components.PendingTransactionCard
import com.example.scholarhub.feature.transactions.components.SampleTransactions
import com.example.scholarhub.feature.transactions.components.TransactionStatus
import com.example.scholarhub.feature.transactions.components.TransactionUi
import com.example.scholarhub.ui.components.CountFilterChip
import com.example.scholarhub.ui.components.EndOfFeed
import com.example.scholarhub.ui.components.ScholarHubDetailTopBar
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.OnWarning
import com.example.scholarhub.ui.theme.PillShape
import com.example.scholarhub.ui.theme.ScholarHubTheme
import com.example.scholarhub.ui.theme.TextMuted
import com.example.scholarhub.ui.theme.Warning
import com.example.scholarhub.ui.theme.WarningContainer
import com.example.scholarhub.ui.theme.nunitoSans

enum class TransactionFilter(val label: String) {
    All("Semua"),
    Pending("Menunggu"),
    Success("Berhasil"),
    Cancelled("Dibatalkan"),
}

/** Isi Riwayat Transaksi. Masih contoh statis sampai data transaksi tersedia. */
data class TransactionHistoryUiState(
    val transactions: List<TransactionUi>,
    /** Angka di chip filter. Di Figma angkanya ringkasan semua periode, bukan hanya kartu yang tampil. */
    val counts: Map<TransactionFilter, Int>,
    val monthLabel: String,
    /** Jumlah transaksi selesai bulan ini (ringkasan data, bisa lebih banyak dari kartu yang dimuat). */
    val monthCompletedCount: Int,
)

val SampleTransactionHistoryUiState = TransactionHistoryUiState(
    transactions = SampleTransactions,
    counts = mapOf(TransactionFilter.All to 5, TransactionFilter.Pending to 1, TransactionFilter.Success to 4),
    monthLabel = "Oktober 2026",
    monthCompletedCount = 2,
)

private val ScreenPadding = 16.492.dp
private val SectionTitleStyle = nunitoSans(18.554f, 24.738f, FontWeight.Bold)

/** Riwayat Transaksi (Figma 846:4272): filter status, tagihan aktif, dan transaksi selesai per bulan. */
@Composable
fun TransactionHistoryScreen(
    state: TransactionHistoryUiState = SampleTransactionHistoryUiState,
    onBack: () -> Unit = {},
    onPay: (TransactionUi) -> Unit = {},
    onSessionDetail: (TransactionUi) -> Unit = {},
) {
    var filter by rememberSaveable { mutableStateOf(TransactionFilter.All) }
    val visible = state.transactions.filter {
        when (filter) {
            TransactionFilter.All -> true
            TransactionFilter.Pending -> it.status == TransactionStatus.Pending
            TransactionFilter.Success -> it.status == TransactionStatus.Success
            TransactionFilter.Cancelled -> it.status == TransactionStatus.Cancelled
        }
    }
    val pending = visible.filter { it.status == TransactionStatus.Pending }
    val done = visible.filter { it.status != TransactionStatus.Pending }

    Scaffold(
        containerColor = Base,
        topBar = { ScholarHubDetailTopBar(title = "Riwayat Transaksi", onBack = onBack) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(start = ScreenPadding, end = ScreenPadding, top = 12.369.dp, bottom = 8.246.dp)
                    .height(32.985.dp),
                horizontalArrangement = Arrangement.spacedBy(8.246.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TransactionFilter.entries.forEach { item ->
                    CountFilterChip(
                        label = item.label,
                        selected = item == filter,
                        onClick = { filter = item },
                        count = state.counts[item],
                        dotColor = if (item == TransactionFilter.Pending) Warning else null,
                    )
                }
            }

            Column(
                modifier = Modifier.padding(start = ScreenPadding, end = ScreenPadding, top = 12.369.dp, bottom = 32.985.dp),
                verticalArrangement = Arrangement.spacedBy(24.738.dp),
            ) {
                if (pending.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.308.dp)) {
                        SectionTitle(icon = R.drawable.ic_trx_pending, iconWidth = 15.462.dp, iconHeight = 15.462.dp, title = "Perlu Diselesaikan") {
                            Text(
                                text = "${pending.size} Tagihan Aktif",
                                modifier = Modifier
                                    .clip(PillShape)
                                    .background(WarningContainer.copy(alpha = 0.9f))
                                    .padding(horizontal = 10.308.dp, vertical = 2.062.dp),
                                style = BadgeStyle,
                                color = OnWarning,
                            )
                        }
                        pending.forEach { PendingTransactionCard(item = it, onPayClick = { onPay(it) }) }
                    }
                }
                if (done.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.369.dp)) {
                        SectionTitle(icon = R.drawable.ic_trx_month, iconWidth = 13.915.dp, iconHeight = 15.462.dp, title = state.monthLabel) {
                            Text(text = "${state.monthCompletedCount} Transaksi Selesai", style = BadgeStyle, color = TextMuted)
                        }
                        done.forEach { CompletedTransactionCard(item = it, onDetailClick = { onSessionDetail(it) }) }
                    }
                }
                if (visible.isEmpty()) {
                    Text(
                        text = "Belum ada transaksi ${filter.label.lowercase()}.",
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        style = nunitoSans(13f, 19.5f, FontWeight.Medium),
                        color = TextMuted,
                    )
                }
            }

            EndOfFeed(text = "Semua riwayat pembayaran telah dimuat")
        }
    }
}

@Composable
private fun SectionTitle(
    @DrawableRes icon: Int,
    iconWidth: Dp,
    iconHeight: Dp,
    title: String,
    trailing: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.185.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(icon), contentDescription = null, modifier = Modifier.size(iconWidth, iconHeight))
            Text(text = title, style = SectionTitleStyle, color = Navy)
        }
        trailing()
    }
}

@Preview(widthDp = 402, heightDp = 931)
@Composable
private fun TransactionHistoryScreenPreview() {
    ScholarHubTheme { TransactionHistoryScreen() }
}
