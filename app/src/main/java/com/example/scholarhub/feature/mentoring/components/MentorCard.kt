package com.example.scholarhub.feature.mentoring.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scholarhub.R
import com.example.scholarhub.ui.components.CroppedImage
import com.example.scholarhub.ui.components.FigmaCrop
import com.example.scholarhub.ui.components.PrimaryButton
import com.example.scholarhub.ui.components.cardSurface
import com.example.scholarhub.ui.theme.InkBlue
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.Ocean
import com.example.scholarhub.ui.theme.OutlineMist
import com.example.scholarhub.ui.theme.PillShape
import com.example.scholarhub.ui.theme.ScholarHubRadius
import com.example.scholarhub.ui.theme.ScholarHubTheme
import com.example.scholarhub.ui.theme.Sky
import com.example.scholarhub.ui.theme.TextVariant

@Immutable
data class MentorUi(
    val id: String,
    val name: String,
    val affiliation: String,
    val topics: List<String>,
    val price: String,
    @DrawableRes val photo: Int,
    /** Crop foto dari Figma; tiap foto punya crop sendiri agar wajah terlihat di kotak 80 dp. */
    val photoCrop: FigmaCrop,
)

/**
 * Kartu mentor (Figma `Mentor Card`). Ukuran memakai nilai Figma apa adanya (foto 82.26, padding 16.45,
 * gap 14.4). Nilai ini tidak dibulatkan ke grid karena font tidak ikut ter-scale; membulatkan padding
 * mengubah lebar area teks dan membuat afiliasi panjang tidak terpecah seperti di Figma.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MentorCard(
    mentor: MentorUi,
    onBookingClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .cardSurface()
            .padding(16.45.dp),
        verticalArrangement = Arrangement.spacedBy(14.4.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            CroppedImage(
                image = mentor.photo,
                width = 82.26.dp,
                height = 82.26.dp,
                crop = mentor.photoCrop,
                contentDescription = "Foto ${mentor.name}",
                modifier = Modifier.clip(RoundedCornerShape(ScholarHubRadius.md)),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mentor.name,
                    modifier = Modifier.padding(top = 4.11.dp),
                    style = MaterialTheme.typography.titleSmall,
                    color = Navy,
                )
                Text(text = mentor.affiliation, style = MaterialTheme.typography.bodyMedium, color = Ocean)
                FlowRow(
                    modifier = Modifier.padding(top = 6.17.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.11.dp),
                    verticalArrangement = Arrangement.spacedBy(4.11.dp),
                ) {
                    mentor.topics.forEach { topic ->
                        Text(
                            text = topic,
                            modifier = Modifier
                                .clip(PillShape)
                                .background(Sky)
                                .padding(horizontal = 8.23.dp, vertical = 2.06.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = InkBlue,
                        )
                    }
                }
            }
        }

        Column {
            HorizontalDivider(color = OutlineMist, thickness = 1.03.dp)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 11.31.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    // Figma: Inter Medium 10.28 / 15.42; font dipetakan ke Nunito Sans, ukuran tetap.
                    Text(
                        text = "Tarif Bimbingan",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.28.sp,
                            lineHeight = 15.42.sp,
                        ),
                        color = TextVariant,
                    )
                    Text(
                        text = mentor.price,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Navy,
                    )
                }
                PrimaryButton(
                    text = "Info & Booking",
                    onClick = onBookingClick,
                    solidShadow = null,
                    contentPadding = PaddingValues(horizontal = 14.4.dp, vertical = 6.17.dp),
                )
            }
        }
    }
}

internal val SampleMentors = listOf(
    MentorUi(
        id = "samantha-dira",
        name = "Samantha Dira",
        affiliation = "Awardee BU - Universitas Brawijaya",
        topics = listOf("Essay Polish", "Interview"),
        price = "Rp 50.000",
        photo = R.drawable.mentor_samantha,
        photoCrop = FigmaCrop(scaleX = 1f, scaleY = 1.7778f, offsetX = -0.0023f, offsetY = -0.2733f),
    ),
    MentorUi(
        id = "sarah-aurelia",
        name = "Sarah Aurelia, S.Kom",
        affiliation = "Awardee Tanoto - Universitas Indonesia",
        topics = listOf("Essay Polish", "Interview"),
        price = "Rp 50.000",
        photo = R.drawable.mentor_sarah,
        photoCrop = FigmaCrop(scaleX = 1.0027f, scaleY = 1.7853f, offsetX = -0.0014f, offsetY = -0.3933f),
    ),
    MentorUi(
        id = "ahmad-ibra",
        name = "Ahmad Ibra, S.Sos",
        affiliation = "Awardee Pertamina - ITB",
        topics = listOf("Essay Polish", "Interview"),
        price = "Rp 50.000",
        photo = R.drawable.mentor_ahmad,
        photoCrop = FigmaCrop(scaleX = 1.0027f, scaleY = 1.7853f, offsetX = -0.0014f, offsetY = -0.3933f),
    ),
)

@Preview(showBackground = true, backgroundColor = 0xFFF1F1F1, widthDp = 401)
@Composable
private fun MentorCardPreview() {
    ScholarHubTheme {
        MentorCard(mentor = SampleMentors[1], onBookingClick = {}, modifier = Modifier.padding(16.dp))
    }
}
