# UI Mentee ScholarHub

Ringkasan 11 frame Figma role **mentee** dari file `ywVjfZKma7n0Ks8mlBGHl4` (ScholarHub). Semua frame berukuran lebar **402 px**. Satuan px Figma dipetakan 1:1 ke `dp` (teks ke `sp`). Token Compose ada di `app/src/main/java/com/example/scholarhub/ui/theme/`.

Dibaca pada 2026-11-20 lewat Figma MCP (`get_metadata`, `get_screenshot`, `get_variable_defs`, `get_design_context`, dan script baca `use_figma`).

## Daftar layar

| # | Nama frame | Node | Tinggi | Header | Bottom nav aktif |
| --- | --- | --- | --- | --- | --- |
| 1 | Home Page | `846:4756` | 1635 | Logo | Home |
| 2 | Mentor Page | `846:4909` | 1145 | Logo | Mentor |
| 3 | Detail Mentor Page | `846:4798` | 1234 | Kembali + "Detail Mentor" | — (sticky CTA) |
| 4 | Beasiswa Page | `846:4855` | 1168 | Logo | Beasiswa |
| 5 | Detail Beasiswa Page | `846:4937` | 3103 | Kembali + "Detail Beasiswa" | Beasiswa |
| 6 | Profil | `846:4522` | 2052 | Kembali + "Profil" | Profil |
| 7 | ScholarMatch | `846:3881` | 2527 | Kembali + "ScholarMatch" | — |
| 8 | Riwayat Notifikasi | `846:4134` | 1222 | Kembali + "Riwayat Notifikasi" | — |
| 9 | Riwayat Transaksi | `846:4272` | 931 | Kembali + "Riwayat Transaksi" | — |
| 10 | Detail Pembayaran | `846:4385` | 931 | Kembali + "Pembayaran" | — |
| 11 | Dokumen | `846:4487` | 1005 | Logo | Premium |

## Isi tiap layar

### 1. Home Page (`846:4756`)
Konten mulai y=106, lebar 360 (margin 21), jarak antarsection 16, judul ke isi 8.
- **Sapaan**: "Selamat Datang, Arza!" (Title Medium) dan "Yuk, lanjutkan persiapan beasiswanya🔥" (Body Medium).
- **Sesi Mentoring Terdekat**: judul dan link "Semua Jadwal" (`#0369A1`), lalu `MentoringSessionCard`.
- **Beasiswa Favorit**: carousel horizontal berisi `ScholarshipCarouselCard` 255×166, gap 16, kartu kedua terpotong di tepi.
- **ScholarMatch**: `MascotBanner` varian bubble (maskot 106×150, bubble `#ECECEC`/border `#E6E6E6` dengan ekor kiri, tombol pill "Test Lagi").
- **Hasil ScholarMatch**: 2 × `ScholarshipCard` (dengan badge "90% Relevan"). Di Figma judulnya typo "Hasil ScholarMacth".

### 2. Mentor Page (`846:4909`)
Konten x=16, lebar 369.
- **Sesi Mentoring Saya**: link "Semua Jadwal" dan `MentoringSessionCard`.
- **Cari Mentor Beasiswa**: subjudul "Pilih mentor awardee untuk bimbingan personal" (`#444650`) dan chip **Filter** (outline, ikon 15×10).
- 3 × **`MentorCard`**: foto 82×82 radius 12, nama Title Small, kampus/afiliasi `#006398` Body Medium, chip topik ("Essay Polish", "Interview") `#D9EEFB`, garis pemisah, "Tarif Bimbingan" + "Rp 50.000" Bold, tombol "Info & Booking" `#001645` radius 12.

### 3. Detail Mentor Page (`846:4798`)
- **`MentorProfileCard`** 369×461: foto 80×80 radius 12, nama Bold 19, afiliasi `#006398`, bio, subjudul "Profile" (Universitas, Jurusan, Spesialisasi) dan "Contact" (Instagram, Email) sebagai baris label/nilai dua kolom.
- **Booking Mentor** (Title Medium).
  - "Pilih Tanggal Sesi" dengan bulan "Oktober 2026" + ikon kalender (`#006398`).
  - **`DayChip`** horizontal 62×74, gap 8: hari (Sen, Sel, …) + tanggal. Aktif `#001645` teks putih, tidak aktif putih.
  - 4 × **`TimeSlotItem`** 369×64, gap 10. State: **Tersedia** (putih, chip "Pilih Jam" `#D9EEFB`), **Terpilih** (`#001645`, teks "Terpilih" putih), **Booked** (abu, chip "Booked", tidak bisa diklik). Komponen Figma: `Button - Jam 1: Tersedia` dengan varian Default/Variant2/Variant3.
- **Sticky bottom bar** 403×110: "Total Biaya Sesi", "Rp 50.000" (Bold 20), ringkasan "Sel, 20 Okt • 11:00 - 11:59 WIB" `#006398`, tombol "Lanjut Bayar" `#001645` 118×36.

### 4. Beasiswa Page (`846:4855`)
Konten x=20, lebar 362.
- **`SearchBar`** 362×50: ikon search kiri, placeholder "Cari beasiswa impianmu, negara, jurusan...", tombol filter/tune kanan.
- **`MascotBanner`** varian info 362×114: maskot 56×80, label "⚓ PANDUAN BEASISWA" `#006398`, judul "Siap Raih Beasiswa Impian?", deskripsi.
- **Eksplor Beasiswa**: ikon filter + judul, info "3 program aktif".
- **`FilterChip`** horizontal: "Semua" (aktif `#001645`), "Luar Negeri" (ikon pesawat), "Dalam Negeri" (ikon pin), dan satu chip lagi di luar layar.
- 2 × `ScholarshipCard` (Kemendikbudristek, LPDP).

