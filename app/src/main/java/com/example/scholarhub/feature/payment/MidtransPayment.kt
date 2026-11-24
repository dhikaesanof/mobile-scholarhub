package com.example.scholarhub.feature.payment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.time.LocalDateTime

/** Tagihan QRIS dari Midtrans Sandbox lewat Worker `server/worker.js`. */
data class QrisCharge(val orderId: String, val qrUrl: String, val qr: ImageBitmap)

enum class RemotePaymentStatus { Pending, Paid, Failed }

/**
 * Klien Worker ScholarHub Pay. Server Key Midtrans hanya ada di Worker, jadi aplikasi cukup tahu alamatnya.
 * Tagihan disimpan per invoice agar membuka ulang layar QRIS tidak membuat tagihan baru (order_id Midtrans harus unik).
 */
class PaymentGateway(private val baseUrl: String = BaseUrl) {
    private val charges = mutableMapOf<String, QrisCharge>()

    suspend fun charge(invoice: String, amount: Int, itemName: String): Result<QrisCharge> = runCatching {
        charges[invoice]?.let { return@runCatching it }
        // Nomor invoice di memori bisa terulang setelah aplikasi dibuka ulang; akhiran waktu menjaga order_id tetap unik.
        val orderId = invoice.replace('/', '-') + "-" + (System.currentTimeMillis() / 1000 % 1_000_000)
        val body = JSONObject().put("orderId", orderId).put("amount", amount).put("itemName", itemName)
        val response = request("POST", "$baseUrl/qris", body.toString())
        val qrUrl = response.optString("qrUrl").ifBlank { throw IOException(response.optString("error", "QR tidak tersedia.")) }
        val bytes = withContext(Dispatchers.IO) { URL(qrUrl).openStream().use { it.readBytes() } }
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap() ?: throw IOException("Gambar QR rusak.")
        QrisCharge(orderId, qrUrl, bitmap).also { charges[invoice] = it }
    }

    suspend fun status(invoice: String): Result<RemotePaymentStatus> = runCatching {
        val charge = charges[invoice] ?: return@runCatching RemotePaymentStatus.Failed
        val response = request("GET", "$baseUrl/status?orderId=" + URLEncoder.encode(charge.orderId, "UTF-8"), null)
        when (response.optString("status")) {
            "paid" -> RemotePaymentStatus.Paid
            "pending" -> RemotePaymentStatus.Pending
            "failed" -> RemotePaymentStatus.Failed
            else -> throw IOException(response.optString("error", "Status tidak dikenali."))
        }
    }

    private suspend fun request(method: String, url: String, body: String?): JSONObject = withContext(Dispatchers.IO) {
        val conn = URL(url).openConnection() as HttpURLConnection
        try {
            conn.requestMethod = method
            conn.connectTimeout = 15_000
            conn.readTimeout = 20_000
            if (body != null) {
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/json")
                conn.outputStream.use { it.write(body.toByteArray()) }
            }
            val stream = if (conn.responseCode in 200..299) conn.inputStream else conn.errorStream
            val text = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
            val json = runCatching { JSONObject(text) }.getOrElse { JSONObject() }
            if (conn.responseCode !in 200..299) throw IOException(json.optString("error", "Server pembayaran tidak merespons."))
            json
        } finally {
            conn.disconnect()
        }
    }

    companion object {
        /** Worker Cloudflare `scholarhub-pay` (lihat `server/README.md`). */
        const val BaseUrl = "https://scholarhub-pay.lorongsantai.workers.dev"
    }
}

/**
 * Layar QRIS yang tersambung ke Midtrans: membuat tagihan saat dibuka, lalu "Periksa Status" menanyakan status ke Midtrans.
 * Tagihan yang masih menunggu tetap di layar ini; yang lunas atau gagal membuka halaman hasil.
 */
@Composable
fun MidtransQrisRoute(
    order: PaymentOrder,
    gateway: PaymentGateway,
    now: LocalDateTime,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onPaid: () -> Unit,
    onFailed: () -> Unit,
) {
    val context = LocalContext.current
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var charge by remember { mutableStateOf<QrisCharge?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var attempt by remember { mutableIntStateOf(0) }
    var checking by remember { mutableStateOf(false) }

    LaunchedEffect(order.invoice, attempt) {
        error = null
        gateway.charge(order.invoice, order.price, order.title)
            .onSuccess { charge = it }
            .onFailure { error = "Qris gagal dibuat, cek jaringan anda" }
    }

    QrisPaymentScreen(
        order = order,
        now = now,
        onBack = onBack,
        onHome = onHome,
        qr = charge?.qr,
        qrError = error,
        onRetryQr = { attempt++ },
        checking = checking,
        snackbar = snackbar,
        onCopyQrLink = charge?.let { c -> { copyToClipboard(context, c.qrUrl); scope.launch { snackbar.showSnackbar("Link QR disalin. Tempel di QRIS Simulator Midtrans.") } } },
        onCheckStatus = {
            checking = true
            scope.launch {
                gateway.status(order.invoice).fold(
                    onSuccess = { status ->
                        when {
                            status == RemotePaymentStatus.Paid -> onPaid()
                            status == RemotePaymentStatus.Failed || !now.isBefore(order.payBefore) -> onFailed()
                            else -> snackbar.showSnackbar("Pembayaran belum diterima. Selesaikan pembayaran, lalu periksa lagi.")
                        }
                    },
                    onFailure = { snackbar.showSnackbar("Status belum bisa diperiksa. Periksa koneksi internet.") },
                )
                checking = false
            }
        },
    )
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("QRIS", text))
}
