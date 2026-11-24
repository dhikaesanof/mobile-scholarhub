package com.example.scholarhub.feature.tracker

import com.example.scholarhub.domain.scholarship.Scholarship
import com.example.scholarhub.domain.scholarship.TimelineStep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

/** Satu langkah di halaman Detail Langkah. */
data class GuideStep(val title: String, val body: String)

/** Kegiatan checklist; [nextLabel] tampil di "Langkah berikutnya" dan sebagai judul Detail Langkah. */
data class TrackerTask(val id: String, val label: String, val nextLabel: String, val guide: List<GuideStep>)

/**
 * Tahap yang dipantau tracker (Figma `130:9862`). [stepLabels] mencocokkan label `selectionSchedule.steps` di katalog.
 * shortcut: checklist dan panduan sama untuk semua beasiswa; ambil dari katalog bila skema menyediakan checklist per beasiswa.
 */
enum class TrackerStage(
    val title: String,
    val stepLabels: List<String>,
    val unit: String,
    val description: String?,
    val footnote: String,
    val tasks: List<TrackerTask>,
) {
    Registration(
        "Pendaftaran", listOf("Mulai Pendaftaran", "Batas Pendaftaran"), "kegiatan", null,
        "Setelah kegiatan benar-benar selesai, tandai pada checklist tahap ini.",
        listOf(
            TrackerTask(
                "reg-docs", "Menyiapkan berkas persyaratan", "Siapkan berkas persyaratan",
                listOf(
                    GuideStep("Baca daftar persyaratan", "Buka tab Persyaratan di detail beasiswa dan catat semua dokumen wajib."),
                    GuideStep("Kumpulkan dokumen", "Siapkan KTM, transkrip, dan dokumen lain yang diminta. Urus surat yang perlu waktu lebih dulu."),
                    GuideStep("Pindai dengan jelas", "Pindai berkas agar terbaca, lalu sesuaikan format dan batas ukuran file."),
                    GuideStep("Simpan dalam satu folder", "Beri nama file yang jelas agar mudah diunggah saat mengisi formulir."),
                ),
            ),
            TrackerTask(
                "reg-form", "Mengisi formulir pendaftaran", "Isi formulir pendaftaran",
                listOf(
                    GuideStep("Buka portal pendaftaran", "Gunakan tautan pendaftaran resmi dari detail beasiswa."),
                    GuideStep("Isi data diri", "Pastikan nama, kampus, dan program studi sama dengan dokumen pendukung."),
                    GuideStep("Lengkapi isian esai", "Tulis jawaban esai atau motivasi sesuai batas kata yang diminta."),
                    GuideStep("Simpan draf", "Simpan isian secara berkala agar tidak hilang sebelum dikirim."),
                ),
            ),
            TrackerTask(
                "reg-submit", "Mengirim formulir dan berkas", "Periksa dan kirim pendaftaran",
                listOf(
                    GuideStep("Periksa seluruh isian", "Cocokkan identitas, kampus, program studi, dan informasi lain pada formulir."),
                    GuideStep("Cek kelengkapan berkas", "Pastikan semua dokumen wajib sudah terlampir, terbaca, dan sesuai format yang diminta."),
                    GuideStep("Kirim melalui portal pendaftaran", "Kirim pendaftaran sebelum tenggat. Periksa kembali sebelum menekan tombol kirim."),
                    GuideStep("Simpan bukti pendaftaran", "Unduh atau simpan bukti pengiriman. Setelah berhasil, tandai kegiatan mengirim pendaftaran sebagai selesai."),
                ),
            ),
        ),
    ),
    Administration(
        "Seleksi Administrasi", listOf("Seleksi Administrasi"), "dokumen",
        "Checklist kelengkapan berkas. Lengkapi perbaikan pada tahap ini hanya jika diminta penyelenggara.",
        "Berkas awal dikirim sebelum tenggat pendaftaran. Pada seleksi administrasi, gunakan checklist untuk memastikan kelengkapan dan menindaklanjuti perbaikan bila diminta.",
        listOf(
            documentTask("adm-ktm", "KTM / Surat Aktif Kuliah", "surat aktif kuliah dari bagian akademik atau KTM yang masih berlaku"),
            documentTask("adm-transcript", "Transkrip Nilai", "transkrip terbaru yang memuat IPK dan sudah dilegalisasi bila diminta"),
            TrackerTask(
                "adm-income", "SKTM / Bukti Penghasilan", "Lengkapi SKTM / Bukti Penghasilan",
                listOf(
                    GuideStep("Pastikan jenis bukti yang diterima", "Periksa ketentuan beasiswa: apakah menggunakan SKTM, slip penghasilan, atau bukti lain yang diterima penyelenggara."),
                    GuideStep("Siapkan dokumen pendukung", "Siapkan bukti penghasilan atau urus surat keterangan sesuai jenis dokumen yang diminta program."),
                    GuideStep("Periksa dan pindai dokumen", "Pastikan identitas, isi keterangan, dan periode dokumen sesuai. Pindai berkas dengan jelas serta ikuti batas ukuran dan format yang diminta."),
                    GuideStep("Simpan dan tindak lanjuti perbaikan", "Simpan berkas agar mudah ditemukan. Jika penyelenggara meminta perbaikan, kirim melalui kanal yang ditentukan sebelum batas perbaikannya."),
                ),
            ),
            documentTask("adm-award", "Sertifikat Prestasi", "sertifikat lomba, organisasi, atau prestasi lain yang relevan"),
        ),
    ),
    Interview(
        "Wawancara", listOf("Tes / Penilaian", "Wawancara"), "kegiatan", null,
        "Setelah kegiatan benar-benar selesai, tandai pada checklist tahap ini.",
        listOf(
            TrackerTask(
                "int-schedule", "Mencatat jadwal dan akses wawancara", "Catat jadwal dan akses wawancara",
                listOf(
                    GuideStep("Cek pengumuman", "Baca email atau portal penyelenggara untuk jadwal wawancara."),
                    GuideStep("Catat waktu dan zona waktu", "Simpan tanggal, jam, dan zona waktu di kalender."),
                    GuideStep("Simpan tautan atau lokasi", "Catat tautan ruang daring atau alamat lokasi wawancara."),
                    GuideStep("Konfirmasi kehadiran", "Balas undangan bila penyelenggara meminta konfirmasi."),
                ),
            ),
            TrackerTask(
                "int-prepare", "Menyiapkan materi dan latihan", "Siapkan materi dan latihan wawancara",
                listOf(
                    GuideStep("Pelajari kembali pendaftaranmu", "Baca esai, pengalaman, serta rencana studi atau kontribusi yang sudah kamu kirim."),
                    GuideStep("Susun poin jawaban", "Siapkan perkenalan, alasan mendaftar, pengalaman relevan, dan tujuan setelah menerima beasiswa."),
                    GuideStep("Latihan menjawab", "Berlatih menjelaskan jawaban dengan singkat dan jelas. Kamu dapat meminta masukan dari teman atau mentor."),
                    GuideStep("Periksa kebutuhan sesi", "Pastikan jadwal, lokasi atau tautan, perangkat, dan dokumen pendukung sudah siap."),
                ),
            ),
            TrackerTask(
                "int-attend", "Mengikuti wawancara", "Ikuti wawancara",
                listOf(
                    GuideStep("Datang lebih awal", "Masuk ruang daring atau tiba di lokasi 10–15 menit sebelum jadwal."),
                    GuideStep("Siapkan dokumen", "Letakkan dokumen pendukung yang mungkin ditanyakan di dekatmu."),
                    GuideStep("Jawab dengan jujur", "Jawab sesuai pengalamanmu dan beri contoh konkret."),
                    GuideStep("Catat hasil sesi", "Catat pertanyaan yang muncul dan tunggu pengumuman dari penyelenggara."),
                ),
            ),
        ),
    ),
}

