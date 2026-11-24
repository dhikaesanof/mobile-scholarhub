package com.example.scholarhub.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.ui.theme.SkySoft

/**
 * Avatar mentor kecil di kartu sesi. Di Figma, wadah elips 44.75 × 46.75 memotong foto
 * 50 dp yang diskalakan setinggi 179% dan digeser ke atas 39.5%, sehingga yang terlihat wajahnya.
 */
@Composable
fun MentorAvatar(
    modifier: Modifier = Modifier,
    @DrawableRes photo: Int = R.drawable.mentor_avatar,
    contentDescription: String? = "Foto mentor",
) {
    Box(
        modifier = modifier
            .size(width = 44.75.dp, height = 46.75.dp)
            .clip(RoundedCornerShape(50))
            .background(SkySoft),
    ) {
        // requiredSize yang lebih besar dari wadah diletakkan di tengah; offset x 2.625 mengembalikan tepi kiri ke 0
        // sesuai Figma, dan pusat vertikal (−22.3 dp) sudah sama dengan posisi Figma.
        Image(
            painter = painterResource(photo),
            contentDescription = contentDescription,
            modifier = Modifier.requiredSize(width = 50.dp, height = 91.3.dp).offset(x = 2.625.dp),
            contentScale = ContentScale.FillBounds,
        )
    }
}
