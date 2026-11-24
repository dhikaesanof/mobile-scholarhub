package com.example.scholarhub.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.ui.theme.NavyStrong
import com.example.scholarhub.ui.theme.OutlineCool
import com.example.scholarhub.ui.theme.OutlineMist
import com.example.scholarhub.ui.theme.OutlineSky
import com.example.scholarhub.ui.theme.PillShape
import com.example.scholarhub.ui.theme.ScholarHubTheme
import com.example.scholarhub.ui.theme.ShadowSubtle
import com.example.scholarhub.ui.theme.TextVariant
import com.example.scholarhub.ui.theme.InterSemiBold
import com.example.scholarhub.ui.theme.nunitoSans

/** Penanda akhir daftar: garis pendek + ikon centang + teks abu 70% (Figma `End of Feed Signature`). */
@Composable
fun EndOfFeed(text: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(top = 15.183.dp, bottom = 22.775.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = 11.388.dp)
                .size(width = 30.367.dp, height = 3.796.dp)
                .clip(PillShape)
                .background(OutlineMist),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(3.796.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.ic_end_of_feed),
                contentDescription = null,
                modifier = Modifier.size(11.667.dp),
            )
            Text(
                text = text,
                style = nunitoSans(11.388f, 15.183f, FontWeight.Normal),
                color = TextVariant.copy(alpha = 0.7f),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF1F1F1, widthDp = 402)
@Composable
private fun FeedComponentsPreview() {
    ScholarHubTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            EndOfFeed("Semua riwayat pembayaran telah dimuat")
        }
    }
}
