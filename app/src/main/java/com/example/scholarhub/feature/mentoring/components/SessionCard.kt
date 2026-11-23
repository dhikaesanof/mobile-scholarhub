package com.example.scholarhub.feature.mentoring.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.feature.mentoring.SessionPerson
import com.example.scholarhub.feature.mentoring.SessionPhase
import com.example.scholarhub.ui.components.MentorAvatar
import com.example.scholarhub.ui.components.cardSurface
import com.example.scholarhub.ui.theme.ErrorContainer
import com.example.scholarhub.ui.theme.ErrorRed
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.Ocean
import com.example.scholarhub.ui.theme.PillShape
import com.example.scholarhub.ui.theme.ScholarHubRadius
import com.example.scholarhub.ui.theme.ScholarHubTheme
import com.example.scholarhub.ui.theme.Sky
import com.example.scholarhub.ui.theme.SkySoft
import com.example.scholarhub.ui.theme.Steel
import com.example.scholarhub.ui.theme.TextVariant
import com.example.scholarhub.ui.theme.nunitoSans

private val ButtonShape12 = RoundedCornerShape(ScholarHubRadius.md)
private val NameStyle = nunitoSans(18.554f, 24.738f, FontWeight.Bold)
internal val SuccessSoft = Color(0xFFD7F5E5)
internal val SuccessInk = Color(0xFF065F46)
private val WarningSoft = Color(0xFFFDF3CB)
private val WarningInk = Color(0xFFB45309)

/**
 * Kartu sesi mentoring (Figma `Card Mentoring Session (Mentee)` `239:13935` dan `309:18137`).
 * - Sesi mendatang: badge di kanan nama (Berlangsung / hitung mundur / Terjadwal) dan tombol "Masuk Room"
 *   yang aktif hanya saat sesi berlangsung.
 * - Riwayat: label status di atas (Menunggu Konfirmasi / Selesai / Dalam Peninjauan) dengan aksi sesuai status.
 * - [phase] null: hanya ringkasan sesi tanpa badge dan aksi (kepala form Pelaporan).
 */
@Composable
fun SessionCard(
    person: SessionPerson,
    topic: String,
    date: String,
    time: String,
    phase: SessionPhase?,
    modifier: Modifier = Modifier,
    countdown: String? = null,
    onClick: (() -> Unit)? = null,
    onJoin: () -> Unit = {},
    onReport: () -> Unit = {},
    onConfirm: () -> Unit = {},
    onContactAdmin: () -> Unit = {},
    /** Riwayat versi mentor (Figma `364:7709`): catatan penghasilan menggantikan tombol konfirmasi milik mentee. */
    mentorNote: String? = null,
) {
    val history = phase != null && !phase.isUpcomingPhase()
    Column(
        modifier.fillMaxWidth().cardSurface()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = if (history) 16.dp else 16.632.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(if (history) 16.dp else 8.dp),
    ) {
        if (history) {
            when (phase) {
                SessionPhase.AwaitingConfirmation -> StatusPill("Menunggu Konfirmasi", WarningSoft, WarningInk)
                SessionPhase.InReview -> StatusPill("Dalam Peninjauan", ErrorContainer, ErrorRed)
                else -> StatusPill("Selesai", SuccessSoft, SuccessInk)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            MentorAvatar(photo = person.photo, contentDescription = "Foto ${person.name}")
            Column(Modifier.weight(1f).padding(top = 3.118.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(person.name, Modifier.weight(1f), style = NameStyle, color = Navy, maxLines = 1)
                    when (phase) {
                        SessionPhase.Ongoing -> StatusPill("Berlangsung", SuccessSoft, SuccessInk)
                        SessionPhase.StartingSoon -> StatusPill(countdown.orEmpty(), ErrorContainer, ErrorRed)
                        SessionPhase.Scheduled -> StatusPill("Terjadwal", Sky, Ocean)
                        else -> Unit
                    }
                }
                Column {
                    Text("Topik Bimbingan:", style = MaterialTheme.typography.labelSmall, color = TextVariant)
                    Text(topic, style = MaterialTheme.typography.labelLarge, color = Navy)
                }
                SchedulePill(date, time)
            }
        }
        if (history && mentorNote != null) {
            Text(mentorNote, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF6F7582))
            if (phase == SessionPhase.InReview) SoftButton("Hubungi Admin", onContactAdmin, Modifier.fillMaxWidth())
            return@Column
        }
        when (phase) {
            SessionPhase.Ongoing, SessionPhase.StartingSoon, SessionPhase.Scheduled ->
                JoinButton(enabled = phase == SessionPhase.Ongoing, onClick = onJoin, modifier = Modifier.padding(top = 6.dp))
            SessionPhase.AwaitingConfirmation -> Row(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SoftButton("Laporkan Kendala", onReport, Modifier.weight(1f))
                SoftButton("Konfirmasi Sesi", onConfirm, Modifier.weight(1f), container = Navy, content = SkySoft)
            }
            SessionPhase.InReview -> Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                SoftButton("Hubungi Admin", onContactAdmin, Modifier.fillMaxWidth().padding(top = 6.dp))
                Text("*Laporanmu sedang ditinjau.", style = nunitoSans(11f, 16.5f, FontWeight.Normal), color = Steel)
            }
            SessionPhase.Completed, null -> Unit
        }
    }
}

