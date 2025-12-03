package com.example.hypergenericlistforbuyingstuff.features.shoppinglist.data.repository

import android.net.Uri
import com.example.hypergenericlistforbuyingstuff.core.utils.Resource
import com.example.hypergenericlistforbuyingstuff.features.shoppinglist.data.source.ShoppingListDataSource
import com.example.hypergenericlistforbuyingstuff.features.shoppinglist.data.source.StorageDataSource
import com.example.hypergenericlistforbuyingstuff.models.Category
import com.example.hypergenericlistforbuyingstuff.models.ShoppingList

class ShoppingListRepositoryImpl(
    private val dataSource: ShoppingListDataSource,
    private val storageDataSource: StorageDataSource
) : ShoppingListRepository {

    override suspend fun getLists(userId: String): Resource<List<ShoppingList>> {
        return try {
            val lists = dataSource.getLists(userId)
            Resource.Success(lists.sortedBy { it.name })
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro ao carregar itens")
        }
    }

    override suspend fun addList(name: String, ownerId: String, imageUri: Uri?): Resource<String> {
        return try {
            var list = ShoppingList(name = name, ownerId = ownerId)
            val id = dataSource.addList(list)

            if (imageUri != null) {
                val imageUrl = storageDataSource.uploadImage(imageUri, id)
                dataSource.updateList(list.copy(id = id, imagePath = imageUrl))
            }
            Resource.Success(id)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro ao adicionar lista")
        }
    }

    override suspend fun updateList(list: ShoppingList, imageUri: Uri?): Resource<Unit> {
        return try {
            var listToUpdate = list
            if (imageUri != null) {
                val imageUrl = storageDataSource.uploadImage(imageUri, list.id)
                listToUpdate = list.copy(imagePath = imageUrl)
            }
            dataSource.updateList(listToUpdate)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro ao atualizar a lista")
        }
    }

    override suspend fun deleteList(listId: String): Resource<Unit> {
        return try {
            storageDataSource.deleteImage(listId)
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

    override suspend fun searchLists(userId: String, query: String): Resource<List<ShoppingList>> {
        return try {
            val lists = dataSource.searchLists(userId, query)
            Resource.Success(lists)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro de busca")
        }
    }

    override suspend fun getCategories(): Resource<List<Category>> {
        return try {
            val categories = dataSource.getCategories()
            if (categories.isEmpty()) {
                val defaults = listOf(
                    Category(name = "Fruta", emoji = "🍎"),
                    Category(name = "Verdura", emoji = "🥦"),
                    Category(name = "Carne", emoji = "🥩"),
                    Category(name = "Padaria", emoji = "🍞"),
                    Category(name = "Bebidas", emoji = "🥤")
                )
                defaults.forEach { dataSource.addCategory(it) }
                Resource.Success(defaults)
            } else {
                Resource.Success(categories)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro ao carregar categorias")
        }
    }

    override suspend fun addCategory(name: String, emoji: String): Resource<Unit> {
        return try {
            dataSource.addCategory(Category(name = name, emoji = emoji))
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Erro ao adicionar categoria")
        }
    }

    override suspend fun deleteCategory(categoryId: String): Resource<Unit> {
        return try {
            dataSource.deleteCategory(categoryId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Erro ao deletar categoria")
        }
    }
}