private fun documentTask(id: String, label: String, what: String) = TrackerTask(
    id, label, "Lengkapi $label",
    listOf(
        GuideStep("Pastikan dokumen yang diminta", "Periksa ketentuan beasiswa untuk $what."),
        GuideStep("Siapkan dokumen", "Minta atau cetak dokumen dari pihak yang berwenang bila belum punya."),
        GuideStep("Periksa dan pindai", "Pastikan identitas dan masa berlaku sesuai, lalu pindai sesuai format dan batas ukuran."),
        GuideStep("Simpan dan tindak lanjuti", "Simpan berkas dan kirim perbaikan melalui kanal resmi bila penyelenggara memintanya."),
    ),
)

/** Rentang tanggal satu tahap: dari langkah pertama yang cocok sampai akhir langkah terakhir. */
data class StageWindow(val stage: TrackerStage, val start: LocalDate, val end: LocalDate)

fun stageWindows(steps: List<TimelineStep>): List<StageWindow> = TrackerStage.entries.mapNotNull { stage ->
    val matched = steps.filter { it.label in stage.stepLabels }
    if (matched.isEmpty()) null else StageWindow(stage, matched.first().start, matched.maxOf { it.end ?: it.start })
}

/**
 * Tahap yang sedang dikerjakan: tahap pertama yang belum lewat pada [today]. Jeda antartahap masuk ke tahap berikutnya
 * (persiapan); setelah semua tahap lewat, tahap terakhir tetap ditampilkan.
 */