### 5. Detail Beasiswa Page (`846:4937`)
Konten x=23, lebar 356, jarak antarkartu 16.
- **Hero card** (`Section - HeroCard`): logo provider, badge "90% Relevan", nama "Beasiswa Cakrawala", penyelenggara, chip status merah "Ditutup", tanggal "Tutup 30 April 2026".
- **Tab** (`Section Detail Beasiswa`): **Ringkasan / Persyaratan / Panduan**. Aktif `#001645` teks putih, sisanya putih.
- **Portal resmi**: kartu biru `#006398`, "PORTAL RESMI PENDAFTARAN" + URL, tombol putih "Buka Tautan" + ikon open-in-new.
- **`DetailSectionCard`** (nama Figma `AboutSection`, padding 21, judul Bold 19, isi mulai y=57):
  - Tentang Program (paragraf)
  - Manfaat: `BenefitItem` latar `#ECF5FB`, judul Bold 16 + nominal. Ikon wallet ada tapi **disembunyikan**, sesuai aturan "kartu teks tanpa ikon".
  - Cakupan Program; Pendidikan dan Akademik (baris label 120 px / nilai)
  - Kondisi Pendaftar; Kemampuan dan Pengalaman; Prioritas Seleksi; Komitmen dan Keberlanjutan (bullet, sebagian masih "xxx")
  - Beasiswa Lain (ikon `error_outline` merah + teks)
  - Dokumen Pendaftaran: `DocumentItem` per dokumen (ikon `insert_drive_file` + teks, latar `#ECF5FB`, tinggi 50, gap 4)
  - Cara Mendaftar; Kontak Bantuan
  - Timeline Pendaftaran: garis vertikal + titik, judul tahap Bold + rentang tanggal
- **`MascotBanner`** varian CTA: "Cek CV & Esai dengan mentor alumni sebelum submit berkas!", tombol "Cari Mentor Esai".

> Frame ini menampilkan semua subbab sekaligus. Menurut `AGENTS.md`, implementasi hanya merender subbab tab aktif dan menyembunyikan subbab kosong.

### 6. Profil (`846:4522`)
Konten x=20, lebar 362.
- **Header profil**: foto 80×80 bulat, "Rayhan Pratama" (Bold 19), "Universitas Indonesia • S1 Teknik Informatika (Sem. 6)", tombol full width "✎ Edit Profil Lengkap" `#001645` tinggi 40.
- **Kualifikasi Akademik**: 3 × **`StatTile`** 118×92 (ikon dalam kotak 28, label kecil, nilai Bold besar + satuan kecil): IPK 3.84 /4.00, IELTS 7.5 Acad., TOEFL 650.
- **Tracker & Reminder**: kartu "PROGRESS ROADMAP" berisi dropdown beasiswa ("Beasiswa Unggulan 2026"), "25 Hari Lagi" merah + "Tutup 30 April 2026", progress bar 66%, 3 × **`ChecklistItem`** (selesai/aktif), dan kartu **Langkah Berikutnya**.
- **Prestasi & Penghargaan**: tombol "+ Tambah" (pill `#D9EEFB`), 3 × **`AchievementCard`** (ikon 40 kotak navy, chip tingkat "Nasional"/"Internasional", tahun, judul Bold, penyelenggara, menu ⋮ 32×32).
- **Akun & Preferensi**: kartu daftar dengan 3 × **`SettingsRow`** tinggi 60 (ikon 36, judul + subjudul, badge angka opsional, chevron): Bookmark (badge 4), Riwayat Bimbingan & Transaksi, Keamanan & Kata Sandi.
- Tombol **"Keluar dari Akun"** (merah muda, teks/ikon merah) 362×49.

### 7. ScholarMatch (`846:3881`)
Satu form panjang, padding horizontal ~14, kartu section padding ~19. Empat **`FormSection`** dengan badge huruf (A–D) dalam lingkaran 21.5 `#001645`:
- **A. Informasi Akademik**: (1) jenjang: grid 2×2 **`OptionButton`**; (2) semester: 4 opsi sebaris; (3) IPK: input + suffix "/ 4.00 Skala"; (4) universitas: input dengan ikon; (5) jurusan: input dengan ikon; (6) tipe PT: 3 opsi.
- **B. Kondisi Ekonomi**: (7) penghasilan orang tua: 4 × **`RadioRow`** (radio di kanan); (8) sedang menerima beasiswa: 2 opsi.
- **C. Kompetensi & Pengalaman**: (9) TOEFL/IELTS: dropdown "Belum Ada Tes" + input skor; (10) prestasi; (11) organisasi; (12) daerah 3T. Semuanya `OptionButton`.
- **D. Preferensi Beasiswa**: (13) manfaat prioritas: 4 × **`CheckboxCard`** (judul + deskripsi). Batas 5 pilihan mengikuti `AGENTS.md`, dan opsinya diambil dari `preferenceOptions`.
- Tombol **"Simpan dan Temukan Beasiswa yang Relevan ✦"** full width, lalu catatan keamanan dengan ikon gembok.
- Setiap pertanyaan punya **badge nomor** 18×18 dan label. `OptionButton` punya state terpilih (border/teks `#006398`, latar `#ECF5FB`, tanda ✓) dan tidak terpilih (putih, border `#E2E8F0`).

### 8. Riwayat Notifikasi (`846:4134`)
- Status: titik biru + "**3 notifikasi** belum dibaca".
- **`FilterChip`** + count: "Semua 3" (aktif), "Deadline Pendaftaran", "Bimbingan Mentor", "Status Berkas", "Info Beasiswa" (ikon masing-masing).
- Grup per waktu (**`GroupHeader`**: judul Bold + badge "2 baru" + tanggal kanan): Hari Ini, Kemarin, Minggu Lalu.
- **`NotificationCard`**: ikon kategori 42 (kotak) atau avatar mentor dengan badge kamera, kategori berwarna, waktu relatif, titik biru jika belum dibaca, judul Bold ~18, isi.
- Penutup feed: garis pendek + "✓ Semua riwayat notifikasi telah dimuat".

