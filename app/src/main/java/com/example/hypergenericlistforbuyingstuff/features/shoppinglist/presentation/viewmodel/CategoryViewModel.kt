package com.example.hypergenericlistforbuyingstuff.features.shoppinglist.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hypergenericlistforbuyingstuff.core.utils.Resource
import com.example.hypergenericlistforbuyingstuff.features.shoppinglist.data.repository.ShoppingListRepository
import com.example.hypergenericlistforbuyingstuff.models.Category
import kotlinx.coroutines.launch

class CategoryViewModel(private val repository: ShoppingListRepository) : ViewModel() {

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _status = MutableLiveData<Resource<Unit>>()
    val status: LiveData<Resource<Unit>> = _status

    fun loadCategories() {
        viewModelScope.launch {
            val result = repository.getCategories()
            if (result is Resource.Success) {
                _categories.value = result.data
            }
        }
    }

    fun addCategory(name: String, emoji: String) {
        viewModelScope.launch {
            val result = repository.addCategory(name, emoji)
            _status.value = result
            if (result is Resource.Success) loadCategories()
        }
    }

    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            repository.deleteCategory(categoryId)
            loadCategories()
        }
    }
}