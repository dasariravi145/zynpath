package com.zynpath.game.core.auth.provider

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Provider client boundary for Firebase Authentication.
 * Completes Firebase Google sign-in and links anonymous guest accounts.
 */
interface FirebaseAuthClient {
    fun isConfigured(): Boolean
    fun getCurrentUser(): FirebaseUser?
    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser?>
    fun signOut()
}

@Singleton
class FirebaseAuthClientImpl @Inject constructor() : FirebaseAuthClient {

    override fun isConfigured(): Boolean {
        return try {
            FirebaseAuth.getInstance() != null
        } catch (_: Throwable) {
            false
        }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return try {
            FirebaseAuth.getInstance().currentUser
        } catch (_: Throwable) {
            null
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser?> = withContext(Dispatchers.IO) {
        try {
            val auth = FirebaseAuth.getInstance()
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val currentUser = auth.currentUser
            val result = if (currentUser != null && currentUser.isAnonymous) {
                try {
                    currentUser.linkWithCredential(credential).awaitTask()
                } catch (_: Throwable) {
                    auth.signInWithCredential(credential).awaitTask()
                }
            } else {
                auth.signInWithCredential(credential).awaitTask()
            }
            Result.success(result.user)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override fun signOut() {
        try {
            FirebaseAuth.getInstance().signOut()
        } catch (_: Throwable) {}
    }

    private suspend fun <T> com.google.android.gms.tasks.Task<T>.awaitTask(): T =
        suspendCancellableCoroutine { continuation ->
            addOnSuccessListener { result ->
                continuation.resume(result)
            }
            addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
            addOnCanceledListener {
                continuation.cancel()
            }
        }
}