### 9. Riwayat Transaksi (`846:4272`)
- **`FilterChip`** + count: Semua 5, Menunggu (titik oranye) 1, Berhasil 4, Dibatalkan.
- **Perlu Diselesaikan** (ikon oranye) + badge "1 Tagihan Aktif" (kuning): **`TransactionCard` (pending)** dengan status "Menunggu Pembayaran" (kuning), timer "Sisa 14:31" (merah), nomor invoice, judul layanan, mentor, total tagihan, tombol "Lanjut Bayar" navy.
- **Oktober 2026** + "2 Transaksi Selesai": **`TransactionCard` (berhasil)** dengan badge hijau "Berhasil", tanggal, kotak "Total Bayar" `#ECF5FB`, tombol sekunder "👁 Detail Sesi".
- Penutup feed: "Semua riwayat pembayaran telah dimuat".

### 10. Detail Pembayaran (`846:4385`)
- **RINGKASAN SESI** (label caps `#006398`): kartu dengan avatar mentor, layanan "Simulasi Wawancara", nama mentor `#006398`, afiliasi, chip jadwal `#ECF5FB` dengan ikon kalender, "Biaya Sesi" dan "Rp 150.000" Bold.
- **METODE PEMBAYARAN** + "⚡ Otomatis Terverifikasi": 3 × **`PaymentMethodCard`** (radio kiri, judul Bold ~18, deskripsi, ikon kanan, chip provider abu): QRIS & E-Wallet (terpilih), Virtual Account Bank, Kartu Kredit / Debit.
- Tombol **"Bayar Sekarang"** navy dengan ikon wallet dan shadow solid bawah.

### 11. Dokumen (`846:4487`)
- **`FilterChip`**: Semua (aktif), Gratis, Premium (Berbayar) dengan ikon badge, Akademik, Eksklusif.
- "Daftar Berkas & Dokumen" + "4 Berkas Tersedia".
- Info box biru muda dengan ikon gembok: "Dokumen Premium Terkunci: …".
- **`DocumentCard`**: ikon dokumen dalam kotak, judul Bold, meta "Awardee Verified • Akses Selamanya", menu ⋮. Dua varian: **premium** (tombol full width "Beli Rp 35.000" `#D9EEFB`) dan **gratis** (chip "✓ GRATIS", tombol "👁 Pratinjau" `#D9EEFB` + "⬇ Unduh" `#001645`).

## Navigasi

Frame Figma **tidak punya link prototype**; tidak ditemukan satu pun `reactions` di 11 frame. Alur di bawah diturunkan dari label tombol, header, dan state bottom nav, jadi perlu dikonfirmasi.

```text
Bottom nav (MenteeBottomBar)
  Premium  → Dokumen
  Mentor   → Mentor Page
  Home     → Home Page
  Beasiswa → Beasiswa Page
  Profil   → Profil

Header logo
  ikon artikel (kiri) → ? (tidak ada frame tujuan)
  lonceng             → Riwayat Notifikasi
  bookmark            → ? (kemungkinan daftar bookmark, fase berikutnya)

Home Page
  "Semua Jadwal"          → Mentor Page (bagian sesi) / daftar jadwal (tidak ada frame)
  Kartu Beasiswa Favorit  → Detail Beasiswa
  "Test Lagi"             → ScholarMatch
  "Lihat Detail Program"  → Detail Beasiswa (dengan skor relevansi)

Mentor Page
  "Info & Booking" → Detail Mentor → "Lanjut Bayar" → Detail Pembayaran

Beasiswa Page
  "Lihat Detail Program" → Detail Beasiswa
  Detail Beasiswa "Cari Mentor Esai" → Mentor Page
  Detail Beasiswa "Buka Tautan"      → browser (registration.applicationUrl)

ScholarMatch
  "Simpan dan Temukan…" → hasil ScholarMatch (Home "Hasil ScholarMatch" atau layar hasil, belum ada frame khusus)

Profil
  Bookmark                       → ? (tidak ada frame)
  Riwayat Bimbingan & Transaksi  → Riwayat Transaksi
  Keluar dari Akun               → Login

Riwayat Transaksi
  "Lanjut Bayar" → Detail Pembayaran
  "Detail Sesi"  → ? (tidak ada frame)
```

Layar dengan header "Kembali" selalu kembali ke layar sebelumnya. Profil memakai header kembali sekaligus bottom nav aktif.

## Komponen yang dipakai berulang

