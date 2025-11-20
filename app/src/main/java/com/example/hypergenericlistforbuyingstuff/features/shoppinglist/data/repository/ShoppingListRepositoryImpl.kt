package com.example.hypergenericlistforbuyingstuff.features.shoppinglist.data.repository

import android.net.Uri
import com.example.hypergenericlistforbuyingstuff.core.utils.Resource
import com.example.hypergenericlistforbuyingstuff.features.shoppinglist.data.source.ShoppingListDataSource
import com.example.hypergenericlistforbuyingstuff.models.ShoppingList

class ShoppingListRepositoryImpl(private val dataSource: ShoppingListDataSource) : ShoppingListRepository {

    override suspend fun getLists(userId: String): Resource<List<ShoppingList>> {
        return try {
            val lists = dataSource.getLists(userId)

            val sortedLists = lists.sortedBy { it.name }

            Resource.Success(sortedLists)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error fetching lists")
        }
    }

    override suspend fun addList(name: String, ownerId: String, imageUri: Uri?): Resource<String> {
        return try {
            val list = ShoppingList(name = name, ownerId = ownerId)

            val id = dataSource.addList(list, imageUri)
            Resource.Success(id)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error adding list")
        }
    }

    override suspend fun updateList(list: ShoppingList, imageUri: Uri?): Resource<Unit> {
        return try {
            dataSource.updateList(list, imageUri)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error updating list")
        }
    }

    override suspend fun deleteList(listId: String): Resource<Unit> {
        return try {
            dataSource.deleteList(listId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error deleting list")
        }
    }

    override suspend fun getListById(listId: String): ShoppingList? {
        return try {
            dataSource.getListById(listId)
        } catch (e: Exception) {
            null
        }
    }
}