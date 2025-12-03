package com.example.hypergenericlistforbuyingstuff.features.category.data.repository

import com.example.hypergenericlistforbuyingstuff.core.utils.Resource
import com.example.hypergenericlistforbuyingstuff.models.Category

interface CategoryRepository {
    suspend fun getCategories(): Resource<List<Category>>
    suspend fun addCategory(name: String, emoji: String): Resource<Unit>
    suspend fun deleteCategory(categoryId: String): Resource<Unit>
}