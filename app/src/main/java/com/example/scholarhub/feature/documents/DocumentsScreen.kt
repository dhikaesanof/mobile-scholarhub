package com.example.scholarhub.feature.documents

import androidx.annotation.DrawableRes
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.ui.components.MenteeBottomBar
import com.example.scholarhub.ui.components.MenteeNavTab
import com.example.scholarhub.ui.components.PrimaryButton
import com.example.scholarhub.ui.components.ScholarHubTopBar
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.InterSemiBold
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.NavyStrong
import com.example.scholarhub.ui.theme.Ocean
import com.example.scholarhub.ui.theme.OceanDark
import com.example.scholarhub.ui.theme.PillShape
import com.example.scholarhub.ui.theme.ScholarHubTheme
import com.example.scholarhub.ui.theme.ShadowSubtle
import com.example.scholarhub.ui.theme.Sky
import com.example.scholarhub.ui.theme.SkyBorder
import com.example.scholarhub.ui.theme.SkySoft
import com.example.scholarhub.ui.theme.SurfaceCard
import com.example.scholarhub.ui.theme.TextMuted
import com.example.scholarhub.ui.theme.TextVariant
import com.example.scholarhub.ui.theme.nunitoSans

private val CardShape20 = RoundedCornerShape(20.dp)
private val Shape12 = RoundedCornerShape(12.dp)
private val CardShadow = Color(0x0F0F2A66) // #0F2A66 @ 6%
private val ChipText = nunitoSans(12f, 16f, InterSemiBold, tracking = 0.24f)
private val TitleText = nunitoSans(14f, 20f, InterSemiBold, tracking = 0.14f)
private val MetaText = nunitoSans(12f, 16f, FontWeight.Normal)
private val ButtonText = nunitoSans(13f, 19.5f, FontWeight.Medium)

enum class DocumentFilter(val label: String) { All("Semua"), Free("Gratis"), Premium("Premium (Berbayar)"), Academic("Akademik"), Exclusive("Eksklusif") }

@Immutable
data class DocumentUi(
    val id: String,
    val title: String,
    val meta: List<String>,
    /** Null = gratis; selain itu label harga untuk tombol "Beli". */
    val price: String?,
    val categories: Set<DocumentFilter> = emptySet(),
)

data class DocumentsUiState(val documents: List<DocumentUi>, val totalAvailable: Int)

/** Contoh statis sesuai Figma (dua kartu premium yang sama dan satu gratis); belum ada katalog dokumen. */
val SampleDocumentsUiState = DocumentsUiState(
    documents = listOf(
        DocumentUi("esai-lpdp-1", "Panduan & Master Template Esai LPDP 2026", listOf("Awardee Verified", "Akses Selamanya"), "Rp 35.000"),
        DocumentUi("esai-lpdp-2", "Panduan & Master Template Esai LPDP 2026", listOf("Awardee Verified", "Akses Selamanya"), "Rp 35.000"),
        DocumentUi("cv-ats", "CV ATS Internasional", listOf("Awardee Verified"), null),
    ),
    totalAvailable = 4,
)

/** Dokumen (Figma 846:4487, tab Premium): filter, info dokumen premium, dan daftar berkas. */
@Composable
fun DocumentsScreen(
    state: DocumentsUiState = SampleDocumentsUiState,
    onBuy: (DocumentUi) -> Unit = {},
    onPreview: (DocumentUi) -> Unit = {},
    onDownload: (DocumentUi) -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    onTabSelect: (MenteeNavTab) -> Unit = {},
) {
    var filter by rememberSaveable { mutableStateOf(DocumentFilter.All) }
    val visible = state.documents.filter {
        when (filter) {
            DocumentFilter.All -> true
            DocumentFilter.Free -> it.price == null
            DocumentFilter.Premium -> it.price != null
            DocumentFilter.Academic, DocumentFilter.Exclusive -> filter in it.categories
        }
    }

    Scaffold(
        containerColor = Base,
        topBar = { ScholarHubTopBar(onNotificationClick = onOpenNotifications) },
        bottomBar = { MenteeBottomBar(currentTab = MenteeNavTab.Premium, onTabSelect = onTabSelect) },
    ) { padding ->
        // Figma: konten mulai 3 dp di bawah header, spacer 8 + jarak 24 sebelum chip (total 35), padding samping 16.
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 35.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).height(36.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                DocumentFilter.entries.forEach { DocumentChip(it, selected = it == filter, onClick = { filter = it }) }
            }

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Daftar Berkas & Dokumen", style = nunitoSans(18f, 24f, FontWeight.Bold), color = Navy)
                    Text("${state.totalAvailable} Berkas Tersedia", style = MetaText, color = TextMuted)
                }
                PremiumInfoBox()
                visible.forEach { doc ->
                    if (doc.price != null) {
                        PremiumDocumentCard(doc, onBuy = { onBuy(doc) })
                    } else {
                        FreeDocumentCard(doc, onPreview = { onPreview(doc) }, onDownload = { onDownload(doc) })
                    }
                }
                if (visible.isEmpty()) {
                    Text("Belum ada dokumen di kategori \"${filter.label}\".", style = MetaText, color = TextMuted)
                }
            }
        }
    }
}