| Komponen | Dipakai di | Catatan |
| --- | --- | --- |
| `AppHeader` (Figma `Header`) | 11 layar | 2 varian. **Logo**: ikon artikel, logo jangkar 28 + "ScholarHub" Bold 19, lonceng dengan dot `#BA1A1A`, bookmark. **Detail**: panah kembali + judul tengah Bold 19. Tinggi bar 64, latar blur + shadow `0 2 13 #000/4%`. |
| `MenteeBottomBar` (Figma `NavMentee`) | Home, Mentor, Beasiswa, Detail Beasiswa, Profil, Dokumen | Sudah ada di `feature/home/HomeScreen.kt`. Tinggi 102, latar `#FBF9F8` 90%, tab aktif berupa lingkaran 40 `#001645` yang naik −12. Label 10 sp tracking 0.6. Komponen Figma punya varian `Home=Default/Mentor/Beasiswa/Profil/Premium`. |
| `ScholarshipCard` | Home, Beasiswa | 360/362 lebar, padding 16, gap 14, radius 16: logo 48, badge relevan, provider, judul, jenjang, chip deadline merah, 3 tag ikon, tombol "Lihat Detail Program" dengan shadow solid `0 2.5 #000C26`. |
| `ScholarshipCarouselCard` | Home | 255×166, logo 48, chip jenjang, judul 2 baris, garis, lokasi + sisa hari. |
| `MentoringSessionCard` (Figma `Kartu Konsultasi Mentor`) | Home, Mentor | Avatar 50 dengan ring `#006398` 40%, chip tanggal outline, judul, mentor, tombol Reschedule (`#D9EEFB`) + Masuk Room (`#001645`). |
| `MentorCard` | Mentor | Lihat layar 2. |
| `MascotBanner` | Home (bubble), Beasiswa (info), Detail Beasiswa (CTA) | Satu komponen dengan 3 varian: maskot + label + judul + deskripsi + tombol opsional. |
| `FilterChip` (+ count/ikon opsional) | Beasiswa, Notifikasi, Transaksi, Dokumen | Pill, aktif `#001645` teks putih, nonaktif putih/`#ECF5FB` dengan border tipis, scroll horizontal. |
| `SectionHeader` | hampir semua | Judul Bold 19 `#001645` + aksi kanan opsional (link, info, tombol "+ Tambah"). |
| `InfoChip` / `Tag` | banyak | Pill kecil 11 sp: tag biru `#D9EEFB`, badge relevan `#ECF5FB`, deadline `#FFE9E6`/`#BA1A1A`, sukses, warning, abu provider. |
| `PrimaryButton` / `SecondaryButton` | banyak | Primary `#001645` (atau `#0F2A66`) radius 12, opsional shadow solid bawah. Secondary `#D9EEFB` teks `#006398`/`#001645`. |
| `ProviderLogo` | ScholarshipCard, Carousel, Hero | Kotak 48, `#ECF5FB`, border `#CCE5FF`, radius 12, gambar 32. |
| `MentorAvatar` | Session card, Notifikasi, Pembayaran | 50 px bulat dengan ring. |
| `DetailSectionCard`, `LabelValueRow` | Detail Beasiswa, Detail Mentor | Kartu putih radius 16, padding 21, baris label 120 / nilai. |
| `OptionButton`, `RadioRow`, `CheckboxCard`, `FormSection` | ScholarMatch | State terpilih/tidak. |
| `EndOfFeed` | Notifikasi, Transaksi | Garis 30×4 + ikon centang + teks abu 12. |
| `SettingsRow` | Profil | Ikon 36 + judul/subjudul + badge + chevron. |

## Design token

Variable resmi di Figma hanya: `Primary #001645`, `Secondary #006398`, `Accents #D9EEFB`, `Base #F1F1F1`, `Labels/Primary #000000`, `Primary/blue-900 #0B1727`, serta text style Title Medium/Small, Body Large/Medium, dan Label Large/Small. Token lain di bawah dihitung dari pemakaian aktual di semua node (frekuensi dalam kurung).

### Warna
| Peran | Hex | Pemakaian |
| --- | --- | --- |
| Primary (navy) | `#001645` | teks judul (104), tombol utama, tab aktif |
| Primary strong | `#0F2A66` | tombol CTA besar (Lanjut Bayar, Bayar Sekarang) |
| Secondary | `#006398` | teks/ikon biru (39), border terpilih, label caps |
| Secondary dark | `#004B73`, `#00476E` | teks tag dan badge relevan |
| Link | `#0369A1` | "Semua Jadwal" |
| Accent | `#D9EEFB` | tag, chip, tombol sekunder |
| Accent soft | `#ECF5FB` | latar logo, badge relevan, item manfaat/dokumen |
| Accent faint | `#F4FAFF` | latar kartu info |
| Accent border | `#CCE5FF` | border logo, info box |
| Background | `#F1F1F1` | latar halaman |
| Surface | `#FFFFFF` | kartu |
| Nav surface | `#FBF9F8` @90% | bottom nav |
| Text strong | `#141D21` | label form, nilai |
| Text variant | `#444650` | teks sekunder (63) |
| Text muted | `#757681` | nav nonaktif, placeholder, ikon |
| Outline | `#E2E8F0` | border kartu/tombol |
| Outline variant | `#F1F5F9`, `#E6EFF5`, `#E0E9EF`, `#DBE4EA` | divider dan border lembut |
| Error | `#BA1A1A` | dot notifikasi, deadline, logout |
| Error container | `#FFE9E6` / `#FFDAD6` @60% | chip deadline, tombol keluar |
| On error container | `#640005` | teks timer |
| Warning | `#F59E0B`, `#D97706` | titik/ikon menunggu |
| Warning container | `#FEF3C7`, `#FFFBEB` | badge menunggu |
| On warning | `#B45309`, `#92400E` | teks menunggu |
| Success | `#047857` | teks "Berhasil" |
| Success container | `#ECFDF5` | badge berhasil |
| Bubble | `#ECECEC` + border `#E6E6E6`, teks `#1B1C1C` | speech bubble maskot |
| Disabled | `#B8B8B8`, hitam @20% | slot "Booked" |
| Slate (frame Inter) | `#0F172A`, `#1E293B`, `#475569`, `#64748B` | teks di Notifikasi/Transaksi/Pembayaran/Dokumen |

### Tipografi
Font utama **Nunito Sans** (`res/font/nunito_sans.ttf`, variable). Line height 1.5×.

| Token Figma | Style | M3 slot |
| --- | --- | --- |
| Title Medium | Bold 19 / 28.5 | `titleMedium` |
| Title Small | Bold 16 / 24 | `titleSmall` |
| Body Large | Regular 16 / 24 | `bodyLarge` |
| Body Medium | SemiBold 13 / 19.5 | `bodyMedium` |
| Label Large | Medium 13 / 19.5 | `labelLarge` |
| Label Small | SemiBold 11 / 16.5 | `labelSmall` |
| (observasi) harga | Bold 20 / 28 | `headlineSmall` |
| (observasi) teks kecil | Regular 12 / 16 | `bodySmall` |
| (observasi) label tombol kecil | Bold 12 / 16.5 | `labelMedium` |
| (observasi) label nav | ExtraBold 10 / 14, tracking 0.6 | `ScholarHubTextStyles.navLabel` |

