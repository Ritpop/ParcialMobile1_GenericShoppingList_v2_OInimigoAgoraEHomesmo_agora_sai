package com.example.hypergenericlistforbuyingstuff.features.shoppinglist.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hypergenericlistforbuyingstuff.core.utils.Resource
import com.example.hypergenericlistforbuyingstuff.features.shoppinglist.data.repository.ShoppingListRepository
import com.example.hypergenericlistforbuyingstuff.models.GroupedListItem
import com.example.hypergenericlistforbuyingstuff.models.ListItem
import kotlinx.coroutines.launch

class ListItemsViewModel(
    private val repository: ShoppingListRepository
) : ViewModel() {

    private val _itemsState = MutableLiveData<Resource<List<GroupedListItem>>>()
    val itemsState: LiveData<Resource<List<GroupedListItem>>> = _itemsState

    private val _operationState = MutableLiveData<Resource<Unit>>()
    val operationState: LiveData<Resource<Unit>> = _operationState

    fun loadItems(listId: String) {
        _itemsState.value = Resource.Loading
        viewModelScope.launch {
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
            val item = ListItem(name = name, quantity = quantity, unit = unit, category = category, listId = listId)
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

        groupedUnchecked.forEach { (category, categoryItems) ->
            val emoji = getEmojiForCategory(category)
            groupedList.add(GroupedListItem.Header(category, emoji))
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

    private fun getEmojiForCategory(category: String): String {
        return when (category) {
            "Fruta" -> "🍎"
            "Verdura" -> "🥦"
            "Carne" -> "🥩"
            "Laticínios" -> "🥛"
            "Padaria" -> "🍞"
            "Bebidas" -> "🥤"
            "Limpeza" -> "🧼"
            "Higiene" -> "🪥"
            else -> "📦"
        }
    }
}