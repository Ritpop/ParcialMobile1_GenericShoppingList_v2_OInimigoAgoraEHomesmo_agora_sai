package com.example.hypergenericlistforbuyingstuff.features.shoppinglist.data.source

import com.example.hypergenericlistforbuyingstuff.models.Category
import com.example.hypergenericlistforbuyingstuff.models.ShoppingList

interface ShoppingListDataSource {
    suspend fun getLists(userId: String): List<ShoppingList>
    suspend fun addList(list: ShoppingList): String
    suspend fun updateList(list: ShoppingList)
    suspend fun deleteList(listId: String)
    suspend fun getListById(listId: String): ShoppingList?
    suspend fun searchLists(userId: String, query: String): List<ShoppingList>

    suspend fun getCategories(): List<Category>
    suspend fun addCategory(category: Category)
    suspend fun deleteCategory(categoryId: String)
}