**Inkonsistensi font.** Frame ScholarMatch, Notifikasi, Transaksi, Pembayaran, Dokumen, dan sebagian Profil/Detail Mentor memakai **Inter** (sekitar 190 segmen teks) dengan ukuran pecahan. Inter tidak ada di project. Token sementara memakai Nunito Sans untuk semua teks. Lihat pertanyaan terbuka.

### Frame yang di-scale
Beberapa frame berisi nilai pecahan yang konsisten dengan hasil scaling grid 4 px, misalnya ScholarMatch ×0.897, Riwayat Notifikasi ×0.949, Riwayat Transaksi/Detail Pembayaran ×1.031, serta instance `Mentor Card` ×1.028 dan `Kartu Konsultasi Mentor` ×1.04.

**Aturan: pakai nilai Figma apa adanya, jangan dibulatkan ke grid.** Pada instance yang di-scale, padding dan ukuran gambar ikut ter-scale tetapi ukuran font sering tidak. Membulatkan padding mengubah lebar area teks, sehingga pemenggalan baris berbeda dari Figma. Contohnya di Mentor Page: dengan padding 16 dan foto 80, "Awardee Tanoto - Universitas Indonesia" muat 1 baris, padahal di Figma 2 baris. Ukuran font pecahan hasil impor Inter (misalnya 10.28) juga dipakai apa adanya.

### Spacing
Grid 4: `2, 4, 6, 8, 12, 16, 20, 24`. Padding kartu 16 (ScholarMatch/Detail Beasiswa ~20), margin layar 16–21, gap antarsection 16, judul ke isi 8.

### Radius
`full` (pill, avatar, chip), `16` (kartu), `12` (tombol, input, logo, foto mentor), `8` (chip kecil, item), `4` (badge kecil), `20` (chip deadline), `28` (avatar 50).

### Shadow
| Nama | Nilai |
| --- | --- |
| card | `0 1 2` hitam 5% (paling sering) |
| cardRaised | `0 4 20 -4` hitam 5% |
| header | `0 2 13` hitam 4% |
| bottomNav | `0 -2 12` hitam 5% |
| buttonSolid | `0 2.5–3 0` `#000C26` / `#001645` / `#08193D` (efek tombol 3D) |
| navActive | `0 4 6 -1` hitam 10% |

## Aset

Gambar raster yang perlu diunduh: logo Tut Wuri (32), foto mentor "Sarah Aurelia" (50/80/82), foto profil mentee (80), maskot hiu bajak laut (dipakai di 3 ukuran dan crop berbeda), serta foto mentor kedua dan ketiga di Mentor Page. Ikon berupa vektor (diekspor ke vector drawable). Sebagian sudah ada di `res/drawable` (`ic_home_*`, `ic_nav_*`, `ic_scholarhub_logo`). Aset baru diunduh per layar saat layar itu dikerjakan.

## Keputusan implementasi (2026-11-20)

1. **Font**: semua layar memakai **Nunito Sans** sesuai token resmi Figma. Teks Inter dipetakan ke Nunito Sans dengan ukuran, line height, dan tracking yang sama. Bobot Inter SemiBold dipetakan ke Nunito Sans **Bold** (`InterSemiBold` di `Type.kt`) karena Nunito SemiBold terlihat jauh lebih tipis; bobot Inter lain dipetakan ke bobot yang sama.
2. **Navigasi tanpa frame tujuan** (ikon artikel, bookmark, "Semua Jadwal", "Detail Sesi", layar hasil ScholarMatch): callback disediakan dengan default kosong dan belum diarahkan ke mana pun.
3. **Tab Detail Beasiswa** mengikuti struktur data katalog:
   - **Ringkasan**: Portal resmi pendaftaran (`registration.applicationUrl`), Tentang Program, Manfaat, Cakupan Program (`summary`).
   - **Persyaratan**: semua `requirements` (Pendidikan & akademik, Kondisi pendaftar, Kemampuan & pengalaman, Komitmen & keberlanjutan, Prioritas seleksi).
   - **Panduan**: semua `guide.sections` (Dokumen pendaftaran, Cara mendaftar, Jadwal & proses seleksi) dan `guide.officialGuideUrl`.
   - Subbab Figma "Beasiswa Lain" dan "Kontak Bantuan" tidak punya data di skema v1, sehingga tidak tampil (aturan: sembunyikan subbab tanpa isi). Dokumen pendaftaran ada di `guide`, jadi masuk tab Panduan (sebelumnya dicatat di Persyaratan).
4. **Line height**: style teks mematikan trim line height Compose (`LineHeightStyle.Trim.None`) supaya tinggi teks satu baris sama dengan Figma/CSS. Tanpa ini, chip dan kartu jadi 2–3 dp lebih pendek per baris teks.
5. **Border**: border di Figma (hasil impor HTML) menambah ukuran elemen, sedangkan border Compose digambar di dalam. Elemen ber-border tanpa ukuran tetap diberi padding +1 dp.

## Pertanyaan terbuka

1. **Filter katalog Luar Negeri / Dalam Negeri / Fully Funded** (Beasiswa Page). Belum diputuskan (2026-11-20). Pilihan: (a) tambah field lokasi studi dan pendanaan penuh ke `scholarships.json` (perubahan skema: versi, model, parser, pemakai data); atau (b) ganti filter dengan yang didukung data sekarang, misalnya "Masih Dibuka" dan "Arsip". Sampai diputuskan, chip tetap tampil dan memilihnya menampilkan pesan bahwa data belum tersedia.

## Status layar

