package com.example.scholarhub.feature.mentoring.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.scholarhub.ui.components.cardSurface
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.Ocean
import com.example.scholarhub.ui.theme.OutlineSky
import com.example.scholarhub.ui.theme.ScholarHubRadius
import com.example.scholarhub.ui.theme.ScholarHubTheme

@Immutable
data class MentorProfileUi(
    val id: String,
    val name: String,
    val affiliation: String,
    val bio: String,
    val profile: List<Pair<String, String>>,
    val contact: List<Pair<String, String>>,
    @DrawableRes val photo: Int,
    val photoCrop: FigmaCrop,
)

private val ProfileCardShape = RoundedCornerShape(ScholarHubRadius.md)

/** Kartu profil di Detail Mentor (Figma `Mentor Profile Card`): radius 12, border #E6EFF5, padding 17. */
@Composable
fun MentorProfileCard(profile: MentorProfileUi, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .cardSurface(shape = ProfileCardShape)
            .border(1.dp, OutlineSky, ProfileCardShape)
            .padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        CroppedImage(
            image = profile.photo,
            width = 80.dp,
            height = 80.dp,
            crop = profile.photoCrop,
            contentDescription = "Foto ${profile.name}",
            modifier = Modifier.clip(CircleShape),
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = profile.name,
                    modifier = Modifier.padding(top = 2.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = Navy,
                )
                Text(
                    text = profile.affiliation,
                    modifier = Modifier.padding(horizontal = 1.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = Ocean,
                )
            }
            Text(
                text = profile.bio,
                modifier = Modifier.padding(horizontal = 1.dp),
                style = MaterialTheme.typography.labelLarge,
                color = Navy,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            ProfileHeading("Profil Mentor")
            LabelValueRows(profile.profile)
            ProfileHeading("Informasi Kontak", topPadding = 12)
            LabelValueRows(profile.contact)
        }
    }
}

@Composable
private fun ProfileHeading(text: String, topPadding: Int = 2) {
    Text(
        text = text,
        modifier = Modifier.padding(top = topPadding.dp),
        style = MaterialTheme.typography.titleSmall,
        color = Navy,
    )
}

/** Grid 2 kolom Figma: label 120 dp + nilai, gap 3 / 2, tinggi baris minimum 24. */
@Composable
private fun LabelValueRows(rows: List<Pair<String, String>>) {
    Column(modifier = Modifier.padding(top = 4.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        rows.forEach { (label, value) ->
            Row(
                modifier = Modifier.heightIn(min = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = label,
                    modifier = Modifier.width(120.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = Navy,
                )
                // Figma: Inter Regular 12 / 24, dipetakan ke Nunito Sans.
                Text(
                    text = value,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Normal, lineHeight = 24.sp),
                    color = Navy,
                )
            }
        }
    }
}

private val AvatarCrop = FigmaCrop(scaleY = 1.7902f, offsetY = -0.3951f)

/** Profil Detail Mentor (Figma `209:5537`) per ID mentor di [SampleMentors]. Masih contoh statis. */
internal val MentorProfiles = listOf(
    MentorProfileUi(
        id = "samantha-dira",
        name = "Samantha Dira",
        affiliation = "Awardee Beasiswa Unggulan",
        bio = "Alumni Universitas Brawijaya, berpengalaman meloloskan 50+ penerima beasiswa LPDP dan Chevening.",
        profile = listOf(
            "Universitas" to "Universitas Brawijaya",
            "Program Studi" to "Teknik Informatika",
            "Spesialisasi" to "Review Esai, Simulasi Wawancara",
        ),
        contact = listOf("Instagram" to "@samanthadira", "Email" to "samanthadira@gmail.com"),
        photo = R.drawable.mentor_avatar,
        photoCrop = AvatarCrop,
    ),
    MentorProfileUi(
        id = "sarah-aurelia",
        name = "Sarah Aurelia, S.Kom",
        affiliation = "Awardee Tanoto Foundation",
        bio = "Lulusan Ilmu Komputer Universitas Indonesia, mendampingi penyusunan esai dan persiapan wawancara beasiswa.",
        profile = listOf(
            "Universitas" to "Universitas Indonesia",
            "Program Studi" to "Ilmu Komputer",
            "Spesialisasi" to "Review Esai, Simulasi Wawancara",
        ),
        contact = listOf("Instagram" to "@sarahaurelia", "Email" to "sarahaurelia@gmail.com"),
        photo = R.drawable.mentor_sarah,
        photoCrop = FigmaCrop(scaleX = 1.0027f, scaleY = 1.7853f, offsetX = -0.0014f, offsetY = -0.3933f),
    ),
    MentorProfileUi(
        id = "ahmad-ibra",
        name = "Ahmad Ibra, S.Sos",
        affiliation = "Awardee Beasiswa Pertamina",
        bio = "Alumni ITB yang aktif membantu mahasiswa menyiapkan esai motivasi dan rencana studi.",
        profile = listOf(
            "Universitas" to "Institut Teknologi Bandung",
            "Program Studi" to "Studi Pembangunan",
            "Spesialisasi" to "Review Esai, Simulasi Wawancara",
        ),
        contact = listOf("Instagram" to "@ahmadibra", "Email" to "ahmadibra@gmail.com"),
        photo = R.drawable.mentor_ahmad,
        photoCrop = FigmaCrop(scaleX = 1.0027f, scaleY = 1.7853f, offsetX = -0.0014f, offsetY = -0.3933f),
    ),
)

internal val SampleMentorProfile = MentorProfiles.first()

@Preview(showBackground = true, backgroundColor = 0xFFF1F1F1, widthDp = 401)
@Composable
private fun MentorProfileCardPreview() {
    ScholarHubTheme { MentorProfileCard(SampleMentorProfile, Modifier.padding(16.dp)) }
}
