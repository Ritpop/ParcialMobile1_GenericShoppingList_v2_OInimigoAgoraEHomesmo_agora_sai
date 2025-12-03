package com.example.hypergenericlistforbuyingstuff.features.shoppinglist.data.repository

import android.net.Uri
import com.example.hypergenericlistforbuyingstuff.core.utils.Resource
import com.example.hypergenericlistforbuyingstuff.models.Category
import com.example.hypergenericlistforbuyingstuff.models.ShoppingList

interface ShoppingListRepository {
    suspend fun getLists(userId: String): Resource<List<ShoppingList>>
    suspend fun addList(name: String, ownerId: String, imageUri: Uri?): Resource<String>
    suspend fun updateList(list: ShoppingList, imageUri: Uri?): Resource<Unit>
    suspend fun deleteList(listId: String): Resource<Unit>
    suspend fun getListById(listId: String): ShoppingList?
    suspend fun searchLists(userId: String, query: String): Resource<List<ShoppingList>>

    suspend fun getCategories(): Resource<List<Category>>
    suspend fun addCategory(name: String, emoji: String): Resource<Unit>
    suspend fun deleteCategory(categoryId: String): Resource<Unit>
}