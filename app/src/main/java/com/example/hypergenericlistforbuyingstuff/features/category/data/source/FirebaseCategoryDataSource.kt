package com.example.hypergenericlistforbuyingstuff.features.category.data.source

import com.example.hypergenericlistforbuyingstuff.models.Category
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseCategoryDataSource(private val firestore: FirebaseFirestore) : CategoryDataSource {

    override suspend fun getCategories(): List<Category> {
        val snapshot = firestore.collection("categories").orderBy("name").get().await()
        return snapshot.toObjects(Category::class.java)
    }

    override suspend fun addCategory(category: Category) {
        val id = firestore.collection("categories").document().id
        val toSave = category.copy(id = id)
        firestore.collection("categories").document(id).set(toSave).await()
    }

    override suspend fun deleteCategory(categoryId: String) {
        firestore.collection("categories").document(categoryId).delete().await()
    }
}