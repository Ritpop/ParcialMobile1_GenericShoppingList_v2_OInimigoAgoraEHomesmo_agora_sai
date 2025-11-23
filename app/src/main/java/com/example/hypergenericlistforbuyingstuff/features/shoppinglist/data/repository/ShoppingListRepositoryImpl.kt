package com.example.hypergenericlistforbuyingstuff.features.shoppinglist.data.repository

import android.net.Uri
import com.example.hypergenericlistforbuyingstuff.core.utils.Resource
import com.example.hypergenericlistforbuyingstuff.features.shoppinglist.data.source.ShoppingListDataSource
import com.example.hypergenericlistforbuyingstuff.models.ListItem
import com.example.hypergenericlistforbuyingstuff.models.ShoppingList

class ShoppingListRepositoryImpl(private val dataSource: ShoppingListDataSource) : ShoppingListRepository {

    override suspend fun getLists(userId: String): Resource<List<ShoppingList>> {
        return try {
            val lists = dataSource.getLists(userId)
            Resource.Success(lists.sortedBy { it.name })
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro ao carregar listas")
        }
    }

    override suspend fun addList(name: String, ownerId: String, imageUri: Uri?): Resource<String> {
        return try {
            val list = ShoppingList(name = name, ownerId = ownerId)
            val id = dataSource.addList(list, imageUri)
            Resource.Success(id)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro ao adicionar listas")
        }
    }

    override suspend fun updateList(list: ShoppingList, imageUri: Uri?): Resource<Unit> {
        return try {
            dataSource.updateList(list, imageUri)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro ao atualizar lista")
        }
    }

    override suspend fun deleteList(listId: String): Resource<Unit> {
        return try {
            dataSource.deleteList(listId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro ao deletar lista")
        }
    }

    override suspend fun getListById(listId: String): ShoppingList? {
        return try {
            dataSource.getListById(listId)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getItems(listId: String): Resource<List<ListItem>> {
        return try {
            val items = dataSource.getItems(listId)
            Resource.Success(items)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erron ao carregar itens")
        }
    }

    override suspend fun addItem(listId: String, item: ListItem): Resource<String> {
        return try {
            val id = dataSource.addItem(listId, item)
            Resource.Success(id)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro ao adicionar item")
        }
    }

    override suspend fun updateItem(listId: String, item: ListItem): Resource<Unit> {
        return try {
            dataSource.updateItem(listId, item)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro ao atualizar item")
        }
    }

    override suspend fun deleteItem(listId: String, itemId: String): Resource<Unit> {
        return try {
            dataSource.deleteItem(listId, itemId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro ao deletar item")
        }
    }

    override suspend fun toggleItemChecked(listId: String, itemId: String, isChecked: Boolean): Resource<Unit> {
        return try {
            dataSource.toggleItemChecked(listId, itemId, isChecked)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro ao marcaar")
        }
    }
    override suspend fun searchLists(userId: String, query: String): Resource<List<ShoppingList>> {
        return try {
            val lists = dataSource.searchLists(userId, query)
            Resource.Success(lists)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro de busca")
        }
    }

    override suspend fun searchItems(listId: String, query: String): Resource<List<ListItem>> {
        return try {
            val items = dataSource.searchItems(listId, query)
            Resource.Success(items)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro de busca")
        }
    }
}