/** Chip filter: aktif #0F2A66, "Premium" berlatar #CCE5FF 50% dengan ikon, lainnya #ECF5FB. */
@Composable
private fun DocumentChip(filter: DocumentFilter, selected: Boolean, onClick: () -> Unit) {
    val premium = filter == DocumentFilter.Premium
    val background = when {
        selected -> NavyStrong
        premium -> SkyBorder.copy(alpha = 0.5f)
        else -> SkySoft
    }
    val content = when {
        selected -> SurfaceCard
        premium -> OceanDark
        else -> TextVariant
    }
    Row(
        Modifier.then(if (selected) Modifier.shadow(1.dp, PillShape, ambientColor = ShadowSubtle, spotColor = ShadowSubtle) else Modifier)
            .clip(PillShape).background(background).clickable(role = Role.Tab, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (premium) {
            Image(
                painterResource(R.drawable.ic_docs_premium), null, Modifier.size(10.667.dp, 14.dp),
                colorFilter = if (selected) ColorFilter.tint(SurfaceCard) else null,
            )
        }
        Text(filter.label, style = ChipText, color = content)
    }
}

@Composable
private fun PremiumInfoBox() {
    val shape = RoundedCornerShape(18.dp)
    Row(
        Modifier.fillMaxWidth().clip(shape).background(SkyBorder.copy(alpha = 0.2f)).border(1.dp, SkyBorder.copy(alpha = 0.4f), shape).padding(17.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Image(painterResource(R.drawable.ic_docs_lock), null, Modifier.size(14.667.dp, 21.25.dp))
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = InterSemiBold)) { append("Dokumen Premium Terkunci:") }
                append(" Dokumen bertanda gembok memerlukan pembayaran sekali bayar untuk membuka akses penuh dan unduh langsung terintegrasi dengan metode pembayaran resmi ScholarHub.")
            },
            modifier = Modifier.padding(end = 8.67.dp),
            style = nunitoSans(12f, 19.5f, FontWeight.Normal),
            color = OceanDark,
        )
    }
}

@Composable
private fun DocumentHeader(doc: DocumentUi, iconBackground: Color, extra: (@Composable () -> Unit)? = null) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(Modifier.padding(top = 2.dp).size(48.dp).clip(Shape12).background(iconBackground), contentAlignment = Alignment.Center) {
            Image(painterResource(R.drawable.ic_docs_file), null, Modifier.size(17.333.dp, 21.667.dp))
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Text(doc.title, modifier = Modifier.weight(1f).padding(end = 2.58.dp), style = TitleText, color = Navy)
                // Figma: tombol 32 dp yang menjorok 4 dp ke atas dari slot 28 dp.
                Box(Modifier.width(26.dp).height(28.dp)) {
                    Box(
                        Modifier.offset(y = (-4).dp).size(32.dp).clip(CircleShape).clickable(onClick = {}),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(painterResource(R.drawable.ic_docs_more), "Opsi dokumen", Modifier.size(3.333.dp, 13.333.dp))
                    }
                }
            }
            Text(doc.meta.joinToString("  •  "), style = MetaText, color = TextMuted)
            extra?.invoke()
        }
    }
}

/** Kartu dokumen premium (Figma `Background+Border+Shadow`): border #CCE5FF 60%, tombol "Beli". */
@Composable
private fun PremiumDocumentCard(doc: DocumentUi, onBuy: () -> Unit) {
    Column(
        Modifier.fillMaxWidth()
            .shadow(8.dp, CardShape20, ambientColor = CardShadow, spotColor = CardShadow)
            .clip(CardShape20).background(SurfaceCard).border(1.dp, SkyBorder.copy(alpha = 0.6f), CardShape20)
            .padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DocumentHeader(doc, iconBackground = SkyBorder.copy(alpha = 0.5f))
        PrimaryButton(
            text = "Beli ${doc.price}",
            onClick = onBuy,
            modifier = Modifier.fillMaxWidth().shadow(1.dp, Shape12, ambientColor = ShadowSubtle, spotColor = ShadowSubtle),
            containerColor = Sky,
            contentColor = Ocean,
            solidShadow = null,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            textStyle = nunitoSans(13f, 19.5f, FontWeight.Bold),
        )
    }
}

/** Kartu dokumen gratis (Figma `Background+Shadow`): chip "GRATIS", tombol Pratinjau dan Unduh. */
@Composable
private fun FreeDocumentCard(doc: DocumentUi, onPreview: () -> Unit, onDownload: () -> Unit) {
    Column(
        Modifier.fillMaxWidth()
            .shadow(5.dp, CardShape20, ambientColor = CardShadow, spotColor = CardShadow)
            .clip(CardShape20).background(SurfaceCard)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DocumentHeader(doc, iconBackground = SkySoft) {
            // Figma: chip "GRATIS" di kolom teks, 6 dp di bawah meta.
            Row(
                Modifier.padding(top = 4.dp).clip(PillShape).background(SkySoft).padding(horizontal = 10.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(painterResource(R.drawable.ic_docs_check), null, Modifier.size(10.833.dp))
                Text("GRATIS", style = nunitoSans(10f, 14f, InterSemiBold, tracking = 0.4f), color = OceanDark)
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PrimaryButton(
                text = "Pratinjau",
                onClick = onPreview,
                modifier = Modifier.width(165.dp),
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

@Preview(widthDp = 402, heightDp = 1005)
@Composable
private fun DocumentsScreenPreview() {
    ScholarHubTheme { DocumentsScreen() }
}
