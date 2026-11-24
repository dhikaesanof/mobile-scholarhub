package com.example.scholarhub.feature.documents

import android.content.ContentValues
import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.feature.mentoring.rupiah
import com.example.scholarhub.feature.payment.components.PayButton
import com.example.scholarhub.feature.payment.components.PaymentCardShape
import com.example.scholarhub.feature.payment.components.PaymentMethodCard
import com.example.scholarhub.feature.payment.components.PaymentSectionLabel
import com.example.scholarhub.feature.payment.components.SamplePaymentMethods
import com.example.scholarhub.ui.components.CountChip
import com.example.scholarhub.ui.components.EndOfFeed
import com.example.scholarhub.ui.components.MenteeBottomBar
import com.example.scholarhub.ui.components.MenteeNavTab
import com.example.scholarhub.ui.components.PrimaryButton
import com.example.scholarhub.ui.components.ScholarHubDetailTopBar
import com.example.scholarhub.ui.components.ScholarHubTopBar
import com.example.scholarhub.ui.components.SearchField
import com.example.scholarhub.ui.components.cardSurface
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.InterSemiBold
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.Ocean
import com.example.scholarhub.ui.theme.OceanDark
import com.example.scholarhub.ui.theme.PillShape
import com.example.scholarhub.ui.theme.Sky
import com.example.scholarhub.ui.theme.SkyBorder
import com.example.scholarhub.ui.theme.SkySoft
import com.example.scholarhub.ui.theme.SurfaceCard
import com.example.scholarhub.ui.theme.TextMuted
import com.example.scholarhub.ui.theme.TextStrong
import com.example.scholarhub.ui.theme.TextVariant
import com.example.scholarhub.ui.theme.nunitoSans
import kotlinx.coroutines.launch

private val CardShape20 = RoundedCornerShape(20.dp)
private val Shape12 = RoundedCornerShape(12.dp)
private val CardShadow = Color(0x0F0F2A66) // #0F2A66 @ 6%
private val TitleText = nunitoSans(14f, 20f, InterSemiBold, tracking = 0.14f)
private val MetaText = nunitoSans(12f, 16f, FontWeight.Normal)
private val ButtonText = nunitoSans(13f, 19.5f, FontWeight.Medium)

enum class DocumentFilter(val label: String) { All("Semua"), Free("Gratis"), Paid("Berbayar"), Owned("Sudah Dibeli") }

/**
 * Daftar Berkas & Dokumen (Figma `266:12391`, setelah membeli `255:10786`): info premium, pencarian, filter dengan jumlah,
 * dan kartu dokumen. Dokumen premium yang sudah dibeli bisa dipratinjau dan diunduh seperti dokumen gratis.
 */
