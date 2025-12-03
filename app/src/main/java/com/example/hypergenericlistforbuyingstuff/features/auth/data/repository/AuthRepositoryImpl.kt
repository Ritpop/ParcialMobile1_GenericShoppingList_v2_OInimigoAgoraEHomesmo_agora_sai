package com.example.hypergenericlistforbuyingstuff.features.auth.data.repository

import com.example.hypergenericlistforbuyingstuff.core.utils.Resource
import com.example.hypergenericlistforbuyingstuff.features.auth.data.source.AuthDataSource
import com.example.hypergenericlistforbuyingstuff.features.auth.data.source.UserDataSource
import com.example.hypergenericlistforbuyingstuff.models.User

class AuthRepositoryImpl(
    private val authDataSource: AuthDataSource,
    private val userDataSource: UserDataSource
) : AuthRepository {

    override suspend fun login(email: String, password: String): Resource<User> {
        return try {
            val uid = authDataSource.login(email, password)
            val user = userDataSource.getUser(uid)
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro desconhecido no login")
        }
    }

    override suspend fun register(name: String, email: String, password: String): Resource<User> {
        return try {
            val uid = authDataSource.register(email, password)
            val newUser = User(id = uid, name = name, email = email)
            userDataSource.saveUser(newUser)
            Resource.Success(newUser)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro ao registrar usuário")
        }
    }

    override suspend fun logout() {
        authDataSource.logout()
    }

    override suspend fun getCurrentUser(): User? {
        val uid = authDataSource.getCurrentUserUid() ?: return null
        return userDataSource.getUser(uid)
    }

    override suspend fun recoverPassword(email: String): Resource<Unit> {
        return try {
            authDataSource.recoverPassword(email)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro ao enviar email")
        }
    }
}