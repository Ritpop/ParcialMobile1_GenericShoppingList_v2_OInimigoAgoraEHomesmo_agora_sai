package com.example.hypergenericlistforbuyingstuff.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hypergenericlistforbuyingstuff.core.utils.Resource
import com.example.hypergenericlistforbuyingstuff.features.auth.data.repository.AuthRepository
import com.example.hypergenericlistforbuyingstuff.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<Resource<User>?>(null)
    val loginState: StateFlow<Resource<User>?> = _loginState.asStateFlow()

    private val _recoverState = MutableStateFlow<Resource<Unit>?>(null)
    val recoverState: StateFlow<Resource<Unit>?> = _recoverState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading
            val result = repository.login(email, password)
            _loginState.value = result
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading
            val result = repository.loginWithGoogle(idToken)
            _loginState.value = result
        }
    }

    fun recoverPassword(email: String) {
        viewModelScope.launch {
            _recoverState.value = Resource.Loading
            val result = repository.recoverPassword(email)
            _recoverState.value = result
        }
    }

    suspend fun getCurrentUser(): User? {
        return repository.getCurrentUser()
    }
}