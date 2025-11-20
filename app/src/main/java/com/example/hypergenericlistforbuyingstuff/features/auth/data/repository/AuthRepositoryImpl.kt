package com.example.hypergenericlistforbuyingstuff.features.auth.data.repository

import com.example.hypergenericlistforbuyingstuff.core.utils.Resource
import com.example.hypergenericlistforbuyingstuff.features.auth.data.source.AuthDataSource

class AuthRepositoryImpl(private val dataSource: AuthDataSource) : AuthRepository {

    override suspend fun login(email: String, password: String): Resource<User> {
        return try {
            val user = dataSource.login(email, password)
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro desconhecido no login")
        }
    }

    override suspend fun register(name: String, email: String, password: String): Resource<User> {
        return try {
            val user = dataSource.register(name, email, password)
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro ao registrar usuário")
        }
    }

    override suspend fun logout() {
        dataSource.logout()
    }

    override suspend fun getCurrentUser(): User? {
        return dataSource.getCurrentUser()
    }

    override suspend fun recoverPassword(email: String): Resource<Unit> {
        return try {
            dataSource.recoverPassword(email)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro ao enviar email")
        }
    }
}