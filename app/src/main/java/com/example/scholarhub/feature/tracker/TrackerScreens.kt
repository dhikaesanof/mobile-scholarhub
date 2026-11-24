package com.example.scholarhub.feature.tracker

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.domain.scholarship.Scholarship
import com.example.scholarhub.ui.components.PrimaryButton
import com.example.scholarhub.ui.components.ScholarHubDetailTopBar
import com.example.scholarhub.ui.components.cardSurface
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.SkySoft
import com.example.scholarhub.ui.theme.SurfaceCard
import com.example.scholarhub.ui.theme.nunitoSans
import java.time.LocalDate
import kotlin.math.roundToInt

// Warna frame Tracker (Figma `130:9862`).
private val Link = Color(0xFF0077A8)
private val Muted = Color(0xFF667080)
private val BodyGray = Color(0xFF50545C)
private val CardBorder = Color(0xFFD7E5EF)
private val BarTrack = Color(0xFFDFE8EF)
private val Shape12 = RoundedCornerShape(12.dp)
private val Caption = nunitoSans(11f, 16.5f, FontWeight.Normal)
private val BodySemi = nunitoSans(13f, 19.5f, FontWeight.SemiBold)
private val LabelMedium = nunitoSans(13f, 19.5f, FontWeight.Medium)
private val TitleSmall = nunitoSans(16f, 24f, FontWeight.Bold)

/** Tahap yang sedang dipantau untuk satu beasiswa; null jika jadwalnya tidak memuat tahap tracker. */
data class TrackedStage(val scholarship: Scholarship, val window: StageWindow)

fun Scholarship.trackedStage(today: LocalDate): TrackedStage? =
    currentStage(stageWindows(scheduleSteps()), today)?.let { TrackedStage(this, it) }

/** Beasiswa yang bisa dipilih di tracker: proses seleksinya belum selesai pada [today]. */
fun trackableScholarships(all: List<Scholarship>, today: LocalDate): List<Scholarship> =
    all.filter { s -> stageWindows(s.scheduleSteps()).lastOrNull()?.let { !today.isAfter(it.end) } == true }

/**
 * Kartu Tracker & Reminder di Profil (Figma `389:8104` Pendaftaran, `389:8212` Seleksi Administrasi, `389:8320` Wawancara):
 * pilihan beasiswa, tahap saat ini, progres checklist, dan langkah berikutnya. [onToggle] null = mode baca (dilihat mentor).
 */
@Composable
fun TrackerCard(
    options: List<Scholarship>,
    tracked: TrackedStage?,
    done: Set<String>,
    today: LocalDate,
    onSelect: (String) -> Unit,
    onToggle: ((taskId: String) -> Unit)?,
    onOpenGuide: (TrackerTask) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.fillMaxWidth().clip(Shape12).background(SurfaceCard).border(1.dp, CardBorder, Shape12).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("PROGRESS ROADMAP", style = Caption, color = Muted)
        if (tracked == null) {
            Text("Belum ada beasiswa yang sedang berjalan untuk dipantau.", style = BodySemi, color = Navy)
            return@Column
        }
        ScholarshipPicker(tracked.scholarship.name, options, enabled = onToggle != null, onSelect = onSelect)
        val stage = tracked.window.stage
        val id = tracked.scholarship.id
        Text("Tahap saat ini: ${stage.title}", style = TitleSmall, color = Navy)
        stage.description?.let { Text(it, style = Caption, color = Muted) }
        Text("${tracked.window.dateRange()} · ${tracked.window.status(today)}", style = Caption, color = Link)

        val doneCount = stage.tasks.count { TrackerStore.key(id, it.id) in done }
        val ratio = doneCount.toFloat() / stage.tasks.size
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)).background(BarTrack)) {
                if (ratio > 0f) Box(Modifier.fillMaxHeight().fillMaxWidth(ratio).clip(RoundedCornerShape(4.dp)).background(Link))
            }
            Text(
                "$doneCount dari ${stage.tasks.size} ${stage.unit} · ${(ratio * 100).roundToInt()}% tahap ini",
                style = Caption,
                color = Navy,
            )
        }

        stage.tasks.forEach { task ->
            val checked = TrackerStore.key(id, task.id) in done
            Row(
                Modifier.fillMaxWidth().clip(Shape12).background(SkySoft)
                    .then(if (onToggle != null) Modifier.clickable(role = Role.Checkbox) { onToggle(task.id) } else Modifier)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Image(
                    painterResource(if (checked) R.drawable.ic_tracker_check_on else R.drawable.ic_tracker_check_off),
                    if (checked) "Selesai" else "Belum selesai",
                    Modifier.size(20.dp),
                )
                Text(task.label, Modifier.weight(1f), style = BodySemi, color = Navy)
            }
        }

        val next = stage.tasks.firstOrNull { TrackerStore.key(id, it.id) !in done }
        Column(
            Modifier.fillMaxWidth().clip(Shape12).background(SkySoft).padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text("LANGKAH BERIKUTNYA", style = Caption, color = Muted)
            Text(next?.nextLabel ?: "Semua kegiatan tahap ini sudah selesai", style = LabelMedium, color = Navy)
            if (next != null && onToggle != null) {
                PrimaryButton(
                    text = "Lihat Selengkapnya",
                    onClick = { onOpenGuide(next) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    solidShadow = null,
                )
            }
        }
    }
}

