package com.example.hypergenericlistforbuyingstuff.features.shoppinglist.data.repository

import com.example.hypergenericlistforbuyingstuff.core.utils.Resource
import com.example.hypergenericlistforbuyingstuff.features.shoppinglist.data.source.ListItemDataSource
import com.example.hypergenericlistforbuyingstuff.models.ListItem

class ListItemRepository(private val dataSource: ListItemDataSource) {

    suspend fun getItems(listId: String): Resource<List<ListItem>> {
        return try {
            Resource.Success(dataSource.getItems(listId))
        } catch (e: Exception) {
            Resource.Error("Erro ao carregar")
        }
    }

    suspend fun addItem(listId: String, item: ListItem): Resource<String> {
        return try {
            Resource.Success(dataSource.addItem(listId, item))
        } catch (e: Exception) {
            Resource.Error("Erro ao adicionar item")
        }
    }

    suspend fun updateItem(listId: String, item: ListItem): Resource<Unit> {
        return try {
            dataSource.updateItem(listId, item)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Erro ao editar")
        }
    }

    suspend fun deleteItem(listId: String, itemId: String): Resource<Unit> {
        return try {
            dataSource.deleteItem(listId, itemId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Erro ao deletar item")
        }
    }

    suspend fun toggleItemChecked(listId: String, itemId: String, isChecked: Boolean): Resource<Unit> {
        return try {
            dataSource.toggleItemChecked(listId, itemId, isChecked)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Erro ao marcar item")
        }
    }

    suspend fun searchItems(listId: String, query: String): Resource<List<ListItem>> {
        return try {
            Resource.Success(dataSource.searchItems(listId, query))
        } catch (e: Exception) {
            Resource.Error("Erro de busca")
        }
    }
}