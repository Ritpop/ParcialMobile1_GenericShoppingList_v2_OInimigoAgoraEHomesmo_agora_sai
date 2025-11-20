package com.example.hypergenericlistforbuyingstuff.features.auth.data.source

import com.example.hypergenericlistforbuyingstuff.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseAuthDataSource(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthDataSource {

    override suspend fun login(email: String, password: String): User {
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        val uid = authResult.user?.uid ?: throw Exception("Erro ao obter UID")
        val document = firestore.collection("users").document(uid).get().await()
        val name = document.getString("name") ?: ""

        return User(id = uid, email = email, name = name)
    }

    override suspend fun register(name: String, email: String, password: String): User {
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = authResult.user?.uid ?: throw Exception("Erro ao criar usuário")
        val newUser = User(id = uid, name = name, email = email)

        firestore.collection("users").document(uid).set(newUser).await()

        return newUser
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return try {
            val doc = firestore.collection("users").document(firebaseUser.uid).get().await()
            val name = doc.getString("name") ?: ""
            User(id = firebaseUser.uid, email = firebaseUser.email ?: "", name = name)
        } catch (e: Exception) {
            User(id = firebaseUser.uid, email = firebaseUser.email ?: "", name = "")
        }
    }

    override suspend fun recoverPassword(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }
}