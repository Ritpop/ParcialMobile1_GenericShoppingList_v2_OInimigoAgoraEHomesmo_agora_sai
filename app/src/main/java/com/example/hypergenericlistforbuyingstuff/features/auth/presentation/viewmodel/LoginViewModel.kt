package com.example.hypergenericlistforbuyingstuff.features.auth.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hypergenericlistforbuyingstuff.core.utils.Resource
import com.example.hypergenericlistforbuyingstuff.features.auth.data.repository.AuthRepository
import com.example.hypergenericlistforbuyingstuff.models.User
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _loginState = MutableLiveData<Resource<User>>()
    val loginState: LiveData<Resource<User>> = _loginState

    private val _recoverState = MutableLiveData<Resource<Unit>>()
    val recoverState: LiveData<Resource<Unit>> = _recoverState

    fun login(email: String, password: String) {
        _loginState.value = Resource.Loading
        viewModelScope.launch {
            val result = repository.login(email, password)
            _loginState.value = result
        }
    }

    fun recoverPassword(email: String) {
        _recoverState.value = Resource.Loading
        viewModelScope.launch {
            val result = repository.recoverPassword(email)
            _recoverState.value = result
        }
    }

    suspend fun getCurrentUser(): User? {
        return repository.getCurrentUser()
    }
}