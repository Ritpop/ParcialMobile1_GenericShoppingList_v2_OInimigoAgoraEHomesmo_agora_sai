package com.example.hypergenericlistforbuyingstuff.features.auth.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hypergenericlistforbuyingstuff.core.utils.Resource
import com.example.hypergenericlistforbuyingstuff.features.auth.data.repository.AuthRepository
import com.example.hypergenericlistforbuyingstuff.models.User
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _registerState = MutableLiveData<Resource<User>>()
    val registerState: LiveData<Resource<User>> = _registerState

    fun register(name: String, email: String, password: String) {
        _registerState.value = Resource.Loading
        viewModelScope.launch {
            val result = repository.register(name, email, password)
            _registerState.value = result
        }
    }
}