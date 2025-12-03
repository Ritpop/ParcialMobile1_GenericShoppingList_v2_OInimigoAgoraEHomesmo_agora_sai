package com.example.hypergenericlistforbuyingstuff.features.listitem.data.repository

import com.example.hypergenericlistforbuyingstuff.core.utils.Resource
import com.example.hypergenericlistforbuyingstuff.features.listitem.data.source.ListItemDataSource
import com.example.hypergenericlistforbuyingstuff.models.ListItem

class ListItemRepositoryImpl(private val dataSource: ListItemDataSource) : ListItemRepository {

    override suspend fun getItems(listId: String): Resource<List<ListItem>> {
        return try {
            Resource.Success(dataSource.getItems(listId))
        } catch (e: Exception) {
            Resource.Error("Erro ao carregar")
        }
    }

    override suspend fun addItem(listId: String, item: ListItem): Resource<String> {
        return try {
            Resource.Success(dataSource.addItem(listId, item))
        } catch (e: Exception) {
            Resource.Error("Não foi possivel adicionar item.")
        }
    }

    override suspend fun updateItem(listId: String, item: ListItem): Resource<Unit> {
        return try {
            dataSource.updateItem(listId, item)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Erro ao editar.")
        }
    }

    override suspend fun deleteItem(listId: String, itemId: String): Resource<Unit> {
        return try {
            dataSource.deleteItem(listId, itemId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("erro ao deletar")
        }
    }

    override suspend fun toggleItemChecked(listId: String, itemId: String, isChecked: Boolean): Resource<Unit> {
        return try {
            dataSource.toggleItemChecked(listId, itemId, isChecked)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("erro ao marcar iten")
        }
    }

    override suspend fun searchItems(listId: String, query: String): Resource<List<ListItem>> {
        return try {
            Resource.Success(dataSource.searchItems(listId, query))
        } catch (e: Exception) {
            Resource.Error("Erro busca")
        }
    }
}