package com.example.hypergenericlistforbuyingstuff.features.shoppinglist.presentation.viewmodel

import android.net.Uri
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

class ListDetailsViewModel(
    private val listRepository: ShoppingListRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _listData = MutableStateFlow<ShoppingList?>(null)
    val listData: StateFlow<ShoppingList?> = _listData.asStateFlow()

    private val _saveState = MutableStateFlow<Resource<Unit>?>(null)
    val saveState: StateFlow<Resource<Unit>?> = _saveState.asStateFlow()

    fun loadList(listId: String) {
        viewModelScope.launch {
            val list = listRepository.getListById(listId)
            _listData.value = list
        }
    }

    fun saveList(currentListId: String?, name: String, imageUri: Uri?) {
        if (name.isBlank()) {
            _saveState.value = Resource.Error("O nome da lista não pode ser vazio")
            return
        }

        viewModelScope.launch {
            _saveState.value = Resource.Loading
            val currentUser = authRepository.getCurrentUser()
            if (currentUser == null) {
                _saveState.value = Resource.Error("Usuário não logado")
                return@launch
            }

            val result = if (currentListId == null) {
                listRepository.addList(name, currentUser.id, imageUri)
            } else {
                val currentData = _listData.value ?: ShoppingList(id = currentListId, name = name, ownerId = currentUser.id)
                val updatedList = currentData.copy(name = name)
                listRepository.updateList(updatedList, imageUri)
            }

            if (result is Resource.Success) {
                _saveState.value = Resource.Success(Unit)
            } else if (result is Resource.Error) {
                _saveState.value = Resource.Error(result.message)
            }
        }
    }
}