@Composable
fun DocumentsScreen(
    store: DocumentStore,
    onBuy: (DocumentItem) -> Unit,
    onPreview: (DocumentItem) -> Unit,
    onOpenNotifications: () -> Unit = {},
    onTabSelect: (MenteeNavTab) -> Unit = {},
    initialFilter: DocumentFilter = DocumentFilter.All,
    onOpenTransactions: () -> Unit = {},
    onOpenFavorites: () -> Unit = {},
) {
    val purchases by store.purchases.collectAsState()
    var filter by rememberSaveable { mutableStateOf(initialFilter) }
    var query by rememberSaveable { mutableStateOf("") }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val owned = purchases.filter { it.payment == com.example.scholarhub.feature.mentoring.PaymentStatus.Paid }.map { it.document.id }.toSet()
    fun DocumentFilter.matches(doc: DocumentItem) = when (this) {
        DocumentFilter.All -> true
        DocumentFilter.Free -> !doc.premium
        DocumentFilter.Paid -> doc.premium
        DocumentFilter.Owned -> doc.id in owned
    }
    val q = query.trim()
    val visible = DocumentCatalog.filter { filter.matches(it) && (q.isEmpty() || it.title.contains(q, ignoreCase = true)) }
    val download = { doc: DocumentItem ->
        val saved = saveDocumentPdf(context, doc)
        scope.launch { snackbar.showSnackbar(if (saved) "Dokumen tersimpan di folder Download." else "Dokumen gagal diunduh.") }
        Unit
    }

    Scaffold(
        containerColor = Base,
        topBar = { ScholarHubTopBar(onArticleClick = onOpenTransactions, onNotificationClick = onOpenNotifications, onBookmarkClick = onOpenFavorites) },
        bottomBar = { MenteeBottomBar(currentTab = MenteeNavTab.Documents, onTabSelect = onTabSelect) },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("Daftar Berkas & Dokumen", style = MaterialTheme.typography.titleMedium, color = Navy)
            PremiumInfoBox()
            SearchField(query = query, onQueryChange = { query = it }, placeholder = "Cari Berkas atau Dokumen...")
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                DocumentFilter.entries.forEach { f ->
                    CountChip(f.label, DocumentCatalog.count { f.matches(it) }, selected = f == filter) { filter = f }
                }
            }
            visible.forEach { doc ->
                DocumentCard(
                    doc = doc,
                    owned = !doc.premium || doc.id in owned,
                    onBuy = { onBuy(doc) },
                    onPreview = { onPreview(doc) },
                    onDownload = { download(doc) },
                )
            }
            if (visible.isEmpty()) {
                Text("Belum ada dokumen di kategori \"${filter.label}\".", style = MetaText, color = TextMuted)
            } else {
                EndOfFeed("Semua dokumen telah dimuat")
            }
        }
    }
}

/** Info premium (Figma `Overlay+Border`): latar #CCE5FF 20%, ikon gembok. */
@Composable
private fun PremiumInfoBox() {
    val shape = RoundedCornerShape(18.dp)
    Row(
        Modifier.fillMaxWidth().clip(shape).background(SkyBorder.copy(alpha = 0.2f)).border(1.dp, SkyBorder.copy(alpha = 0.4f), shape).padding(17.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Image(painterResource(R.drawable.ic_docs_lock), null, Modifier.size(14.667.dp, 21.25.dp))
        Text(
            "Dokumen Premium memerlukan pembayaran satu kali untuk mendapatkan akses penuh dan dapat dibuka kembali kapan saja melalui akunmu.",
            modifier = Modifier.padding(end = 8.67.dp),
            style = nunitoSans(12f, 19.5f, FontWeight.Normal),
            color = OceanDark,
        )
    }
}

/**
 * Kartu dokumen (Figma `Background+Border+Shadow`): ikon, judul, meta, chip Premium/GRATIS, lalu "Beli" untuk premium
 * yang belum dibeli atau "Pratinjau" + "Unduh".
 */
@Composable
private fun DocumentCard(doc: DocumentItem, owned: Boolean, onBuy: () -> Unit, onPreview: () -> Unit, onDownload: () -> Unit) {
    Column(
        Modifier.fillMaxWidth()
            .shadow(8.dp, CardShape20, ambientColor = CardShadow, spotColor = CardShadow)
            .clip(CardShape20).background(SurfaceCard)
            .then(if (doc.premium) Modifier.border(1.dp, SkyBorder.copy(alpha = 0.6f), CardShape20) else Modifier)
            .padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                Modifier.padding(top = 2.dp).size(48.dp).clip(Shape12).background(if (doc.premium) SkyBorder.copy(alpha = 0.5f) else SkySoft),
                contentAlignment = Alignment.Center,
            ) {
                Image(painterResource(R.drawable.ic_docs_file), null, Modifier.size(17.333.dp, 21.667.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    Text(doc.title, modifier = Modifier.weight(1f).padding(end = 2.58.dp), style = TitleText, color = Navy)
                    Box(Modifier.width(26.dp).height(28.dp)) {
                        Box(Modifier.offset(y = (-4).dp).size(32.dp).clip(CircleShape), contentAlignment = Alignment.Center) {
                            Image(painterResource(R.drawable.ic_docs_more), "Opsi dokumen", Modifier.size(3.333.dp, 13.333.dp))
                        }
                    }
                }
                Text(doc.meta.joinToString("  •  "), style = MetaText, color = TextMuted)
                Row(
                    Modifier.padding(top = 6.dp).clip(PillShape).background(SkySoft).padding(horizontal = 10.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(painterResource(R.drawable.ic_docs_check), null, Modifier.size(10.833.dp))
                    Text(if (doc.premium) "Premium" else "GRATIS", style = nunitoSans(10f, 14f, InterSemiBold, tracking = 0.4f), color = OceanDark)
                }
            }
        }
        if (!owned) {
            PrimaryButton(
                text = "Beli ${rupiah(doc.price ?: 0)}",
                onClick = onBuy,
                modifier = Modifier.fillMaxWidth(),
                containerColor = Navy,
                contentColor = Sky,
                solidShadow = null,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                textStyle = ButtonText,
            )
        } else {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PrimaryButton(
                    text = "Pratinjau",
                    onClick = onPreview,
                    modifier = Modifier.weight(1f),
                    containerColor = Sky,
                    contentColor = Ocean,
                    solidShadow = null,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    textStyle = ButtonText,
                    iconGap = 6.dp,
                    leadingContent = { Image(painterResource(R.drawable.ic_docs_preview), null, Modifier.size(16.5.dp, 11.25.dp)) },
                )
                PrimaryButton(
                    text = "Unduh",
                    onClick = onDownload,
                    modifier = Modifier.weight(1f),
                    containerColor = Navy,
                    contentColor = Sky,
                    solidShadow = null,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    textStyle = ButtonText,
                    iconGap = 6.dp,
                    leadingContent = { Image(painterResource(R.drawable.ic_docs_download), null, Modifier.size(12.dp)) },
                )
            }
        }
    }
}

