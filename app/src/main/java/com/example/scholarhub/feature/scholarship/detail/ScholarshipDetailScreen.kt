package com.example.scholarhub.feature.scholarship.detail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.data.scholarship.ScholarshipJsonParser
import com.example.scholarhub.domain.scholarship.Scholarship
import com.example.scholarhub.domain.scholarship.ScholarshipBenefit
import com.example.scholarhub.domain.scholarship.ScholarshipRegistration
import com.example.scholarhub.domain.scholarship.ScholarshipSection
import com.example.scholarhub.domain.scholarship.TimelineStep
import com.example.scholarhub.feature.scholarship.components.providerInitials
import com.example.scholarhub.ui.components.MascotInfoBanner
import com.example.scholarhub.ui.components.PrimaryButton
import com.example.scholarhub.ui.components.ScholarHubDetailTopBar
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.ErrorBadge
import com.example.scholarhub.ui.theme.ErrorRed
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.Ocean
import com.example.scholarhub.ui.theme.OutlineSoft
import com.example.scholarhub.ui.theme.PillShape
import com.example.scholarhub.ui.theme.ScholarHubTheme
import com.example.scholarhub.ui.theme.Sky
import com.example.scholarhub.ui.theme.Slate500
import com.example.scholarhub.ui.theme.Slate600
import com.example.scholarhub.ui.theme.Slate800
import com.example.scholarhub.ui.theme.Slate900
import com.example.scholarhub.ui.theme.Success
import com.example.scholarhub.ui.theme.SuccessContainer
import com.example.scholarhub.ui.theme.SurfaceCard
import com.example.scholarhub.ui.theme.TextStrong
import com.example.scholarhub.ui.theme.TitleInk
import com.example.scholarhub.ui.theme.nunitoSans
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import androidx.compose.foundation.shape.CircleShape

private val CardShape16 = RoundedCornerShape(16.dp)
private val Shape12 = RoundedCornerShape(12.dp)
private val LinkInk = Color(0xFF0C4A6E) // teks "Buka Tautan"
private val SectionShadow = Color(0x0D000000) // Figma: 0 4 20 -4 hitam 5%
private val Indonesian = Locale.forLanguageTag("id-ID")
private val DateFormat = DateTimeFormatter.ofPattern("d MMMM yyyy", Indonesian)

private val SectionTitle = nunitoSans(19f, 28.5f, FontWeight.Bold)
private val BodyText = nunitoSans(13f, 19.5f, FontWeight.SemiBold)

private enum class DetailTab(val label: String) {
    Overview("Ringkasan"), Requirements("Persyaratan"), Guide("Panduan"),
}

/**
 * Detail Beasiswa (Figma 846:4937). Isi diambil dari katalog; hanya subbab tab aktif yang dirender dan
 * subbab tanpa isi disembunyikan. [scholarMatchRelevancePercent] hanya diisi jika dibuka dari hasil ScholarMatch.
 */
@Composable
fun ScholarshipDetailScreen(
    model: ScholarshipDetailViewModel,
    onBack: () -> Unit,
    scholarMatchRelevancePercent: Int? = null,
    onFindMentor: () -> Unit = {},
    favorite: Boolean = false,
    onToggleFavorite: () -> Unit = {},
) {
    val state by model.uiState.collectAsState()
    ScholarshipDetailContent(state, onBack, model::load, scholarMatchRelevancePercent, onFindMentor, favorite, onToggleFavorite)
}

