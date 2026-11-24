package com.example.scholarhub.feature.transactions

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.feature.documents.DocumentPurchase
import com.example.scholarhub.feature.documents.DocumentStore
import com.example.scholarhub.feature.mentoring.Booking
import com.example.scholarhub.feature.mentoring.MentoringStore
import com.example.scholarhub.feature.mentoring.PaymentStatus
import com.example.scholarhub.feature.mentoring.countdown
import com.example.scholarhub.feature.mentoring.paymentStatusAt
import com.example.scholarhub.feature.mentoring.rememberNow
import com.example.scholarhub.feature.mentoring.rupiah
import com.example.scholarhub.feature.transactions.components.BadgeStyle
import com.example.scholarhub.feature.transactions.components.TransactionCard
import com.example.scholarhub.feature.transactions.components.TransactionStatus
import com.example.scholarhub.feature.transactions.components.TransactionUi
import com.example.scholarhub.ui.components.CountChip
import com.example.scholarhub.ui.components.EndOfFeed
import com.example.scholarhub.ui.components.ScholarHubDetailTopBar
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.OnWarning
import com.example.scholarhub.ui.theme.PillShape
import com.example.scholarhub.ui.theme.ShadowSubtle
import com.example.scholarhub.ui.theme.Sky
import com.example.scholarhub.ui.theme.SkySubtle
import com.example.scholarhub.ui.theme.SurfaceCard
import com.example.scholarhub.ui.theme.TextMuted
import com.example.scholarhub.ui.theme.WarningContainer
import com.example.scholarhub.ui.theme.nunitoSans
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime

enum class TransactionFilter(val label: String) {
    All("Semua"),
    Pending("Perlu Diselesaikan"),
    Success("Berhasil"),
    Failed("Gagal"),
}

private fun DocumentPurchase.toTransaction(now: LocalDateTime): TransactionUi {
    val status = when (statusAt(now)) {
        PaymentStatus.Pending -> TransactionStatus.Pending
        PaymentStatus.Paid -> TransactionStatus.Success
        PaymentStatus.Failed -> TransactionStatus.Failed
    }
    return TransactionUi(
        id = id,
        bookingId = null,
        invoice = invoice,
        title = document.title,
        subtitle = "Premium · Akses selamanya",
        subtitleIcon = R.drawable.ic_docs_file,
        amount = rupiah(document.price ?: 0),
        status = status,
        timeLeft = if (status == TransactionStatus.Pending) "Sisa ${Duration.between(now, payBefore).countdown(withHours = false)}" else null,
    )
}

private fun Booking.toTransaction(now: LocalDateTime): TransactionUi {
    val status = when (paymentStatusAt(now)) {
        PaymentStatus.Pending -> TransactionStatus.Pending
        PaymentStatus.Paid -> TransactionStatus.Success
        PaymentStatus.Failed -> TransactionStatus.Failed
    }
    return TransactionUi(
        id = id,
        bookingId = id,
        invoice = invoice,
        title = topic,
        subtitle = "Mentor: ${mentor.name}",
        subtitleIcon = R.drawable.ic_trx_person,
        amount = rupiah(price),
        status = status,
        timeLeft = if (status == TransactionStatus.Pending) "Sisa ${Duration.between(now, payBefore).countdown(withHours = false)}" else null,
    )
}

/**
 * Riwayat Transaksi (Figma `215:6949`): filter status dengan jumlah, tagihan yang perlu diselesaikan di atas,
 * lalu transaksi lain dari yang terbaru.
 */
@Composable
fun TransactionHistoryScreen(
    store: MentoringStore,
    documents: DocumentStore,
    onBack: () -> Unit,
    /** ID pesanan sesi atau pembelian dokumen; [isDocument] membedakan keduanya. */
    onPay: (id: String, isDocument: Boolean) -> Unit,
    onInvoice: (id: String, isDocument: Boolean) -> Unit,
) {
    val bookings by store.bookings.collectAsState()
    val purchases by documents.purchases.collectAsState()
    val now = rememberNow(store)
    var filter by rememberSaveable { mutableStateOf(TransactionFilter.All) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val all = (bookings.map { it.createdAt to it.toTransaction(now) } + purchases.map { it.createdAt to it.toTransaction(now) })
        .sortedByDescending { it.first }.map { it.second }
        .let { list -> list.filter { it.status == TransactionStatus.Pending } + list.filter { it.status != TransactionStatus.Pending } }
    fun count(f: TransactionFilter) = all.count { f.matches(it) }
    val visible = all.filter { filter.matches(it) }
    val pending = all.count { it.status == TransactionStatus.Pending }

    Scaffold(
        containerColor = Base,
        topBar = { ScholarHubDetailTopBar(title = "Riwayat Transaksi", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
                    .padding(start = 16.492.dp, end = 16.492.dp, top = 12.369.dp, bottom = 8.246.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                TransactionFilter.entries.forEach { item ->
                    CountChip(item.label, count(item), selected = item == filter) { filter = item }
                }
            }
            Column(
                Modifier.padding(start = 16.492.dp, end = 16.492.dp, top = 12.369.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.308.dp),
            ) {
                if (pending > 0 && (filter == TransactionFilter.All || filter == TransactionFilter.Pending)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.185.dp), verticalAlignment = Alignment.CenterVertically) {
                            Image(painterResource(R.drawable.ic_trx_pending), null, Modifier.size(15.462.dp))
                            Text("Perlu Diselesaikan", style = nunitoSans(18.554f, 24.738f, FontWeight.Bold), color = Navy)
                        }
                        Text(
                            "$pending Tagihan Aktif",
                            Modifier.clip(PillShape).background(WarningContainer.copy(alpha = 0.9f)).padding(horizontal = 10.308.dp, vertical = 2.062.dp),
                            style = BadgeStyle,
                            color = OnWarning,
                        )
                    }
                }
                visible.forEach { item ->
                    TransactionCard(item, onAction = {
                        val isDocument = item.bookingId == null
                        if (item.status == TransactionStatus.Pending) onPay(item.id, isDocument) else onInvoice(item.id, isDocument)
                    })
                }
                if (visible.isEmpty()) {
                    Text(
                        "Belum ada transaksi ${filter.label.lowercase()}.",
                        Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = TextMuted,
                    )
                }
            }
            if (visible.isNotEmpty()) EndOfFeed(text = "Semua riwayat pembayaran telah dimuat")
        }
    }
}

private fun TransactionFilter.matches(item: TransactionUi) = when (this) {
    TransactionFilter.All -> true
    TransactionFilter.Pending -> item.status == TransactionStatus.Pending
    TransactionFilter.Success -> item.status == TransactionStatus.Success
    TransactionFilter.Failed -> item.status == TransactionStatus.Failed
}
