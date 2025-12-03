package com.example.hypergenericlistforbuyingstuff.features.auth.data.source

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class FirebaseAuthDataSource(private val auth: FirebaseAuth) : AuthDataSource {

    override suspend fun login(email: String, password: String): String {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user?.uid ?: throw Exception("UID nulo")
    }

    override suspend fun register(email: String, password: String): String {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        return result.user?.uid ?: throw Exception("UID nulo")
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun getCurrentUserUid(): String? {
        return auth.currentUser?.uid
    }

    override suspend fun recoverPassword(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }
}