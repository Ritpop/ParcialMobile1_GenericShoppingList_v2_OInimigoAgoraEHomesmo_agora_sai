package com.example.hypergenericlistforbuyingstuff.features.shoppinglist.data.repository

import android.net.Uri
import com.example.hypergenericlistforbuyingstuff.core.utils.Resource
import com.example.hypergenericlistforbuyingstuff.models.Category
import com.example.hypergenericlistforbuyingstuff.models.ListItem
import com.example.hypergenericlistforbuyingstuff.models.ShoppingList

interface ShoppingListRepository {
    suspend fun getLists(userId: String): Resource<List<ShoppingList>>
    suspend fun addList(name: String, ownerId: String, imageUri: Uri?): Resource<String>
    suspend fun updateList(list: ShoppingList, imageUri: Uri?): Resource<Unit>
    suspend fun deleteList(listId: String): Resource<Unit>
    suspend fun getListById(listId: String): ShoppingList?

    suspend fun getItems(listId: String): Resource<List<ListItem>>
    suspend fun addItem(listId: String, item: ListItem): Resource<String>
    suspend fun updateItem(listId: String, item: ListItem): Resource<Unit>
    suspend fun deleteItem(listId: String, itemId: String): Resource<Unit>
    suspend fun toggleItemChecked(listId: String, itemId: String, isChecked: Boolean): Resource<Unit>
    suspend fun searchLists(userId: String, query: String): Resource<List<ShoppingList>>
    suspend fun searchItems(listId: String, query: String): Resource<List<ListItem>>

    suspend fun getCategories(): Resource<List<Category>>
    suspend fun addCategory(name: String, emoji: String): Resource<Unit>
    suspend fun deleteCategory(categoryId: String): Resource<Unit>
}