fun currentStage(windows: List<StageWindow>, today: LocalDate): StageWindow? =
    windows.firstOrNull { !today.isAfter(it.end) } ?: windows.lastOrNull()

private val Month = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("id-ID"))
private val DayMonth = DateTimeFormatter.ofPattern("d MMMM", Locale.forLanguageTag("id-ID"))

/** "1–30 November 2026" atau "28 November – 2 Desember 2026". */
fun StageWindow.dateRange(): String =
    if (start.month == end.month && start.year == end.year) "${start.dayOfMonth}–${end.dayOfMonth} ${end.format(Month)}"
    else "${start.format(DayMonth)} – ${end.dayOfMonth} ${end.format(Month)}"

/** Keterangan di samping tanggal (Figma "7 hari menuju penutupan", "Lengkapi dokumen administrasi", "Siapkan wawancaramu"). */
fun StageWindow.status(today: LocalDate): String {
    val toStart = ChronoUnit.DAYS.between(today, start)
    val toEnd = ChronoUnit.DAYS.between(today, end)
    return when {
        toEnd < 0 -> "Tahap telah berakhir"
        toStart > 0 -> "Dimulai $toStart hari lagi"
        stage == TrackerStage.Registration -> if (toEnd == 0L) "Ditutup hari ini" else "$toEnd hari menuju penutupan"
        stage == TrackerStage.Administration -> "Lengkapi dokumen administrasi"
        else -> "Siapkan wawancaramu"
    }
}

fun Scholarship.scheduleSteps(): List<TimelineStep> =
    guide.sections.firstOrNull { it.key == "selectionSchedule" }?.steps.orEmpty()

/** Beasiswa yang dipantau dan checklist yang sudah ditandai, di memori untuk satu sesi masuk. */
class TrackerStore {
    private val selected = MutableStateFlow<String?>(null)
    val selectedId: StateFlow<String?> = selected.asStateFlow()

    private val doneState = MutableStateFlow(emptySet<String>())
    val done: StateFlow<Set<String>> = doneState.asStateFlow()

    fun select(scholarshipId: String) {
        selected.value = scholarshipId
    }

    fun toggle(scholarshipId: String, taskId: String) {
        val key = key(scholarshipId, taskId)
        doneState.update { if (key in it) it - key else it + key }
    }

    companion object {
        fun key(scholarshipId: String, taskId: String) = "$scholarshipId/$taskId"
    }
}
