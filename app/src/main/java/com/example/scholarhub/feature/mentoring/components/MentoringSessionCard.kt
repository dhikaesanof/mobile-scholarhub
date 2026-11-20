package com.example.scholarhub.feature.mentoring.components

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.ui.components.ButtonShape
import com.example.scholarhub.ui.components.MentorAvatar
import com.example.scholarhub.ui.components.cardSurface
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.Ocean
import com.example.scholarhub.ui.theme.Outline
import com.example.scholarhub.ui.theme.PillShape
import com.example.scholarhub.ui.theme.ScholarHubTheme
import com.example.scholarhub.ui.theme.Sky
import com.example.scholarhub.ui.theme.SkySoft
import com.example.scholarhub.ui.theme.TextVariant

@Immutable
data class MentoringSessionUi(
    val schedule: String,
    val title: String,
    val mentorLine: String,
    @DrawableRes val mentorPhoto: Int = R.drawable.mentor_avatar,
)

/**
 * Kartu sesi mentoring (Figma `Kartu Konsultasi Mentor`) untuk Home dan Mentor Page.
 * Nilai pecahan Figma (16.632, 4.158, 2.079) dipakai apa adanya agar lebar teks dan tinggi kartu sama.
 */
@Composable
fun MentoringSessionCard(
    session: MentoringSessionUi,
    modifier: Modifier = Modifier,
    onRescheduleClick: () -> Unit = {},
    onJoinClick: () -> Unit = {},
    /** Teks dan ikon tombol "Masuk Room": #ECF5FB di Home, putih di Mentor Page. */
    joinContentColor: Color = SkySoft,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .cardSurface()
            .padding(16.632.dp),
        verticalArrangement = Arrangement.spacedBy(16.632.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            MentorAvatar(photo = session.mentorPhoto)
            Column(modifier = Modifier.padding(top = 3.118.dp), verticalArrangement = Arrangement.spacedBy(2.079.dp)) {
                Row(
                    modifier = Modifier
                        .border(1.dp, Ocean, PillShape)
                        .padding(horizontal = 8.316.dp, vertical = 2.079.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.158.dp),
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_home_session_clock),
                        contentDescription = null,
                        modifier = Modifier.size(width = 10.17.dp, height = 10.63.dp),
                    )
                    Text(text = session.schedule, style = MaterialTheme.typography.labelSmall, color = Ocean)
                }
                Text(
                    text = session.title,
                    modifier = Modifier.padding(top = 2.079.dp),
                    style = MaterialTheme.typography.titleSmall,
                    color = Navy,
                )
                Text(text = session.mentorLine, style = MaterialTheme.typography.bodyMedium, color = TextVariant)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top,
        ) {
            SessionButton(
                modifier = Modifier
                    .weight(1f)
                    .clip(ButtonShape)
                    .background(Sky)
                    .border(1.dp, Outline, ButtonShape),
                verticalPadding = 9.dp,
                icon = R.drawable.ic_home_reschedule,
                iconWidth = 14.dp,
                iconHeight = 14.dp,
                label = "Reschedule",
                labelColor = Ocean,
                onClick = onRescheduleClick,
            )
            SessionButton(
                modifier = Modifier.width(160.dp).clip(ButtonShape).background(Navy),
                verticalPadding = 8.dp,
                icon = R.drawable.ic_home_camera,
                iconWidth = 15.25.dp,
                iconHeight = 12.75.dp,
                label = "Masuk Room",
                labelColor = joinContentColor,
                iconTint = joinContentColor,
                onClick = onJoinClick,
            )
        }
    }
}

@Composable
private fun SessionButton(
    modifier: Modifier,
    verticalPadding: Dp,
    @DrawableRes icon: Int,
    iconWidth: Dp,
    iconHeight: Dp,
    label: String,
    labelColor: Color,
    onClick: () -> Unit,
    iconTint: Color? = null,
) {
    // Figma: gap 4 + margin kiri teks 4 = 8 dp antara ikon dan label.
    Row(
        modifier = modifier.clickable(onClick = onClick).padding(vertical = verticalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(iconWidth, iconHeight),
            colorFilter = iconTint?.let { ColorFilter.tint(it) },
        )
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = labelColor)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF1F1F1, widthDp = 392)
@Composable
private fun MentoringSessionCardPreview() {
    ScholarHubTheme {
        MentoringSessionCard(
            session = MentoringSessionUi("Selasa, 20 Okt • 19:30 WIB", "Review Esai LPDP", "Bersama Kak Samantha Dira"),
            modifier = Modifier.padding(16.dp),
        )
    }
}
