package com.example.ch11.data.auth

import com.example.ch11.data.model.AuthUser
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    // TODO [T2.1] Ganti flowOf(...) dengan callbackFlow yang mengamati status login:
    //   callbackFlow {
    //       val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser?.toAuthUser()) }
    //       auth.addAuthStateListener(listener)
    //       awaitClose { auth.removeAuthStateListener(listener) }
    //   }.distinctUntilChanged()
    // flowOf() hanya memancarkan SATU nilai, sehingga login/logout tidak pernah terdeteksi.
    override val authState: Flow<AuthUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser?.toAuthUser()) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }.distinctUntilChanged()

    override val currentUser: AuthUser?
        get() = auth.currentUser?.toAuthUser()

    // TODO [T2.1] authenticate { auth.signInWithEmailAndPassword(email, password).await() }
    //   (import kotlinx.coroutines.tasks.await)
    override suspend fun login(email: String, password: String): Result<AuthUser> =
        authenticate { auth.signInWithEmailAndPassword(email, password).await() }

    // TODO [T2.1] authenticate { auth.createUserWithEmailAndPassword(email, password).await() }
    //   createUser... otomatis membuat pengguna langsung login.
    override suspend fun register(email: String, password: String): Result<AuthUser> =
        authenticate { auth.createUserWithEmailAndPassword(email, password).await() }

    // TODO [T5.2] Tukar ID token Google menjadi identitas Firebase:
    //   val credential = GoogleAuthProvider.getCredential(idToken, null)
    //   authenticate { auth.signInWithCredential(credential).await() }
    override suspend fun signInWithGoogle(idToken: String): Result<AuthUser> = authenticate {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).await()
    }

    override fun logout() = auth.signOut()

    // Sudah disediakan: menjalankan operasi Firebase dan menerjemahkan
    // exception-nya menjadi pesan bahasa Indonesia (lihat AuthErrors.kt).
    private suspend fun authenticate(block: suspend () -> AuthResult): Result<AuthUser> =
        try {
            val user = block().user ?: error("Pengguna tidak ditemukan")
            Result.success(user.toAuthUser())
        } catch (e: CancellationException) {
            throw e                        // pembatalan coroutine jangan ditelan
        } catch (e: Exception) {
            Result.failure(AuthException(authErrorMessage(e), e))
        }

}

class AuthException(message: String, cause: Throwable? = null) : Exception(message, cause)

private fun FirebaseUser.toAuthUser() = AuthUser(uid = uid, email = email, displayName = displayName)