/** Detail Pembayaran dokumen (Figma `255:10275`): ringkasan dokumen, metode QRIS, "Bayar Sekarang". */
@Composable
fun DocumentPaymentScreen(doc: DocumentItem, onBack: () -> Unit, onPay: () -> Unit) {
    Scaffold(containerColor = Base, topBar = { ScholarHubDetailTopBar(title = "Pembayaran", onBack = onBack) }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.492.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.246.dp)) {
                PaymentSectionLabel("Ringkasan Dokumen")
                Column(
                    Modifier.fillMaxWidth().cardSurface(shape = PaymentCardShape).padding(16.492.dp),
                    verticalArrangement = Arrangement.spacedBy(16.492.dp),
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.492.dp)) {
                        Box(Modifier.size(49.dp).clip(Shape12).background(SkyBorder.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                            Image(painterResource(R.drawable.ic_docs_file), null, Modifier.size(17.333.dp, 21.667.dp))
                        }
                        Column(Modifier.weight(1f)) {
                            Text(doc.title, Modifier.padding(bottom = 4.dp), style = MaterialTheme.typography.titleMedium, color = Navy)
                            Text("Dokumen Premium", style = MaterialTheme.typography.labelLarge, color = Ocean)
                            Text("Pembayaran satu kali · Akses selamanya", style = MaterialTheme.typography.labelLarge, color = TextVariant)
                        }
                    }
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(8.246.dp)).background(SkySoft).padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(painterResource(R.drawable.ic_docs_lock), null, Modifier.size(11.dp, 15.dp))
                        Text("Akses terbuka setelah pembayaran berhasil", style = MaterialTheme.typography.labelLarge, color = TextStrong)
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Harga Dokumen", style = MaterialTheme.typography.labelLarge, color = TextVariant)
                        Text(rupiah(doc.price ?: 0), style = MaterialTheme.typography.titleMedium, color = Navy)
                    }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.246.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    PaymentSectionLabel("Metode Pembayaran")
                    Row(horizontalArrangement = Arrangement.spacedBy(4.123.dp), verticalAlignment = Alignment.CenterVertically) {
                        Image(painterResource(R.drawable.ic_pay_verified), null, Modifier.size(10.308.dp, 12.885.dp))
                        Text("Verifikasi otomatis", style = MaterialTheme.typography.labelSmall, color = TextVariant)
                    }
                }
                PaymentMethodCard(method = SamplePaymentMethods.first(), selected = true, onClick = {})
            }
            PayButton(text = "Bayar Sekarang", onClick = onPay)
        }
    }
}

