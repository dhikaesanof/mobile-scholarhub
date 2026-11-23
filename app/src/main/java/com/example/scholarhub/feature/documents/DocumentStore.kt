package com.example.scholarhub.feature.documents

import com.example.scholarhub.feature.mentoring.PaymentStatus
import com.example.scholarhub.feature.mentoring.PaymentWindow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.Clock
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/** Berkas di menu Dokumen. [price] null = gratis. [pages] adalah isi contoh untuk pratinjau dan PDF unduhan. */
data class DocumentItem(
    val id: String,
    val title: String,
    val meta: List<String>,
    val price: Int?,
    val pages: List<Pair<String, String>>,
    /** Label pendek di atas halaman pratinjau (Figma "PANDUAN ESAI LPDP"). */
    val previewLabel: String,
) {
    val premium: Boolean get() = price != null
}

private val EssayGuide = listOf(
    "1. Memahami tujuan esai" to "Gunakan esai untuk menjelaskan perjalananmu, alasan memilih studi, serta kontribusi yang ingin kamu berikan.",
    "2. Susunan cerita" to "Pembuka: pengalaman yang membentuk tujuanmu.\n\nIsi: tantangan, tindakan, dan hasil yang kamu capai.\n\nPenutup: rencana studi dan kontribusi yang terukur.",
    "3. Periksa ulang" to "Pastikan setiap paragraf menjawab pertanyaan esai, gunakan contoh konkret, dan minta umpan balik mentor sebelum mengirim.",
)

/** Contoh statis sesuai Figma `234:11761` (dua dokumen premium dan satu gratis); belum ada katalog dokumen. */
val DocumentCatalog = listOf(
    DocumentItem("esai-lpdp-1", "Panduan & Master Template Esai LPDP 2026", listOf("Awardee Verified", "Akses Selamanya"), 35_000, EssayGuide, "PANDUAN ESAI LPDP"),
    DocumentItem("esai-lpdp-2", "Panduan & Master Template Esai LPDP 2026", listOf("Awardee Verified", "Akses Selamanya"), 35_000, EssayGuide, "PANDUAN ESAI LPDP"),
    DocumentItem(
        "cv-ats",
        "CV ATS Internasional",
        listOf("Awardee Verified"),
        null,
        listOf(
            "1. Struktur CV" to "Ringkasan profil, pendidikan, pengalaman, proyek, dan keterampilan dalam satu kolom agar mudah dibaca sistem ATS.",
            "2. Kata kunci" to "Sesuaikan kata kunci dengan deskripsi program atau beasiswa yang kamu lamar.",
        ),
        "PANDUAN CV ATS",
    ),
)

data class DocumentPurchase(
    val id: String,
    val invoice: String,
    val document: DocumentItem,
    val createdAt: LocalDateTime,
    val payment: PaymentStatus,
    val paidAt: LocalDateTime? = null,
) {
    val payBefore: LocalDateTime get() = createdAt.plus(PaymentWindow)

    /** Tagihan yang tidak dibayar sampai batasnya dianggap gagal. */
    fun statusAt(now: LocalDateTime): PaymentStatus =
        if (payment == PaymentStatus.Pending && !now.isBefore(payBefore)) PaymentStatus.Failed else payment
}

/**
 * Pembelian dokumen di memori untuk satu sesi masuk. Data awal: satu dokumen premium sudah dibeli (Figma "Sudah Dibeli").
 * shortcut: belum ada backend pembayaran; "Periksa Status Pembayaran" langsung menandai lunas. Ganti saat API tersedia.
 */
class DocumentStore(private val clock: Clock = Clock.systemDefaultZone()) {
    fun now(): LocalDateTime = LocalDateTime.now(clock)

    private val state = MutableStateFlow(
        listOf(
            now().minusDays(1).let { at ->
                DocumentPurchase("D01", invoiceFor(at, 89), DocumentCatalog[1], at, PaymentStatus.Paid, at.plusMinutes(3))
            },
        ),
    )
    val purchases: StateFlow<List<DocumentPurchase>> = state.asStateFlow()

    fun find(id: String): DocumentPurchase? = state.value.firstOrNull { it.id == id }

    fun owns(documentId: String): Boolean = state.value.any { it.document.id == documentId && it.payment == PaymentStatus.Paid }

    fun buy(document: DocumentItem): DocumentPurchase {
        val at = now()
        val number = state.value.size + 90
        val purchase = DocumentPurchase("D$number", invoiceFor(at, number), document, at, PaymentStatus.Pending)
        state.update { it + purchase }
        return purchase
    }

    fun markPaid(id: String) {
        state.update { list -> list.map { if (it.id == id) it.copy(payment = PaymentStatus.Paid, paidAt = now()) else it } }
    }

    private fun invoiceFor(at: LocalDateTime, number: Int) = "INV/DOC/${at.format(DateTimeFormatter.BASIC_ISO_DATE)}/%03d".format(number)
}
