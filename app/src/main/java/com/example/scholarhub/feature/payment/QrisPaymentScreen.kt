package com.example.scholarhub.feature.payment

import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.feature.mentoring.Booking
import com.example.scholarhub.feature.mentoring.countdown
import com.example.scholarhub.feature.mentoring.rupiah
import com.example.scholarhub.feature.transactions.components.AwaitingPaymentBadge
import com.example.scholarhub.feature.transactions.components.TimeLeftBadge
import com.example.scholarhub.ui.components.OutlineButton
import com.example.scholarhub.ui.components.PrimaryButton
import com.example.scholarhub.ui.components.ScholarHubDetailTopBar
import com.example.scholarhub.ui.components.WarningNotice
import com.example.scholarhub.ui.components.cardSurface
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.ScholarHubRadius
import com.example.scholarhub.ui.theme.Steel
import com.example.scholarhub.ui.theme.nunitoSans
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Judul halaman SemiBold 23 / 1.67 (Figma "Selesaikan pembayaran", "Pembayaran berhasil", "Invoice"). */
internal val PageTitleSemi = nunitoSans(23f, 38.4f, FontWeight.SemiBold)
internal val Caption = nunitoSans(11f, 16.5f, FontWeight.Normal)
private val CardShape = RoundedCornerShape(ScholarHubRadius.md)
private val OrderDate = DateTimeFormatter.ofPattern("EEE, d MMMM yyyy", Locale.forLanguageTag("id-ID"))
private val Time = DateTimeFormatter.ofPattern("HH:mm")

/** Pesanan yang dibayar lewat QRIS: sesi mentoring atau dokumen premium. */
data class PaymentOrder(
    val title: String,
    /** Baris di bawah judul, mis. mentor + jadwal, atau "Dokumen Premium\nPembayaran satu kali · Akses selamanya". */
    val details: String,
    val invoice: String,
    val price: Int,
    val payBefore: LocalDateTime,
    val intro: String,
    /** Peringatan merah (judul, isi), mis. "Periksa Kembali Jadwal Sesi" atau "Periksa Kembali Dokumen Pilihanmu". */
    val warning: Pair<String, String>? = null,
)

/**
 * Bayar dengan QRIS (Figma `218:3628` sesi, `255:10365` dokumen). Tagihan berlaku 15 menit;
 * [onCheckStatus] membuka hasil berhasil, atau gagal jika waktunya habis.
 */
@Composable
fun QrisPaymentScreen(
    order: PaymentOrder,
    now: LocalDateTime,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onCheckStatus: () -> Unit,
) {
    val remaining = Duration.between(now, order.payBefore)
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        containerColor = Base,
        topBar = { ScholarHubDetailTopBar(title = "Bayar dengan QRIS", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Selesaikan pembayaran", style = PageTitleSemi, color = Navy)
                Text(order.intro, style = MaterialTheme.typography.bodyMedium, color = Navy)
            }
            order.warning?.let { (title, body) -> WarningNotice(title, body) }
            Column(Modifier.fillMaxWidth().cardSurface(shape = CardShape).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(order.title, style = MaterialTheme.typography.titleSmall, color = Navy)
                Text(order.details, style = MaterialTheme.typography.bodyMedium, color = Navy)
                Text(order.invoice, style = Caption, color = Steel)
            }
            Column(Modifier.fillMaxWidth().cardSurface(shape = CardShape).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(Modifier.fillMaxWidth().padding(bottom = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    AwaitingPaymentBadge()
                    TimeLeftBadge("Sisa ${remaining.countdown(withHours = false)}")
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Image(painterResource(R.drawable.payment_qris), "Kode QRIS ${rupiah(order.price)}", Modifier.size(239.dp).clip(CardShape))
                    Text(
                        rupiah(order.price),
                        Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center),
                        color = Navy,
                    )
                }
                Text(
                    "1. Buka aplikasi bank atau e-wallet.\n2. Pindai QR dan periksa nominal.\n3. Bayar, lalu tunggu verifikasi.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Navy,
                )
                OutlineButton("Unduh QRIS", onClick = {
                    val saved = saveQrisToGallery(context, order.invoice)
                    scope.launch { snackbar.showSnackbar(if (saved) "QRIS tersimpan di Galeri." else "QRIS gagal disimpan.") }
                })
            }
            Column(Modifier.fillMaxWidth().cardSurface(shape = CardShape).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Sudah membayar? Periksa status untuk melihat hasil verifikasi.", style = Caption, color = Steel)
                PrimaryButton(
                    text = "Periksa Status Pembayaran",
                    onClick = onCheckStatus,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    solidShadow = null,
                )
                OutlineButton("Kembali ke Home", onClick = onHome)
            }
        }
    }
}

/** Data QRIS untuk pesanan sesi mentoring. */
fun Booking.toPaymentOrder() = PaymentOrder(
    title = topic,
    details = "${mentor.name}\n${start.format(OrderDate)} · ${start.format(Time)}–${end.format(Time)} WIB",
    invoice = invoice,
    price = price,
    payBefore = payBefore,
    intro = "Pesanan belum menjadi sesi aktif sampai pembayaran terverifikasi.",
    warning = "Periksa Kembali Jadwal Sesi" to
        "Jadwal tidak dapat diubah setelah pembayaran berhasil. Pastikan kamu tersedia pada waktu yang dipilih.",
)

/** Menyimpan gambar QRIS ke Pictures/ScholarHub lewat MediaStore (tanpa izin penyimpanan di API 29+). */
private fun saveQrisToGallery(context: Context, invoice: String): Boolean = try {
    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.payment_qris)
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "QRIS-${invoice.replace('/', '-')}.png")
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/ScholarHub")
    }
    val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    uri != null && context.contentResolver.openOutputStream(uri)?.use { bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) } == true
} catch (_: Exception) {
    false
}
