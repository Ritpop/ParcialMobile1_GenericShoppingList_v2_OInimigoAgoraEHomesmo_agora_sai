package com.example.hypergenericlistforbuyingstuff.features.auth.data.source

import com.example.hypergenericlistforbuyingstuff.models.User

interface AuthDataSource {
    suspend fun login(email: String, password: String): User
    suspend fun register(name: String, email: String, password: String): User
    suspend fun logout()
    suspend fun getCurrentUser(): User?
    suspend fun recoverPassword(email: String)
}