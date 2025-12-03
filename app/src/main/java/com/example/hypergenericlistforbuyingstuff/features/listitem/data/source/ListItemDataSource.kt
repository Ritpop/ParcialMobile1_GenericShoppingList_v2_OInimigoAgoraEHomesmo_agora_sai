package com.example.hypergenericlistforbuyingstuff.features.listitem.data.source

import com.example.hypergenericlistforbuyingstuff.models.ListItem

interface ListItemDataSource {
    suspend fun getItems(listId: String): List<ListItem>
    suspend fun addItem(listId: String, item: ListItem): String
    suspend fun updateItem(listId: String, item: ListItem)
    suspend fun deleteItem(listId: String, itemId: String)
    suspend fun toggleItemChecked(listId: String, itemId: String, isChecked: Boolean)
    suspend fun searchItems(listId: String, query: String): List<ListItem>
}