| Layar | Status | Catatan beda dengan Figma |
| --- | --- | --- |
| Home Page | Selesai | Lihat laporan di bawah. |
| Mentor Page | Selesai | Lihat laporan di bawah. |
| Detail Mentor | Selesai | Lihat laporan di bawah. |
| Detail Pembayaran | Selesai | Lihat laporan di bawah. |
| Riwayat Transaksi | Selesai | Lihat laporan di bawah. |
| Profil | Selesai | Lihat laporan di bawah. |
| Beasiswa Page | Selesai | Lihat laporan di bawah. |
| Detail Beasiswa | Selesai | Lihat laporan di bawah. |
| ScholarMatch | Selesai | Lihat laporan di bawah. |
| Riwayat Notifikasi | Selesai | Lihat laporan di bawah. |
| Dokumen | Selesai | Lihat laporan di bawah. |

### Home Page
Dibandingkan di emulator API 37 dengan lebar 402 dp (`wm size 1055x2400` pada density 420).
- Ukuran kartu sesuai: kartu hasil 302 dp (Figma 301.5), carousel 165.5 dp (166), kartu sesi 164 dp (165.4).
- Lingkaran tab Home di bottom nav sempat tampil oval 40×28 karena dipaksa mengikuti tinggi slot. Sudah diperbaiki dengan `wrapContentSize(unbounded = true)`.
- Teks bubble ScholarMatch terpecah di tempat berbeda ("…yang paling / pas buat profilmu!" vs Figma "…yang / paling pas…") karena perbedaan rendering font di Android. Lebar bubble sudah sama (200 dp).
- Header Figma memakai latar blur transparan, sehingga terlihat sedikit lebih terang dari halaman. Di Android latar header sama dengan `Base` dan hanya ada shadow tipis.
- Status bar iOS di Figma digantikan status bar Android.

### Mentor Page
Dibandingkan di emulator API 37 dengan lebar 402 dp.
- Tinggi kartu: mentor 1 177 dp (Figma 178), mentor 2 189 dp (191.6), sesi 164 dp (165.4). Pemenggalan baris afiliasi sama dengan Figma.
- Tombol "Masuk Room" di layar ini berteks/ikon putih, sedangkan di Home `#ECF5FB`; diatur lewat `joinContentColor`.
- Teks chip "Filter" di Figma memakai Plus Jakarta Sans Medium 12. Di aplikasi memakai Nunito Sans Medium 12 sesuai keputusan font.
- Tombol "Info & Booking" di Figma punya drop shadow `0 1 1` hitam 5% yang hampir tak terlihat; di aplikasi tanpa shadow.
- Foto mentor dari Figma berukuran 864×1536 (±1.7 MB) dan diperkecil ke 432×768 tanpa mengubah crop atau isi.
- Header: latar blur Figma tetap belum ditiru (sama seperti Home).

### Detail Mentor
Dibandingkan di emulator API 37 dengan lebar 402 dp.
- Kartu profil 458 dp (Figma 461), slot jam 64 dp dengan jarak 10, chip tanggal 62 dp dengan jarak 8. Tinggi chip: tidak aktif 74, aktif 72, sama seperti Figma.
- Interaksi: memilih tanggal dan jam memperbarui ringkasan di bar bawah ("Kam, 22 Okt • 12:00 - 12:59 WIB"). Slot "Booked" tidak bisa dipilih. Tombol "Lanjut Bayar" membuka Detail Pembayaran dengan `BookingSelection`.
- Tombol back Android dan tombol kembali di header kembali ke Mentor Page. Layar lain belum punya `BackHandler` sehingga back Android masih menutup aplikasi.
- Lebar tombol "Lanjut Bayar" mengisi sisa ruang setelah ringkasan (maks 217 dp), sama seperti Figma, jadi lebarnya ikut berubah sesuai panjang teks ringkasan.
- Nama hari di Figma memakai Inter 16. Di aplikasi memakai Nunito Sans 16.
- Foto di frame Figma sama dengan avatar sesi (`mentor_avatar`), meskipun namanya Samantha Dira; dipakai apa adanya.

### Detail Pembayaran
Dibandingkan di emulator API 37 dengan lebar 402 dp.
- Tinggi: kartu ringkasan 218 dp (Figma 219.7), QRIS 109 (110.6), Virtual Account 105 (106.5), Kartu 74 (75). Tombol 41 dp + shadow solid 3.09 dp.
- Memilih metode memindahkan radio, warna judul, gaya badge provider, dan opacity (kartu tidak terpilih 90%), sesuai dua varian di Figma.
- Saat dibuka dari Detail Mentor, jadwal, harga, nama, dan afiliasi mentor diambil dari pilihan booking. Judul layanan memakai "Sesi Bimbingan" karena Detail Mentor tidak punya pilihan jenis layanan; frame Figma memakai contoh "Simulasi Wawancara".
- "Bayar Sekarang" mengirim metode terpilih lewat `onPay`; belum ada layar atau proses pembayaran sesudahnya.
- Tombol back Android dan tombol kembali di header kembali ke Detail Mentor. Pilihan tanggal/jam di Detail Mentor kembali ke default karena layar dibuat ulang.
- Teks Inter di Figma dipetakan ke Nunito Sans dengan ukuran, line height, dan tracking yang sama.

### Riwayat Transaksi
Dibandingkan di emulator API 37 dengan lebar 402 dp (dari Profil > "Riwayat Bimbingan & Transaksi", atau langsung dengan `adb shell am start -n com.example.scholarhub/.MainActivity --es startScreen transactions`).
- Tinggi: kartu tagihan 277 dp (Figma 279), kartu selesai 254 dp (257), chip filter 28.3 dp (29.4).
- Chip filter menyaring daftar (Semua / Menunggu / Berhasil / Dibatalkan). Angka di chip dan "2 Transaksi Selesai" adalah ringkasan dari data (`counts`, `monthCompletedCount`), bukan hitungan kartu yang tampil, sama seperti di Figma.
- Daftar kosong (mis. filter "Dibatalkan") menampilkan teks "Belum ada transaksi dibatalkan." — tidak ada desain empty state di Figma.
- "Lanjut Bayar" membuka Detail Pembayaran dengan contoh tagihan yang sama dengan frame Figma; back kembali ke Riwayat Transaksi. "Detail Sesi" belum punya tujuan.
- Back dari Riwayat Transaksi kembali ke Profil.
- Chip yang terpilih di luar layar (mis. "Dibatalkan") tidak otomatis di-scroll ke tampilan.

