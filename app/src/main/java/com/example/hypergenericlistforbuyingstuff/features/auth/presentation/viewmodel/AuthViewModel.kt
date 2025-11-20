package com.example.hypergenericlistforbuyingstuff.features.auth.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hypergenericlistforbuyingstuff.core.utils.Resource
import com.example.hypergenericlistforbuyingstuff.features.auth.data.repository.AuthRepository
import com.example.hypergenericlistforbuyingstuff.models.User
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _authState = MutableLiveData<Resource<User>>()
    val authState: LiveData<Resource<User>> = _authState

    private val _recoverState = MutableLiveData<Resource<Unit>>()
    val recoverState: LiveData<Resource<Unit>> = _recoverState

    fun login(email: String, password: String) {
        _authState.value = Resource.Loading
        viewModelScope.launch {
            val result = repository.login(email, password)
            _authState.value = result
        }
    }

    fun register(name: String, email: String, password: String) {
        _authState.value = Resource.Loading
        viewModelScope.launch {
            val result = repository.register(name, email, password)
            _authState.value = result
        }
    }

    fun recoverPassword(email: String) {
        _recoverState.value = Resource.Loading
        viewModelScope.launch {
            val result = repository.recoverPassword(email)
            _recoverState.value = result
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    suspend fun getCurrentUser(): User? {
        return repository.getCurrentUser()
    }
}