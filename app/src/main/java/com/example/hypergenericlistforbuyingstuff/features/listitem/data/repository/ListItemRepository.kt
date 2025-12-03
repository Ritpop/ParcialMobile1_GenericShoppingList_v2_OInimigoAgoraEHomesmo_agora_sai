package com.example.hypergenericlistforbuyingstuff.features.listitem.data.repository

import com.example.hypergenericlistforbuyingstuff.core.utils.Resource
import com.example.hypergenericlistforbuyingstuff.models.ListItem

interface ListItemRepository {
    suspend fun getItems(listId: String): Resource<List<ListItem>>
    suspend fun addItem(listId: String, item: ListItem): Resource<String>
    suspend fun updateItem(listId: String, item: ListItem): Resource<Unit>
    suspend fun deleteItem(listId: String, itemId: String): Resource<Unit>
    suspend fun toggleItemChecked(listId: String, itemId: String, isChecked: Boolean): Resource<Unit>
    suspend fun searchItems(listId: String, query: String): Resource<List<ListItem>>
}