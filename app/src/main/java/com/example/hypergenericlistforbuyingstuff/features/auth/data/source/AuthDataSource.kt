package com.example.hypergenericlistforbuyingstuff.features.auth.data.source


interface AuthDataSource {
    suspend fun login(email: String, password: String): String
    suspend fun register(email: String, password: String): String 
    suspend fun logout()
    suspend fun getCurrentUserUid(): String?
    suspend fun recoverPassword(email: String)
}