private fun SessionPhase.isUpcomingPhase() =
    this == SessionPhase.Ongoing || this == SessionPhase.StartingSoon || this == SessionPhase.Scheduled

/** Badge status (Figma `Cuntdown` / `Verifikasi`): pill, padding 8.246 × 2.062, SemiBold 11. */
@Composable
fun StatusPill(text: String, background: Color, content: Color, modifier: Modifier = Modifier) {
    Text(
        text,
        modifier.clip(PillShape).background(background).padding(horizontal = 8.246.dp, vertical = 2.062.dp),
        style = MaterialTheme.typography.labelSmall,
        color = content,
        maxLines = 1,
    )
}

/** Pill tanggal + jam (Figma `Frame 94`): border #006398, ikon 14, teks SemiBold 11 #444650. */
@Composable
fun SchedulePill(date: String, time: String, modifier: Modifier = Modifier) {
    Row(
        modifier.border(1.dp, Ocean, RoundedCornerShape(99.dp)).padding(horizontal = 4.dp, vertical = 1.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        listOf(R.drawable.ic_session_calendar to date, R.drawable.ic_session_clock to time).forEach { (icon, text) ->
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Image(painterResource(icon), null, Modifier.size(14.dp))
                Text(text, style = MaterialTheme.typography.labelSmall, color = TextVariant, maxLines = 1)
            }
        }
    }
}

/** "Masuk Room": navy saat sesi berlangsung, navy 50% dan nonaktif di luar jadwal. */
@Composable
fun JoinButton(enabled: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth().clip(ButtonShape12).background(if (enabled) Navy else Navy.copy(alpha = 0.5f))
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick).padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(painterResource(R.drawable.ic_home_camera), null, Modifier.size(15.254.dp, 12.75.dp), colorFilter = ColorFilter.tint(SkySoft))
        Text("Masuk Room", style = MaterialTheme.typography.labelSmall, color = SkySoft)
    }
}

@Composable
private fun SoftButton(label: String, onClick: () -> Unit, modifier: Modifier, container: Color = Sky, content: Color = Navy) {
    Text(
        label,
        modifier.clip(ButtonShape12).background(container).clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        style = MaterialTheme.typography.labelSmall.copy(textAlign = androidx.compose.ui.text.style.TextAlign.Center),
        color = content,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF1F1F1, widthDp = 402)
@Composable
private fun SessionCardPreview() {
    val person = SessionPerson("samantha", "Samantha Dira", "Awardee BU", R.drawable.mentor_avatar)
    ScholarHubTheme {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SessionPhase.entries.forEach { phase ->
                SessionCard(person, "Review Esai Beasiswa", "Kamis, 22 Okt 2026", "16:00 - 17:00 WIB", phase, countdown = "23 : 45 : 20")
            }
        }
    }
}
