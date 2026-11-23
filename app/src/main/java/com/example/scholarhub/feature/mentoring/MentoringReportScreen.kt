package com.example.scholarhub.feature.mentoring

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.ui.components.OutlineButton
import com.example.scholarhub.ui.components.PrimaryButton
import com.example.scholarhub.ui.components.ScholarHubDetailTopBar
import com.example.scholarhub.ui.components.cardSurface
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.ScholarHubRadius
import com.example.scholarhub.ui.theme.TextMuted
import com.example.scholarhub.ui.theme.nunitoSans
import kotlinx.coroutines.launch

private val FieldBg = Color(0xFFEDF6FC)
private val FieldInk = Color(0xFF00194A)
private val DashBorder = Color(0xFFB8DDF4)
private val FieldShape = RoundedCornerShape(ScholarHubRadius.md)
private val Helper = nunitoSans(11f, 16.5f, FontWeight.Normal)

val ReportTypes = listOf("Mentor tidak hadir", "Sesi berakhir lebih awal", "Kendala akses room", "Lainnya")
const val MaxAttachments = 3
private const val MaxAttachmentBytes = 5L * 1024 * 1024
private val AllowedImageTypes = setOf("image/jpeg", "image/png")

/**
 * Pelaporan Mentoring (Figma `319:6589` pilih kendala, `320:6616` terpilih, `319:6632` bukti ditambahkan).
 * Gambar bukti divalidasi (JPG/PNG, maks. 5 MB, maks. 3) dan hanya nama berkasnya yang disimpan; belum ada unggah ke server.
 */
@Composable
fun MentoringReportScreen(
    store: MentoringStore,
    bookingId: String,
    onBack: () -> Unit,
    onSubmitted: () -> Unit,
) {
    val booking = store.find(bookingId) ?: return
    var type by rememberSaveable { mutableStateOf<String?>(null) }
    var story by rememberSaveable { mutableStateOf("") }
    val attachments = remember { mutableStateListOf<String>() }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val problem = validateImage(context, uri)
        if (problem != null) {
            scope.launch { snackbar.showSnackbar(problem) }
        } else if (attachments.size < MaxAttachments) {
            attachments += displayName(context, uri)
        }
    }

    Scaffold(
        containerColor = Base,
        topBar = { ScholarHubDetailTopBar(title = "Pelaporan Mentoring", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Laporkan Kendala", style = MaterialTheme.typography.titleMedium, color = Navy)
                Text(
                    "Ceritakan kendala pada sesi mentoringmu. Selama laporan ditinjau, sesi tidak diselesaikan otomatis dan penghasilan mentor ditahan.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Navy,
                )
            }
            BookingSessionCard(booking, store.now(), showStatus = false)

            Column(
                Modifier.fillMaxWidth().cardSurface().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Field("Jenis kendala", "Pilih kendala yang kamu alami") {
                    var open by remember { mutableStateOf(false) }
                    Box {
                        Row(
                            Modifier.fillMaxWidth().height(48.dp).clip(FieldShape).background(FieldBg)
                                .clickable(role = Role.DropdownList) { open = true }.padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(type ?: "Pilih jenis kendala", Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = FieldInk)
                            Image(painterResource(R.drawable.ic_report_down), null, Modifier.size(20.dp))
                        }
                        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
                            ReportTypes.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, style = MaterialTheme.typography.labelLarge, color = FieldInk) },
                                    onClick = {
                                        type = option
                                        open = false
                                    },
                                )
                            }
                        }
                    }
                }
                Field("Ceritakan kendalamu", "Jelaskan kejadian dengan jelas") {
                    BasicTextField(
                        value = story,
                        onValueChange = { story = it.take(1000) },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = FieldInk),
                        cursorBrush = SolidColor(FieldInk),
                        modifier = Modifier.fillMaxWidth().height(120.dp).clip(FieldShape).background(FieldBg).padding(12.dp),
                        decorationBox = { inner ->
                            if (story.isEmpty()) Text("Contoh: Mentor tidak hadir sampai jadwal sesi berakhir.", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                            inner()
                        },
                    )
                }
                Field("Bukti pendukung (opsional)", "JPG atau PNG · Maks. $MaxAttachments gambar, 5 MB per gambar") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        attachments.forEach { name ->
                            Row(
                                Modifier.fillMaxWidth().clip(FieldShape).background(FieldBg).padding(horizontal = 12.dp, vertical = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Image(painterResource(R.drawable.ic_report_image), null, Modifier.size(20.dp))
                                Text(name, Modifier.weight(1f), style = MaterialTheme.typography.labelLarge, color = FieldInk, maxLines = 1)
                                Image(
                                    painterResource(R.drawable.ic_report_x),
                                    "Hapus $name",
                                    Modifier.size(20.dp).clickable(role = Role.Button) { attachments.remove(name) },
                                )
                            }
                        }
                        if (attachments.size < MaxAttachments) {
                            Row(
                                Modifier.fillMaxWidth().height(84.dp).clip(FieldShape).background(FieldBg)
                                    .drawBehind {
                                        drawRoundRect(
                                            DashBorder,
                                            cornerRadius = CornerRadius(12.dp.toPx()),
                                            style = Stroke(1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f))),
                                        )
                                    }
                                    .clickable(role = Role.Button) {
                                        picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                    },
                                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Image(painterResource(R.drawable.ic_report_plus), null, Modifier.size(20.dp))
                                Text(
                                    if (attachments.isEmpty()) "Tambah gambar" else "Tambah gambar (${attachments.size}/$MaxAttachments)",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = FieldInk,
                                )
                            }
                        }
                    }
                }
            }
            PrimaryButton(
                text = "Kirim Laporan",
                onClick = {
                    val selected = type
                    if (selected == null || story.isBlank()) {
                        scope.launch { snackbar.showSnackbar("Pilih jenis kendala dan ceritakan kendalamu.") }
                    } else {
                        store.report(booking.id, SessionReport(selected, story.trim(), attachments.toList()))
                        onSubmitted()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                solidShadow = null,
            )
            OutlineButton("Batal", onClick = onBack)
        }
    }
}

@Composable
private fun Field(label: String, helper: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = FieldInk)
        content()
        Text(helper, style = Helper, color = FieldInk)
    }
}

/** Pesan kesalahan jika gambar bukan JPG/PNG atau lebih dari 5 MB; null jika valid. */
private fun validateImage(context: Context, uri: Uri): String? {
    val type = context.contentResolver.getType(uri)
    if (type !in AllowedImageTypes) return "Gunakan gambar JPG atau PNG."
    val size = context.contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)?.use { cursor ->
        if (cursor.moveToFirst() && !cursor.isNull(0)) cursor.getLong(0) else null
    }
    return if (size != null && size > MaxAttachmentBytes) "Ukuran gambar maksimal 5 MB." else null
}

private fun displayName(context: Context, uri: Uri): String =
    context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) cursor.getString(0) else null
    } ?: uri.lastPathSegment.orEmpty()
