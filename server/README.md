# ScholarHub Pay (Cloudflare Worker)

Perantara aplikasi Android dan **Midtrans Sandbox** untuk pembayaran QRIS. Server Key Midtrans hanya disimpan di
Cloudflare sebagai secret, tidak pernah di aplikasi atau repo.

## Pasang

1. Midtrans Dashboard → pastikan mode **Sandbox** → **Settings → Access Keys** → salin **Server Key** (`SB-Mid-server-...`).
2. Cloudflare Dashboard → **Workers & Pages → Create → Create Worker** → nama `scholarhub-pay` → **Deploy**.
3. **Edit code** → ganti seluruh isi dengan `worker.js` → **Deploy**.
4. **Settings → Variables and Secrets → Add** → tipe **Secret**, nama `MIDTRANS_SERVER_KEY`, isi Server Key → **Deploy**.
5. Alamat Worker (`https://scholarhub-pay.<akun>.workers.dev`) dipasang di aplikasi (`PaymentGateway.BaseUrl`).

## Endpoint

| Method | Path | Body / query | Balasan |
| --- | --- | --- | --- |
| POST | `/qris` | `{ "orderId", "amount", "itemName" }` | `{ orderId, qrUrl, qrString, expiresAt, status }` |
| GET | `/status` | `?orderId=...` | `{ orderId, status: "pending" \| "paid" \| "failed" }` |

Nominal yang diterima hanya 50.000 (sesi mentoring) dan 35.000 (dokumen premium). Tagihan kedaluwarsa 15 menit.

## Coba bayar (sandbox)

Buka **https://simulator.sandbox.midtrans.com/v2/qris/index**, tempel URL gambar QR (`qrUrl`) dari aplikasi, lalu bayar.
Setelah itu tekan **Periksa Status Pembayaran** di aplikasi.
