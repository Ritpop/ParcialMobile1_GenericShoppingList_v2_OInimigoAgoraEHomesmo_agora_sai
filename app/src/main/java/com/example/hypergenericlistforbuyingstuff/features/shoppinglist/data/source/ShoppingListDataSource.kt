package com.example.hypergenericlistforbuyingstuff.features.shoppinglist.data.source

import android.net.Uri
import com.example.hypergenericlistforbuyingstuff.models.ShoppingList

interface ShoppingListDataSource {
    suspend fun getLists(userId: String): List<ShoppingList>
    suspend fun addList(list: ShoppingList, imageUri: Uri?): String
    suspend fun updateList(list: ShoppingList, imageUri: Uri?)
    suspend fun deleteList(listId: String)
    suspend fun getListById(listId: String): ShoppingList?
}