package com.example.hypergenericlistforbuyingstuff.features.category.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hypergenericlistforbuyingstuff.core.utils.Resource
import com.example.hypergenericlistforbuyingstuff.features.category.data.repository.CategoryRepository
import com.example.hypergenericlistforbuyingstuff.models.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(private val repository: CategoryRepository) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _status = MutableStateFlow<Resource<Unit>?>(null)
    val status: StateFlow<Resource<Unit>?> = _status.asStateFlow()

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