### Profil
Dibandingkan di emulator API 37 dengan lebar 402 dp.
- Tinggi: kartu profil 236 dp (Figma 237), tile statistik 93 (92), kartu tracker 482 (485.8), kartu prestasi 120 (120.4), kartu Akun & Preferensi 189 (188), tombol keluar 48 (49).
- Tab Profil di bottom nav membuka layar ini; back (header/Android) kembali ke Home. "Riwayat Bimbingan & Transaksi" membuka Riwayat Transaksi; "Keluar dari Akun" kembali ke Login.
- Belum punya tujuan: Edit Profil Lengkap, dropdown beasiswa di tracker, + Tambah, menu ⋮ prestasi, Bookmark, Keamanan & Kata Sandi. Profil tersimpan dan bookmark termasuk fase berikutnya menurut `AGENTS.md`.
- Ikon dropdown di Figma memakai `<use>` dengan 5 lapisan hitam 20% di atas #0B1727; vector ditulis manual dengan warna hasil akhir #04080D.
- Judul prestasi memakai baris baru eksplisit seperti teks Figma ("Juara 1 Hackathon Nasional\nGEMASTIK 2023").
- Saat membandingkan layar ini ditemukan bahwa Inter SemiBold → Nunito SemiBold terlalu tipis; pemetaan diubah ke Bold untuk semua layar (Pembayaran, Transaksi, Profil, chip filter).

### Beasiswa Page
Dibandingkan di emulator API 37 dengan lebar 402 dp, tanggal perangkat 20 November 2026.
- Isi kartu diambil dari `scholarships.json` lewat `ScholarshipListViewModel` (`feature/scholarship/list/`), bukan contoh statis. Pemetaan ada di `ScholarshipListMapper.kt` dan diuji di `ScholarshipListMapperTest`.
  - Penyelenggara (huruf kapital), nama, dan periode dari data. Baris jenjang: "Jenjang D4 / S1 • Periode …".
  - Tag jenjang, semester, dan IPK diringkas dari `commonRule` + semua `tracks`; IPK memakai batas terendah antarjalur.
  - Sisa hari dihitung dari `closesOn` (+ `closesAtLocal` jika ada) di zona waktu pendaftaran terhadap waktu sekarang: "N Hari Lagi", "Hari Terakhir", "Ditutup"; status `archived` tampil "Arsip". Urutan: masih buka (paling dekat tutup), tanpa tenggat, ditutup, arsip.
  - "N program aktif" = jumlah yang masih buka per waktu sekarang (4 dari 10 pada 20 Nov 2026).
- Beda dengan Figma karena data/aturan:
  - Badge "% Relevan" disembunyikan; `AGENTS.md` hanya mengizinkan skor relevansi dari hasil ScholarMatch.
  - Katalog tidak punya logo penyelenggara; kotak logo menampilkan inisial (mis. "PS", "IM").
  - Filter Luar Negeri / Dalam Negeri / Fully Funded tampil seperti Figma, tetapi skema v1 tidak punya lokasi studi atau status pendanaan penuh; memilihnya menampilkan pesan bahwa datanya belum tersedia. Butuh perubahan skema (versi, model, parser) untuk bisa berfungsi.
  - Search bar hanya tampilan; pencarian katalog termasuk fase berikutnya.
- Ukuran: search bar 48–50 dp (Figma 50), banner 112 (114), chip 33 (32–33.5).
- "Lihat Detail Program" membuka detail beasiswa yang sudah ada; back kembali ke katalog.
- Ikon chip lokasi/pendanaan menjadi putih saat chip aktif (Figma hanya menggambar "Semua" aktif).

### Detail Beasiswa
Dibandingkan di emulator API 37 dengan lebar 402 dp (SH01 dibuka dengan extra `scholarshipId`, juga dari katalog).
- Layar lama ditulis ulang memakai token tema dan komponen bersama; ViewModel, state (loading/siap/tidak ditemukan/error), pembukaan tautan, dan skor relevansi khusus ScholarMatch tetap.
- Hanya subbab tab aktif yang dirender; subbab tanpa isi disembunyikan; satu kartu teks tanpa ikon per manfaat dan satu kartu per dokumen dari `items`; tombol portal hanya tampil jika `applicationUrl` ada.
- Isi teks per subbab: "Pendidikan & akademik" dan "Cakupan Program" memakai grid label 120 dp untuk baris "Label: nilai"; subbab lain dengan beberapa baris memakai bullet; "Jadwal & proses seleksi" memakai timeline (cincin r 6.5, stroke 3, batang 3 dp) yang digambar ulang dengan Canvas agar jumlah tahap mengikuti data.
- Judul subbab memakai teks dari data (mis. "Pendidikan & akademik"), bukan teks contoh Figma.
- Kotak organisasi 80 dp di hero berisi inisial penyelenggara karena katalog belum punya logo (Figma: kotak navy kosong).
- Bottom nav sekarang berfungsi (sebelumnya hiasan statis); banner CTA "Cari Mentor Esai" membuka Mentor Page; bookmark di header hanya tampilan (fase berikutnya).
- PNG `detail_*` lama yang tidak dipakai lagi dihapus; `detail_back.png` tetap karena dipakai `AuthHeader`.

