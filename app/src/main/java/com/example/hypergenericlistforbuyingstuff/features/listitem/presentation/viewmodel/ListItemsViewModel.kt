package com.example.hypergenericlistforbuyingstuff.features.listitem.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hypergenericlistforbuyingstuff.core.utils.Resource
import com.example.hypergenericlistforbuyingstuff.features.category.data.repository.CategoryRepository
import com.example.hypergenericlistforbuyingstuff.features.listitem.data.repository.ListItemRepository
import com.example.hypergenericlistforbuyingstuff.models.GroupedListItem
import com.example.hypergenericlistforbuyingstuff.models.ListItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ListItemsViewModel(
    private val repository: ListItemRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _itemsState = MutableStateFlow<Resource<List<GroupedListItem>>?>(null)
    val itemsState: StateFlow<Resource<List<GroupedListItem>>?> = _itemsState.asStateFlow()

    private val _operationState = MutableStateFlow<Resource<Unit>?>(null)
    val operationState: StateFlow<Resource<Unit>?> = _operationState.asStateFlow()

    private var categoryMap = mapOf<String, String>()

    fun loadItems(listId: String) {
        viewModelScope.launch {
            _itemsState.value = Resource.Loading

            val catResult = categoryRepository.getCategories()
            if (catResult is Resource.Success) {
                categoryMap = catResult.data.associate { it.name to it.emoji }
            }

            val result = repository.getItems(listId)
            if (result is Resource.Success) {
                val items = result.data
                val grouped = groupItems(items)
                _itemsState.value = Resource.Success(grouped)
            } else if (result is Resource.Error) {
                _itemsState.value = Resource.Error(result.message)
            }
        }
    }

    fun addItem(listId: String, name: String, quantity: Double, unit: String, category: String) {
        viewModelScope.launch {
            val item = ListItem(
                name = name,
                quantity = quantity,
                unit = unit,
                category = category,
                listId = listId
            )
            val result = repository.addItem(listId, item)
            handleOperationResult(result, listId)
        }
    }

    fun updateItem(listId: String, item: ListItem) {
        viewModelScope.launch {
            val result = repository.updateItem(listId, item)
            handleOperationResult(result, listId)
        }
    }

    fun deleteItem(listId: String, itemId: String) {
        viewModelScope.launch {
            val result = repository.deleteItem(listId, itemId)
            handleOperationResult(result, listId)
        }
    }

    fun toggleItemChecked(listId: String, item: ListItem) {
        val newStatus = !item.isChecked
        viewModelScope.launch {
            val result = repository.toggleItemChecked(listId, item.id, newStatus)
            if (result is Resource.Success) {
                loadItems(listId)
            }
        }
    }

    fun searchItems(listId: String, query: String) {
        if (query.isBlank()) {
            loadItems(listId)
            return
        }
        viewModelScope.launch {
            _itemsState.value = Resource.Loading
            val result = repository.searchItems(listId, query)
            if (result is Resource.Success) {
                val grouped = groupItems(result.data)
                _itemsState.value = Resource.Success(grouped)
            } else if (result is Resource.Error) {
                _itemsState.value = Resource.Error(result.message)
            }
        }
    }

    private fun handleOperationResult(result: Resource<Any>, listId: String) {
        if (result is Resource.Success) {
            _operationState.value = Resource.Success(Unit)
            loadItems(listId)
        } else if (result is Resource.Error) {
            _operationState.value = Resource.Error(result.message)
        }
    }

    private fun groupItems(items: List<ListItem>): List<GroupedListItem> {
        val groupedList = mutableListOf<GroupedListItem>()
        val (checkedItems, uncheckedItems) = items.partition { it.isChecked }
        val groupedUnchecked = uncheckedItems.groupBy { it.category }.toSortedMap()

        groupedUnchecked.forEach { (categoryName, categoryItems) ->
            val emoji = categoryMap[categoryName] ?: "📦"
            groupedList.add(GroupedListItem.Header(categoryName, emoji))
            categoryItems.sortedBy { it.name }.forEach { item ->
                groupedList.add(GroupedListItem.Item(item))
            }
        }

        if (checkedItems.isNotEmpty()) {
            groupedList.add(GroupedListItem.Header("Comprados", "✅"))
            checkedItems.sortedBy { it.name }.forEach { item ->
                groupedList.add(GroupedListItem.Item(item))
            }
        }
        return groupedList
    }
}