@Composable
private fun ScholarshipDetailContent(
    state: ScholarshipDetailState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    relevancePercent: Int?,
    onFindMentor: () -> Unit,
    favorite: Boolean = false,
    onToggleFavorite: () -> Unit = {},
) {
    val uriHandler = LocalUriHandler.current
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val openLink: (String) -> Unit = { url ->
        try {
            uriHandler.openUri(url)
        } catch (_: IllegalArgumentException) {
            scope.launch { snackbar.showSnackbar("Tautan tidak dapat dibuka.") }
        }
    }
    Scaffold(
        containerColor = Base,
        topBar = {
            // Figma detail beasiswa tanpa bottom nav; ikon favorit ada di kartu atas.
            ScholarHubDetailTopBar(title = "Detail Beasiswa", onBack = onBack)
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (state) {
                is ScholarshipDetailState.Ready -> {
                    val scholarship = state.scholarship
                    var tab by rememberSaveable(scholarship.id) { mutableStateOf(DetailTab.Overview) }
                    // Figma: konten 356 dp di x=23, 16 dp di bawah header, jarak antarkartu 16.
                    Column(
                        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                            .padding(start = 23.dp, end = 23.dp, top = 16.dp, bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Hero(scholarship, relevancePercent, favorite, onToggleFavorite)
                        DetailTabs(tab) { tab = it }
                        // Figma: portal resmi tampil di ketiga tab, tepat di bawah tab.
                        scholarship.registration.applicationUrl?.takeIf { it.isNotBlank() }?.let { url ->
                            Portal("PORTAL RESMI PENDAFTARAN", url) { openLink(url) }
                        }
                        when (tab) {
                            DetailTab.Overview -> Overview(scholarship)
                            DetailTab.Requirements -> Sections(scholarship.requirements)
                            DetailTab.Guide -> {
                                Sections(scholarship.guide.sections, today = ZonedDateTime.now(scholarship.registration.timeZone).toLocalDate())
                                scholarship.guide.officialGuideUrl?.takeIf { it.isNotBlank() }?.let { url ->
                                    Portal("PANDUAN RESMI", url) { openLink(url) }
                                }
                                // Figma `130:8903`: banner mentor hanya di akhir tab Panduan.
                                MentorCtaBanner(onFindMentor)
                            }
                        }
                    }
                }
                else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    when (state) {
                        ScholarshipDetailState.Loading -> CircularProgressIndicator(color = Ocean)
                        ScholarshipDetailState.NotFound -> Text("Beasiswa tidak ditemukan.", style = BodyText, color = Slate600)
                        ScholarshipDetailState.Error -> Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Text("Detail beasiswa gagal dimuat.", style = BodyText, color = Slate600)
                            PrimaryButton(text = "Coba Lagi", onClick = onRetry)
                        }
                        is ScholarshipDetailState.Ready -> Unit
                    }
                }
            }
        }
    }
}

/** Kartu putih subbab (Figma `AboutSection`): border #F1F5F9, radius 16, padding 21/17, gap 11. */
@Composable
private fun SectionCard(title: String, verticalPadding: Int = 17, content: @Composable () -> Unit) {
    Column(
        Modifier.fillMaxWidth()
            .shadow(6.dp, CardShape16, ambientColor = SectionShadow, spotColor = SectionShadow)
            .clip(CardShape16)
            .background(SurfaceCard)
            .border(1.dp, OutlineSoft, CardShape16)
            .padding(horizontal = 21.dp, vertical = verticalPadding.dp),
        verticalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        Text(text = title, style = SectionTitle, color = Slate900)
        content()
    }
}

@Composable
private fun Hero(scholarship: Scholarship, relevancePercent: Int?, favorite: Boolean, onToggleFavorite: () -> Unit) {
    val registration = scholarship.registration
    val status = registrationStatus(registration)
    Column(
        Modifier.fillMaxWidth()
            .shadow(6.dp, CardShape16, ambientColor = SectionShadow, spotColor = SectionShadow)
            .clip(CardShape16)
            .background(SurfaceCard)
            .border(1.dp, OutlineSoft, CardShape16)
            .padding(21.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            // Figma: kotak 80 navy berborder #006398; katalog belum punya logo, jadi diisi inisial penyelenggara.
            Box(
                Modifier.size(80.dp).clip(Shape12).background(Navy).border(1.dp, Ocean, Shape12),
                contentAlignment = Alignment.Center,
            ) {
                Text(providerInitials(scholarship.provider), style = nunitoSans(24f, 32f, FontWeight.ExtraBold), color = SurfaceCard)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                relevancePercent?.let {
                    Text(
                        text = "$it% Relevan",
                        modifier = Modifier.clip(PillShape).background(Ocean).padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = SurfaceCard,
                    )
                }
                // Figma `Favorite` 24 dp #FFA99D: garis saat belum difavoritkan, terisi saat difavoritkan.
                Image(
                    painterResource(if (favorite) R.drawable.ic_detail_favorite_on else R.drawable.ic_detail_favorite_off),
                    if (favorite) "Hapus dari favorit" else "Tambah ke favorit",
                    Modifier.clip(CircleShape).clickable(role = Role.Button, onClick = onToggleFavorite).padding(horizontal = 8.dp, vertical = 4.dp).size(24.dp),
                )
            }
        }
        Column(Modifier.padding(top = 4.dp)) {
            Text(scholarship.name, modifier = Modifier.padding(bottom = 0.75.dp), style = MaterialTheme.typography.titleMedium, color = TitleInk)
            Text(scholarship.provider, modifier = Modifier.padding(bottom = 0.75.dp), style = BodyText, color = Slate500)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = status.label,
                modifier = Modifier.clip(Shape12).background(status.background).padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = status.foreground,
            )
            registration.closesOn?.format(DateFormat)?.let { deadline ->
                Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End), verticalAlignment = Alignment.CenterVertically) {
                    Image(painterResource(R.drawable.ic_detail_calendar), null, Modifier.size(16.dp))
                    val time = registration.closesAtLocal?.let { " • $it" }.orEmpty()
                    Text("Tutup $deadline$time", style = MaterialTheme.typography.labelSmall, color = Ocean)
                }
            }
        }
    }
}