@Composable
private fun ScholarshipPicker(name: String, options: List<Scholarship>, enabled: Boolean, onSelect: (String) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Box {
        Row(
            Modifier.fillMaxWidth().clip(Shape12).border(1.dp, Link, Shape12)
                .clickable(enabled = enabled && options.size > 1, role = Role.DropdownList) { open = true }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(name, Modifier.weight(1f), style = BodySemi, color = Navy)
            if (enabled) Image(painterResource(R.drawable.ic_tracker_chevron), "Pilih beasiswa", Modifier.size(20.dp))
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }, containerColor = SurfaceCard) {
            options.forEach { s ->
                DropdownMenuItem(
                    text = { Text(s.name, style = BodySemi, color = Navy) },
                    onClick = {
                        open = false
                        onSelect(s.id)
                    },
                )
            }
        }
    }
}

/** Detail Langkah (Figma `389:9907`, `389:9962`, `389:10017`): ringkasan kegiatan dan langkah bernomor. */
@Composable
fun TrackerGuideScreen(tracked: TrackedStage, task: TrackerTask, onBack: () -> Unit) {
    val stage = tracked.window.stage
    Scaffold(containerColor = Base, topBar = { ScholarHubDetailTopBar(title = "Detail Langkah", onBack = onBack) }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Column(
                Modifier.fillMaxWidth().cardSurface(shape = Shape12).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(tracked.scholarship.name, style = Caption, color = Link)
                Text(task.nextLabel, style = nunitoSans(23f, 38.4f, FontWeight.Normal), color = Navy)
                Text("Tahap ${stage.title} · ${tracked.window.dateRange()}", style = Caption, color = BodyGray)
            }
            Column(
                Modifier.fillMaxWidth().cardSurface(shape = Shape12).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text("Apa yang perlu dilakukan?", style = MaterialTheme.typography.titleMedium, color = Navy)
                task.guide.forEachIndexed { index, step ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(SkySoft), contentAlignment = Alignment.Center) {
                            Text("${index + 1}", style = BodySemi, color = Link)
                        }
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(step.title, style = TitleSmall, color = Navy)
                            Text(step.body, style = nunitoSans(13f, 21f, FontWeight.Normal), color = BodyGray)
                        }
                    }
                }
            }
            Text(stage.footnote, style = Caption, color = BodyGray)
            PrimaryButton(text = "Kembali ke Tracker", onClick = onBack, modifier = Modifier.fillMaxWidth().height(48.dp), solidShadow = null)
        }
    }
}
