package com.example.scholarhub.data.auth

import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

enum class UserRole(val id: String) { Mentee("mentee"), Mentor("mentor") }

/** Akun yang masuk; dokumen `users/{uid}` di Firestore menyimpan nama dan peran. */
data class AppUser(val uid: String, val name: String, val email: String, val role: UserRole)

/**
 * Login dan register dengan Firebase Auth (Email/Password). Peran dipilih saat register dan disimpan di Firestore,
 * sehingga login mengarahkan ke Home mentee atau Home mentor sesuai akun.
 */
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    /** Akun yang masih tersimpan dari sesi sebelumnya, atau null. */
    suspend fun restore(): AppUser? = auth.currentUser?.let { user -> runCatching { loadUser(user.uid, user.email.orEmpty()) }.getOrNull() }

    suspend fun login(email: String, password: String): Result<AppUser> = runCatching {
        val user = auth.signInWithEmailAndPassword(email.trim(), password).await().user ?: error("Akun tidak ditemukan.")
        loadUser(user.uid, user.email.orEmpty())
    }.mapFailure()

    suspend fun register(name: String, email: String, password: String, role: UserRole): Result<AppUser> = runCatching {
        val user = auth.createUserWithEmailAndPassword(email.trim(), password).await().user ?: error("Pendaftaran gagal.")
        val profile = mapOf(
            "name" to name.trim(),
            "email" to user.email.orEmpty(),
            "role" to role.id,
            "createdAt" to FieldValue.serverTimestamp(),
        )
        db.collection(Users).document(user.uid).set(profile).await()
        AppUser(user.uid, name.trim(), user.email.orEmpty(), role)
    }.mapFailure()

    fun logout() = auth.signOut()

    private suspend fun loadUser(uid: String, email: String): AppUser {
        val doc = db.collection(Users).document(uid).get().await()
        // Akun yang dibuat langsung di Firebase Console belum punya dokumen; anggap mentee.
        val role = if (doc.getString("role") == UserRole.Mentor.id) UserRole.Mentor else UserRole.Mentee
        return AppUser(uid, doc.getString("name") ?: email.substringBefore('@'), email, role)
    }

    private companion object {
        const val Users = "users"
    }
}

/** Pesan galat Firebase dalam bahasa Indonesia untuk ditampilkan di form. */
class AuthException(message: String, cause: Throwable? = null) : Exception(message, cause)

private fun <T> Result<T>.mapFailure(): Result<T> = recoverCatching { e ->
    throw AuthException(
        when (e) {
            is FirebaseAuthWeakPasswordException -> "Kata sandi terlalu lemah. Gunakan minimal 8 karakter berisi huruf dan angka."
            is FirebaseAuthUserCollisionException -> "Email sudah terdaftar. Silakan masuk."
            is FirebaseAuthInvalidUserException, is FirebaseAuthInvalidCredentialsException -> "Email atau kata sandi salah."
            is FirebaseNetworkException -> "Tidak ada koneksi internet. Coba lagi."
            is FirebaseTooManyRequestsException -> "Terlalu banyak percobaan. Coba lagi beberapa saat."
            // Email/Password belum diaktifkan di Firebase Console (Authentication → Sign-in method).
            else -> if (e.message.orEmpty().contains("CONFIGURATION_NOT_FOUND")) {
                "Login Email/Password belum diaktifkan di Firebase Console."
            } else {
                "Gagal terhubung ke server. Coba lagi."
            }
        },
        e,
    )
}

private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { cont ->
    addOnCompleteListener { task ->
        val error = task.exception
        if (error != null) cont.resumeWithException(error) else if (task.isCanceled) cont.cancel() else cont.resume(task.result)
    }
}
