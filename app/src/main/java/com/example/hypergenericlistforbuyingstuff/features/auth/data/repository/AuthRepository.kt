package com.example.hypergenericlistforbuyingstuff.features.auth.data.repository

import com.example.hypergenericlistforbuyingstuff.core.utils.Resource
import com.example.hypergenericlistforbuyingstuff.models.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Resource<User>
    suspend fun register(name: String, email: String, password: String): Resource<User>
    suspend fun logout()
    suspend fun getCurrentUser(): User?
    suspend fun recoverPassword(email: String): Resource<Unit>
    suspend fun loginWithGoogle(idToken: String): Resource<User>
}