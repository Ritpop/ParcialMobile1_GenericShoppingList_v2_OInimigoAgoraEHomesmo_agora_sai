package com.example.hypergenericlistforbuyingstuff.features.category.data.source

import com.example.hypergenericlistforbuyingstuff.models.Category

interface CategoryDataSource {
    suspend fun getCategories(): List<Category>
    suspend fun addCategory(category: Category)
    suspend fun deleteCategory(categoryId: String)
}