/** Pratinjau Dokumen (Figma `266:13035`): halaman pertama berisi contoh, lalu "Unduh Dokumen". */
@Composable
fun DocumentPreviewScreen(doc: DocumentItem, onBack: () -> Unit) {
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    Scaffold(
        containerColor = Base,
        topBar = { ScholarHubDetailTopBar(title = "Pratinjau Dokumen", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(doc.title, style = MaterialTheme.typography.titleMedium, color = Navy)
                Text(if (doc.premium) "Dokumen Premium · PDF" else "Dokumen Gratis · PDF", style = nunitoSans(11f, 16.5f, FontWeight.Normal), color = Navy)
            }
            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(SkySoft).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.fillMaxWidth().background(SurfaceCard).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(doc.previewLabel, style = MaterialTheme.typography.titleMedium, color = Navy)
                    Text(doc.title, style = nunitoSans(23f, 38f, FontWeight.Normal), color = Navy)
                    doc.pages.take(2).forEach { (heading, body) ->
                        Text(heading, style = MaterialTheme.typography.titleSmall, color = Navy)
                        Text(body, style = MaterialTheme.typography.labelLarge, color = Navy)
                    }
                }
                Text("Halaman 1 dari ${doc.pages.size}", style = nunitoSans(11f, 16.5f, FontWeight.Normal), color = Navy)
            }
            PrimaryButton(
                text = "Unduh Dokumen",
                onClick = {
                    val saved = saveDocumentPdf(context, doc)
                    scope.launch { snackbar.showSnackbar(if (saved) "Dokumen tersimpan di folder Download." else "Dokumen gagal diunduh.") }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                solidShadow = null,
            )
        }
    }
}

/**
 * Membuat PDF A4 dari isi contoh dokumen dan menyimpannya ke Download/ScholarHub lewat MediaStore (API 29+, tanpa izin).
 * shortcut: isi PDF adalah teks contoh, bukan berkas asli; ganti dengan unduhan dari server saat katalog dokumen tersedia.
 */
fun saveDocumentPdf(context: Context, doc: DocumentItem): Boolean = try {
    val pdf = PdfDocument()
    val pageWidth = 595
    val pageHeight = 842
    val margin = 48
    val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 20f; isFakeBoldText = true }
    val headPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 14f; isFakeBoldText = true }
    val bodyPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 12f }
    doc.pages.forEachIndexed { index, (heading, body) ->
        val page = pdf.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, index + 1).create())
        val canvas = page.canvas
        var y = margin.toFloat()
        listOf(doc.title to titlePaint, heading to headPaint, body to bodyPaint).forEach { (text, paint) ->
            val layout = StaticLayout.Builder.obtain(text, 0, text.length, paint, pageWidth - margin * 2)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL).build()
            canvas.save()
            canvas.translate(margin.toFloat(), y)
            layout.draw(canvas)
            canvas.restore()
            y += layout.height + 16f
        }
        pdf.finishPage(page)
    }
    val values = ContentValues().apply {
        put(MediaStore.Downloads.DISPLAY_NAME, "${doc.title.replace(Regex("[^A-Za-z0-9 ]"), "").trim()}.pdf")
        put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
        put(MediaStore.Downloads.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/ScholarHub")
    }
    val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
    val ok = uri != null && context.contentResolver.openOutputStream(uri)?.use { pdf.writeTo(it) } != null
    pdf.close()
    ok
} catch (_: Exception) {
    false
}
