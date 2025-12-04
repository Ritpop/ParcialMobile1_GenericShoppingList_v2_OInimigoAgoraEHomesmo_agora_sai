package com.example.hypergenericlistforbuyingstuff.features.shoppinglist.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hypergenericlistforbuyingstuff.core.utils.Resource
import com.example.hypergenericlistforbuyingstuff.features.auth.data.repository.AuthRepository
import com.example.hypergenericlistforbuyingstuff.features.shoppinglist.data.repository.ShoppingListRepository
import com.example.hypergenericlistforbuyingstuff.models.ShoppingList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ListsViewModel(
    private val listRepository: ShoppingListRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _listsState = MutableStateFlow<Resource<List<ShoppingList>>?>(null)
    val listsState: StateFlow<Resource<List<ShoppingList>>?> = _listsState.asStateFlow()

    private val _deleteState = MutableStateFlow<Resource<Unit>?>(null)
    val deleteState: StateFlow<Resource<Unit>?> = _deleteState.asStateFlow()

    fun loadLists() {
        viewModelScope.launch {
            _listsState.value = Resource.Loading
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

    fun searchLists(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                loadLists()
                return@launch
            }
            _listsState.value = Resource.Loading
            val user = authRepository.getCurrentUser()
            if (user != null) {
                val result = listRepository.searchLists(user.id, query)
                _listsState.value = result
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}