package com.example.hypergenericlistforbuyingstuff.features.shoppinglist.data.source

import android.net.Uri
import com.example.hypergenericlistforbuyingstuff.models.ListItem
import com.example.hypergenericlistforbuyingstuff.models.ShoppingList

interface ShoppingListDataSource {
    suspend fun getLists(userId: String): List<ShoppingList>
    suspend fun addList(list: ShoppingList, imageUri: Uri?): String
    suspend fun updateList(list: ShoppingList, imageUri: Uri?)
    suspend fun deleteList(listId: String)
    suspend fun getListById(listId: String): ShoppingList?

    suspend fun getItems(listId: String): List<ListItem>
    suspend fun addItem(listId: String, item: ListItem): String
    suspend fun updateItem(listId: String, item: ListItem)
    suspend fun deleteItem(listId: String, itemId: String)
    suspend fun toggleItemChecked(listId: String, itemId: String, isChecked: Boolean)4

    suspend fun searchLists(userId: String, query: String): List<ShoppingList>
    suspend fun searchItems(listId: String, query: String): List<ListItem>
}