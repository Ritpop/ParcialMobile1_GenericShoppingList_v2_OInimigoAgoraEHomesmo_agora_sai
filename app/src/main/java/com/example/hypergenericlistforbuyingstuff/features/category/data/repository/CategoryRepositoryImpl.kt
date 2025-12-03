package com.example.hypergenericlistforbuyingstuff.features.category.data.repository

import com.example.hypergenericlistforbuyingstuff.core.utils.Resource
import com.example.hypergenericlistforbuyingstuff.features.category.data.source.CategoryDataSource
import com.example.hypergenericlistforbuyingstuff.models.Category

class CategoryRepositoryImpl(private val dataSource: CategoryDataSource) : CategoryRepository {

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
            Resource.Error(e.message ?: "Erro ao carrefar cateforias")
        }
    }

    override suspend fun addCategory(name: String, emoji: String): Resource<Unit> {
        return try {
            dataSource.addCategory(Category(name = name, emoji = emoji))
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Não foi possivel adiiconar")
        }
    }

    override suspend fun deleteCategory(categoryId: String): Resource<Unit> {
        return try {
            dataSource.deleteCategory(categoryId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Erro ao deletar")
        }
    }
}