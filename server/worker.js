/**
 * ScholarHub Pay: perantara aplikasi Android dan Midtrans Sandbox (QRIS).
 * Server Key Midtrans disimpan sebagai secret Cloudflare `MIDTRANS_SERVER_KEY`, tidak pernah ada di aplikasi atau repo.
 *
 * POST /qris    { orderId, amount, itemName }  -> { orderId, qrUrl, qrString, expiresAt, status }
 * GET  /status?orderId=...                     -> { orderId, status: "pending" | "paid" | "failed" }
 * GET  /health                                 -> { ok, version }
 */

const MIDTRANS = "https://api.sandbox.midtrans.com/v2";
// Hanya nominal yang dijual aplikasi: sesi mentoring dan dokumen premium. Mencegah tagihan sembarang.
const ALLOWED_AMOUNTS = new Set([50000, 35000]);
const ORDER_ID = /^[A-Za-z0-9._-]{1,50}$/;
const HEADERS = { "Content-Type": "application/json", Accept: "application/json", "User-Agent": "ScholarHub-Pay/1.0" };

export default {
  async fetch(request, env) {
    const url = new URL(request.url);
    // Penanda versi untuk memastikan kode terbaru sudah ter-deploy.
    if (url.pathname === "/health") return json({ ok: true, version: 3, hasKey: Boolean(env.MIDTRANS_SERVER_KEY) });
    if (!env.MIDTRANS_SERVER_KEY) return json({ error: "Server Key belum diatur." }, 500);
    const auth = "Basic " + btoa(env.MIDTRANS_SERVER_KEY.trim() + ":");

    try {
      if (request.method === "POST" && url.pathname === "/qris") return await createQris(request, auth);
      if (request.method === "GET" && url.pathname === "/status") return await checkStatus(url, auth);
      return json({ error: "Tidak ditemukan." }, 404);
    } catch (e) {
      return json({ error: "Gagal menghubungi Midtrans." }, 502);
    }
  },
};

async function createQris(request, auth) {
  const body = await request.json().catch(() => null);
  const orderId = body?.orderId;
  const amount = Number(body?.amount);
  if (!ORDER_ID.test(orderId ?? "") || !ALLOWED_AMOUNTS.has(amount)) {
    return json({ error: "Pesanan tidak valid." }, 400);
  }
  const res = await fetch(`${MIDTRANS}/charge`, {
    method: "POST",
    headers: { ...HEADERS, Authorization: auth },
    body: JSON.stringify({
      payment_type: "qris",
      transaction_details: { order_id: orderId, gross_amount: amount },
      item_details: [{ id: "item", price: amount, quantity: 1, name: String(body.itemName ?? "ScholarHub").slice(0, 50) }],
      qris: { acquirer: "gopay" },
      // Sama dengan batas bayar di aplikasi.
      custom_expiry: { expiry_duration: 15, unit: "minute" },
    }),
  });
  const text = await res.text();
  const data = parse(text);
  if (!res.ok || !String(data.status_code).startsWith("2")) {
    // Teruskan alasan Midtrans (mis. kunci salah, QRIS belum aktif, atau diblokir) agar mudah dilacak.
    const reason = data.status_message ?? data.error ?? (data.error_messages ?? []).join(", ");
    return json({ error: reason || "Tagihan gagal dibuat.", midtransCode: data.status_code ?? res.status, raw: text.slice(0, 300) }, 502);
  }
  const qr = (data.actions ?? []).find((a) => a.name === "generate-qr-code");
  return json({
    orderId,
    qrUrl: qr?.url ?? null,
    qrString: data.qr_string ?? null,
    expiresAt: data.expiry_time ?? null,
    status: mapStatus(data.transaction_status),
  });
}

async function checkStatus(url, auth) {
  const orderId = url.searchParams.get("orderId") ?? "";
  if (!ORDER_ID.test(orderId)) return json({ error: "Pesanan tidak valid." }, 400);
  const res = await fetch(`${MIDTRANS}/${encodeURIComponent(orderId)}/status`, {
    headers: { ...HEADERS, Authorization: auth },
  });
  const data = parse(await res.text());
  // 404 dari Midtrans: tagihan belum pernah dibuat.
  if (String(data.status_code) === "404") return json({ orderId, status: "failed" });
  return json({ orderId, status: mapStatus(data.transaction_status) });
}

function mapStatus(status) {
  if (status === "settlement" || status === "capture") return "paid";
  if (status === "pending") return "pending";
  return "failed"; // expire, cancel, deny, failure
}

function parse(text) {
  try {
    return JSON.parse(text);
  } catch {
    return {};
  }
}

function json(data, status = 200) {
  return new Response(JSON.stringify(data), { status, headers: { "Content-Type": "application/json" } });
}