private enum class RegistrationStatus(val label: String, val foreground: Color, val background: Color) {
    Open("Dibuka", Success, SuccessContainer),
    Closed("Ditutup", ErrorRed, ErrorBadge),
    Upcoming("Belum dibuka", Navy, Sky),
    Unknown("Jadwal belum tersedia", Slate600, OutlineSoft),
}

private fun registrationStatus(registration: ScholarshipRegistration): RegistrationStatus {
    val now = ZonedDateTime.now(registration.timeZone)
    val today = now.toLocalDate()
    val close = registration.closesOn
    val closeTime = registration.closesAtLocal
    return when {
        registration.statusAtSnapshot == "archived" -> RegistrationStatus.Closed
        close != null && today > close -> RegistrationStatus.Closed
        today == close && closeTime != null &&
            now.toLocalTime().truncatedTo(ChronoUnit.MINUTES) > closeTime -> RegistrationStatus.Closed
        registration.opensOn?.let { today < it } == true -> RegistrationStatus.Upcoming
        registration.opensOn != null || close != null -> RegistrationStatus.Open
        else -> RegistrationStatus.Unknown
    }
}

/** Tab (Figma `Section Detail Beasiswa`): latar putih radius 12, tab aktif navy dengan teks #D9EEFB. */
@Composable
private fun DetailTabs(selected: DetailTab, onSelect: (DetailTab) -> Unit) {
    Row(Modifier.fillMaxWidth().clip(Shape12).background(SurfaceCard).selectableGroup()) {
        for (tab in DetailTab.entries) {
            val active = tab == selected
            Box(
                Modifier.weight(1f).clip(Shape12).background(if (active) Navy else SurfaceCard)
                    .selectable(active, role = Role.Tab, onClick = { onSelect(tab) })
                    .heightIn(min = 36.dp).padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(tab.label, style = MaterialTheme.typography.labelLarge, color = if (active) Sky else Navy)
            }
        }
    }
}

@Composable
private fun Overview(scholarship: Scholarship) {
    val summary = scholarship.summary
    if (summary.about.isNotBlank()) {
        SectionCard("Tentang Program", verticalPadding = 21) { Text(summary.about, style = BodyText, color = Slate600) }
    }
    if (summary.benefits.isNotEmpty()) {
        SectionCard("Manfaat") { summary.benefits.forEach { BenefitCard(it) } }
    }
    val coverage = summary.coverage.filter(String::isNotBlank)
    if (coverage.isNotEmpty()) {
        SectionCard("Cakupan Program") { LabelValueLines(coverage) }
    }
}

/** Tautan resmi (Figma `OfficialPortalLink`): kartu #006398, label Bold 13 #D9EEFB, tombol putih "Buka Tautan". */
@Composable
private fun Portal(label: String, url: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clip(CardShape16).background(Ocean).clickable(role = Role.Button, onClick = onClick).padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(label, style = nunitoSans(13f, 19.5f, FontWeight.Bold), color = Sky)
            Text(
                url.substringAfter("://").trimEnd('/'),
                style = MaterialTheme.typography.labelLarge,
                color = OutlineSoft,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Row(
            Modifier.shadow(2.dp, Shape12).clip(Shape12).background(SurfaceCard).padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Buka Tautan", style = MaterialTheme.typography.labelSmall, color = LinkInk)
            Image(painterResource(R.drawable.ic_detail_external), null, Modifier.size(11.958.dp))
        }
    }
}

