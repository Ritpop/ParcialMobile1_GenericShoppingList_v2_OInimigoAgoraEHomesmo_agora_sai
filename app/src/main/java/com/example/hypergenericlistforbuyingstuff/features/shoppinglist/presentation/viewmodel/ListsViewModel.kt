package com.example.hypergenericlistforbuyingstuff.features.shoppinglist.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hypergenericlistforbuyingstuff.core.utils.Resource
import com.example.hypergenericlistforbuyingstuff.features.auth.data.repository.AuthRepository
import com.example.hypergenericlistforbuyingstuff.features.shoppinglist.data.repository.ShoppingListRepository
import com.example.hypergenericlistforbuyingstuff.models.ShoppingList
import kotlinx.coroutines.launch

class ListsViewModel(
    private val listRepository: ShoppingListRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _listsState = MutableLiveData<Resource<List<ShoppingList>>>()
    val listsState: LiveData<Resource<List<ShoppingList>>> = _listsState

    private val _deleteState = MutableLiveData<Resource<Unit>>()
    val deleteState: LiveData<Resource<Unit>> = _deleteState

    fun loadLists() {
        _listsState.value = Resource.Loading
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            if (currentUser != null) {
                val result = listRepository.getLists(currentUser.id)
                _listsState.value = result
            } else {
                _listsState.value = Resource.Error("usuario não foi autenticado")
            }
        }
    }

    fun deleteList(listId: String) {
        viewModelScope.launch {
            val result = listRepository.deleteList(listId)
            _deleteState.value = result
            if (result is Resource.Success) {
                loadLists()
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
    fun searchLists(query: String) {
        if (query.isBlank()) {
            loadLists()
            return
        }
        _listsState.value = Resource.Loading
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            if (user != null) {
                val result = listRepository.searchLists(user.id, query)
                _listsState.value = result
            }
        }
    }
}