### ScholarMatch
Dibandingkan di emulator API 37 dengan lebar 402 dp (Home → "Test Lagi").
- Form mengikuti Figma: bagian A–D, 13 pertanyaan, tombol "Simpan dan Temukan Beasiswa yang Relevan" dengan ikon sparkle. Frame di-scale ×0.897 secara merata (termasuk font), dan nilainya dipakai apa adanya. Tombol pilihan 35 dp, input 39 dp, lebar kartu 371 dp (Figma 373).
- Pilihan prioritas (pertanyaan 13) diambil dari `preferenceOptions` katalog (11 opsi), maksimal 5; opsi lain dinonaktifkan saat batas tercapai. Data belum punya deskripsi opsi, jadi hanya label yang tampil (Figma menampilkan judul + deskripsi contoh).
- Catatan di bawah tombol diganti dari "Data terenkripsi…" menjadi "Jawaban hanya dipakai sementara untuk pencocokan beasiswa." karena aplikasi tidak menyimpan/mengenkripsi jawaban.
- Dropdown TOEFL/IELTS punya opsi Belum Ada Tes, TOEFL ITP, TOEFL iBT, IELTS (Figma hanya menunjukkan "Belum Ada Tes"); kolom skor muncul setelah memilih tes.
- Matcher (`domain/scholarmatch/ScholarshipMatcher.kt`) menerapkan aturan `AGENTS.md`: `commonRule` + tiap jalur terpisah, grup `all`/`any`, operator eq/in/gt/gte/lte, jawaban kosong = belum diketahui, `manualChecks` → Perlu dicek, skor = prioritas unik yang cocok dengan tag manfaat (tanpa manfaat bersyarat/sekunder). Hanya beasiswa dengan `statusAtSnapshot` "open…" yang direkomendasikan, sehingga beasiswa yang tenggatnya sudah lewat setelah `scheduleAsOf` tetap muncul dengan chip "Ditutup".
- Karena banyak field aturan tidak ditanyakan form (mis. kewarganegaraan, status mahasiswa aktif), sebagian besar hasil berstatus **Perlu dicek** dengan daftar alasan. Ini perilaku yang diminta aturan, bukan bug.
- Layar hasil tidak punya frame Figma: memakai `ScholarshipCard` (badge "% Relevan" = prioritas cocok / prioritas dipilih) ditambah status, jalur, prioritas cocok, dan maksimal 3 alasan.
- Navigasi: form ↔ hasil berbagi `ScholarMatchViewModel`; back dari hasil mempertahankan jawaban; back dari form ke Home membuang jawaban; detail dari hasil menampilkan skor relevansi.
- Bug yang ditemukan dan diperbaiki saat uji: ketikan cepat di input teks hilang karena nilai field dibaca dari StateFlow; teks sekarang disimpan lokal lalu diteruskan ke ViewModel.
- Section "Hasil ScholarMatch" di Home masih contoh statis dan belum terhubung ke hasil ini.

### Riwayat Notifikasi
Dibandingkan di emulator API 37 dengan lebar 402 dp (ikon lonceng di header Home, Mentor Page, atau Beasiswa Page).
- Tinggi kartu 161–162 dp (Figma 162), chip 32 dp (31). Frame di-scale ×0.949; nilai Figma dipakai apa adanya.
- Isi masih contoh statis sesuai Figma; project belum punya sumber data notifikasi.
- Interaksi: chip menyaring per kategori; mengetuk kartu menandainya sudah dibaca, dan jumlah "belum dibaca", badge "Semua", serta badge "N baru" per grup ikut berubah. Status dibaca hanya bertahan selama layar terbuka.
- Figma menulis "3 notifikasi belum dibaca" dan grup Kemarin "1 baru", tetapi kartu di grup Kemarin digambar dengan gaya sudah dibaca. Kartu itu dibuat belum dibaca agar semua angka konsisten.
- Kategori "Keamanan Akun" tidak punya chip di Figma, jadi hanya tampil di filter "Semua". Chip "Status Berkas" belum punya contoh notifikasi.
- Avatar mentor memakai foto 50 × 51 dengan cincin Ocean 40% sesuai Figma (berbeda dari avatar kartu sesi).
- Back (header/Android) kembali ke layar asal.

### Dokumen
Dibandingkan di emulator API 37 dengan lebar 402 dp (tab Premium).
- Tinggi: kartu premium 139.5 dp (Figma 138–140), kartu gratis 154 (156). Chip filter mulai 35 dp di bawah header seperti Figma.
- Isi masih contoh statis sesuai Figma (dua kartu premium yang sama dan satu gratis; teks "4 Berkas Tersedia" dipertahankan sebagai ringkasan data). Belum ada katalog dokumen.
- Filter Gratis/Premium menyaring berdasarkan harga; Akademik dan Eksklusif belum punya contoh dokumen sehingga menampilkan pesan kosong (kategori dokumen tidak dikarang).
- Tombol "Beli", "Pratinjau", "Unduh", dan menu ⋮ belum punya aksi; alur pembelian dokumen tidak ada di Figma.
- Chip "Gratis" di Figma punya shadow lebih tebal (tampak seperti state ditekan); di aplikasi semua chip tidak aktif memakai gaya yang sama.

## Ringkasan bottom nav dan pintu masuk
| Pintu masuk | Tujuan |
| --- | --- |
| Tab Premium | Dokumen |
| Tab Mentor | Mentor Page → Detail Mentor → Detail Pembayaran |
| Tab Home | Home → "Test Lagi" → ScholarMatch → Hasil → Detail Beasiswa |
| Tab Beasiswa | Beasiswa Page → Detail Beasiswa |
| Tab Profil | Profil → Riwayat Transaksi → Detail Pembayaran |
| Ikon lonceng (Home, Mentor, Beasiswa, Dokumen) | Riwayat Notifikasi |