/** Satu kartu teks tanpa ikon per manfaat (aturan `AGENTS.md`). Nominal null = belum tersedia. */
@Composable
private fun BenefitCard(benefit: ScholarshipBenefit) {
    Column(Modifier.fillMaxWidth().clip(Shape12).background(Sky).border(1.dp, OutlineSoft, Shape12).padding(13.dp)) {
        Text(benefit.title, style = MaterialTheme.typography.titleSmall, color = Slate800)
        val body = MaterialTheme.typography.labelLarge
        // Manfaat nonfinansial (mentoring, pelatihan, magang) tidak punya nominal.
        if (benefit.amountKind != "nonfinancial") {
            val amount = benefit.amountIdr?.let {
                val prefix = if (benefit.amountKind == "maximum") "Hingga Rp" else "Rp"
                prefix + NumberFormat.getNumberInstance(Indonesian).format(it)
            } ?: "Nominal belum tersedia"
            Text(listOfNotNull(amount, benefit.unitOrCoverage).joinToString(" / "), style = body, color = Navy)
        }
        benefit.duration?.let { Text(it, style = body, color = Navy) }
        benefit.description?.let { Text(it, style = body, color = Navy) }
    }
}

@Composable
private fun Sections(sections: List<ScholarshipSection>, today: LocalDate? = null) {
    for (section in sections.filter { it.body.isNotBlank() || it.items.isNotEmpty() || it.steps.isNotEmpty() }) {
        SectionCard(section.title) {
            when (section.key) {
                "educationAcademics" -> LabelValueLines(section.body.lines())
                "selectionSchedule" -> Timeline(section.steps, today)
                "documents" -> {
                    if (section.body.isNotBlank()) Text(section.body, style = BodyText, color = Slate600)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        section.items.forEach { DocumentItem(it) }
                    }
                }
                // Figma "Cara Mendaftar": langkah bernomor.
                "application" -> BodyLines(section.body.lines(), numbered = true)
                else -> BodyLines(section.body.lines())
            }
        }
    }
}

/** Isi beberapa baris ditampilkan sebagai bullet (Figma "Kondisi Pendaftar"); satu baris sebagai paragraf. */
@Composable
private fun BodyLines(lines: List<String>, numbered: Boolean = false) {
    val items = lines.map(String::trim).filter(String::isNotEmpty)
    if (items.size <= 1) {
        items.firstOrNull()?.let { Text(it, style = BodyText, color = Slate600) }
        return
    }
    Column {
        items.forEachIndexed { index, line ->
            Row {
                Text(if (numbered) "${index + 1}." else "•", modifier = Modifier.width(19.5.dp).padding(start = if (numbered) 2.dp else 7.dp), style = BodyText, color = Slate600)
                Text(line, modifier = Modifier.weight(1f), style = BodyText, color = Slate600)
            }
        }
    }
}

