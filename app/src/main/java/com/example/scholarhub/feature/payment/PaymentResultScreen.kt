package com.example.scholarhub.feature.payment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.feature.mentoring.AdminContact
import com.example.scholarhub.feature.mentoring.Booking
import com.example.scholarhub.feature.mentoring.invoiceSchedule
import com.example.scholarhub.feature.mentoring.invoiceTime
import com.example.scholarhub.feature.mentoring.rupiah
import com.example.scholarhub.ui.components.OutlineButton
import com.example.scholarhub.ui.components.PrimaryButton
import com.example.scholarhub.ui.components.ScholarHubDetailTopBar
import com.example.scholarhub.ui.components.cardSurface
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.ErrorContainer
import com.example.scholarhub.ui.theme.ErrorRed
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.ScholarHubRadius
import kotlinx.coroutines.launch

private val SuccessSoft = Color(0xFFD7F5E5)
private val SuccessInk = Color(0xFF065F46)
private val CardShape = RoundedCornerShape(ScholarHubRadius.md)

/**
 * Hasil pembayaran: berhasil (Figma `218:4013`, dokumen `255:10705`) atau gagal (`266:11906`, `255:11750`),
 * beserta invoice. Juga dipakai "Lihat Invoice" di Riwayat Transaksi.
 */
@Composable
fun PaymentResultScreen(
    paid: Boolean,
    message: String,
    invoice: List<Pair<String, List<Pair<String, String>>>>,
    onBack: () -> Unit,
    primary: Pair<String, () -> Unit>,
    secondary: Pair<String, () -> Unit>,
) {
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    Scaffold(
        containerColor = Base,
        topBar = { ScholarHubDetailTopBar(title = if (paid) "Pembayaran Berhasil" else "Pembayaran Gagal", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Image(
                painterResource(if (paid) R.drawable.mascot_payment_success else R.drawable.mascot_payment_failed),
                null,
                Modifier.align(Alignment.CenterHorizontally).size(width = 174.dp, height = 237.dp),
                contentScale = ContentScale.Fit,
            )
            Column(
                Modifier.fillMaxWidth().clip(CardShape).background(if (paid) SuccessSoft else ErrorContainer).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val ink = if (paid) SuccessInk else ErrorRed
                Text(if (paid) "Pembayaran berhasil" else "Pembayaran gagal", style = PageTitleSemi, color = ink)
                Text(message, style = MaterialTheme.typography.bodyMedium, color = ink)
            }
            Column(Modifier.fillMaxWidth().cardSurface(shape = CardShape).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Invoice", style = PageTitleSemi, color = Navy)
                invoice.forEach { (title, rows) -> InvoiceSection(title, rows) }
            }
            Column(Modifier.fillMaxWidth().cardSurface(shape = CardShape).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (paid) {
                    PrimaryButton(text = primary.first, onClick = primary.second, modifier = Modifier.fillMaxWidth().height(48.dp), solidShadow = null)
                    OutlineButton(secondary.first, onClick = secondary.second)
                } else {
                    PrimaryButton(
                        text = "Hubungi Admin",
                        onClick = {
                            try {
                                uriHandler.openUri(AdminContact)
                            } catch (_: Exception) {
                                scope.launch { snackbar.showSnackbar("Aplikasi email tidak ditemukan.") }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        solidShadow = null,
                    )
                    Text(
                        "Jika saldo sudah terpotong, periksa status di Riwayat Transaksi sebelum mencoba membayar lagi.",
                        style = Caption,
                        color = Navy,
                    )
                }
            }
        }
    }
}

/** Isi hasil pembayaran untuk pesanan sesi mentoring. */
@Composable
fun SessionPaymentResultScreen(booking: Booking, paid: Boolean, onBack: () -> Unit, onHome: () -> Unit, onSeeSchedule: () -> Unit) {
    PaymentResultScreen(
        paid = paid,
        message = if (paid) {
            "Pembayaran berhasil! Sesi mentoring kamu telah terjadwal. Lihat detail sesi dan informasi jadwal melalui menu Semua Jadwal."
        } else {
            "Pembayaran belum berhasil diproses. Sesi mentoring belum terjadwal. Silakan periksa status transaksi atau laporkan kendala."
        },
        invoice = listOf(
            "Detail Sesi Mentoring" to listOf("Mentor" to booking.mentor.name, "Topik" to booking.topic, "Metode Pembayaran" to "QRIS", "Waktu" to booking.invoiceSchedule()),
            "Detail Pembayaran" to listOfNotNull(
                "Nomor Invoice" to booking.invoice,
                "Nominal" to rupiah(booking.price),
                "Waktu" to (booking.paidAt ?: booking.createdAt).invoiceTime(),
                if (paid) null else "Status" to "Gagal",
            ),
        ),
        onBack = onBack,
        primary = "Kembali ke Home" to onHome,
        secondary = "Lihat Semua Jadwal" to onSeeSchedule,
    )
}

/** Bagian invoice: judul Bold 16, grid label 140 dp + nilai SemiBold 13. */
@Composable
private fun InvoiceSection(title: String, rows: List<Pair<String, String>>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall, color = Navy)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            rows.forEach { (label, value) ->
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(label, Modifier.width(140.dp), style = MaterialTheme.typography.bodyMedium, color = Navy)
                    Text(value, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = Navy)
                }
            }
        }
    }
}
