package com.example.hypergenericlistforbuyingstuff.features.auth.data.source

import com.example.hypergenericlistforbuyingstuff.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserDataSource(private val firestore: FirebaseFirestore) {

    suspend fun saveUser(user: User) {
        firestore.collection("users").document(user.id).set(user).await()
    }

    suspend fun getUser(uid: String): User {
        return try {
            val document = firestore.collection("users").document(uid).get().await()
            val name = document.getString("name") ?: ""
            val email = document.getString("email") ?: ""
            User(id = uid, name = name, email = email)
        } catch (e: Exception) {
            User(id = uid)
        }
    }
}