/** Grid label 120 dp + nilai (Figma "Pendidikan dan Akademik", "Cakupan Program") untuk baris "Label: nilai". */
@Composable
private fun LabelValueLines(lines: List<String>) {
    Column(Modifier.padding(top = 4.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        for (line in lines.filter(String::isNotBlank)) {
            val fields = line.split(": ", limit = 2)
            if (fields.size == 2) {
                // Label rata atas agar sejajar dengan baris pertama nilai yang panjang (Figma: self-start).
                Row(Modifier.heightIn(min = 24.dp), horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.Top) {
                    Text(fields[0], modifier = Modifier.width(120.dp).padding(top = 2.25.dp), style = MaterialTheme.typography.labelLarge, color = Navy)
                    // Figma: Inter Regular 12 / 24, dipetakan ke Nunito Sans.
                    Text(fields[1], modifier = Modifier.weight(1f), style = nunitoSans(12f, 24f, FontWeight.Normal), color = Navy)
                }
            } else {
                Text(line, style = BodyText, color = Slate600)
            }
        }
    }
}

/** Kartu dokumen (Figma `Coverage Item`): latar #D9EEFB, ikon `insert_drive_file` 24, teks Regular 16. */
@Composable
private fun DocumentItem(text: String) {
    Row(
        Modifier.fillMaxWidth().clip(Shape12).background(Sky).border(1.dp, OutlineSoft, Shape12).padding(13.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(painterResource(R.drawable.ic_detail_document), null, Modifier.size(24.dp))
        Text(text, style = MaterialTheme.typography.bodyLarge, color = Navy)
    }
}

/**
 * Timeline (Figma "Timeline Pendaftaran"): cincin r 6.5 stroke 3 #006398 dengan batang penghubung 3 dp.
 * Satu titik per tahap di data. Cincin tahap yang sudah dimulai per [today], beserta tahap sebelumnya, diisi penuh.
 */
@Composable
private fun Timeline(steps: List<TimelineStep>, today: LocalDate?) {
    val lastReached = today?.let { lastReachedStep(steps, it) } ?: -1
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        steps.forEachIndexed { index, step ->
            Row(Modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Canvas(Modifier.width(16.dp).fillMaxHeight()) {
                    val center = Offset(8.dp.toPx(), 10.dp.toPx())
                    if (index <= lastReached) {
                        drawCircle(Ocean, radius = 8.dp.toPx(), center = center)
                    } else {
                        drawCircle(Ocean, radius = 6.5.dp.toPx(), center = center, style = Stroke(width = 3.dp.toPx()))
                    }
                    if (index < steps.lastIndex) {
                        // Batang masuk 1 dp ke tepi cincin ini dan cincin berikutnya (jarak antartahap 4 dp,
                        // pusat cincin 10 dp dari atas baris, tepi luar 8 dp) supaya tersambung rapat.
                        val top = center.y + 7.dp.toPx()
                        val nextRingTop = size.height + 4.dp.toPx() + center.y - 7.dp.toPx()
                        drawRoundRect(
                            Ocean,
                            topLeft = Offset(6.5.dp.toPx(), top),
                            size = Size(3.dp.toPx(), nextRingTop - top),
                            cornerRadius = CornerRadius.Zero,
                        )
                    }
                }
                Column(Modifier.padding(bottom = if (index < steps.lastIndex) 4.dp else 0.dp)) {
                    Text(step.label, style = nunitoSans(13f, 19.5f, FontWeight.Bold), color = Navy)
                    Text(stepDates(step), style = nunitoSans(13f, 19.5f, FontWeight.Normal), color = Navy)
                    step.note?.let { Text(it, style = nunitoSans(13f, 19.5f, FontWeight.Normal), color = Slate600) }
                }
            }
        }
    }
}

/** Indeks tahap terakhir yang sudah dimulai per [today], atau -1. */
internal fun lastReachedStep(steps: List<TimelineStep>, today: LocalDate): Int =
    steps.indexOfLast { !it.start.isAfter(today) }

/** "9 Desember 2026", "1 - 7 Desember 2026", atau "28 November - 2 Desember 2026" (format Figma). */
internal fun stepDates(step: TimelineStep): String {
    val end = step.end ?: return step.start.format(DateFormat)
    val startPattern = when {
        step.start.year != end.year -> "d MMMM yyyy"
        step.start.month != end.month -> "d MMMM"
        else -> "d"
    }
    return step.start.format(DateTimeFormatter.ofPattern(startPattern, Indonesian)) + " - " + end.format(DateFormat)
}

/** Banner CTA di akhir tab Panduan (Figma: maskot 74 × 105, tombol #006398 "Cari Mentor"). */
@Composable
private fun MentorCtaBanner(onFindMentor: () -> Unit) {
    MascotInfoBanner(
        label = "Panduan Beasiswa",
        title = "Siap Raih Beasiswa Impian?",
        description = "Cek CV & Esai dengan mentor alumni\nsebelum submit berkas!",
        mascotWidth = 74.dp,
        mascotHeight = 105.dp,
        labelStyle = nunitoSans(11f, 16.5f, FontWeight.Bold),
        titleStyle = nunitoSans(13f, 19.5f, FontWeight.Bold),
        descriptionStyle = nunitoSans(13f, 19.5f, FontWeight.SemiBold),
        descriptionColor = TextStrong,
        action = {
            PrimaryButton(
                text = "Cari Mentor",
                onClick = onFindMentor,
                containerColor = Ocean,
                solidShadow = null,
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                textStyle = nunitoSans(12f, 16f, FontWeight.Bold),
            )
        },
    )
}

@Preview(name = "Detail Hana Bank", widthDp = 402, heightDp = 1800)
@Composable
private fun ScholarshipDetailPreview() {
    val assets = LocalContext.current.assets
    val state = remember(assets) {
        val json = assets.open("scholarships.json").bufferedReader().use { it.readText() }
        val scholarship = ScholarshipJsonParser.parse(json).scholarships.first { it.id == "SH01" }
        ScholarshipDetailState.Ready(scholarship)
    }
    ScholarHubTheme {
        ScholarshipDetailContent(state, onBack = {}, onRetry = {}, relevancePercent = null, onFindMentor = {})
    }
}
