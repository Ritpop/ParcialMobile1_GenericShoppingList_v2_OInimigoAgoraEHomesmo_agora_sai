package com.example.hypergenericlistforbuyingstuff.features.shoppinglist.data.source

import com.example.hypergenericlistforbuyingstuff.models.Category
import com.example.hypergenericlistforbuyingstuff.models.ShoppingList
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseShoppingListDataSource(
    private val firestore: FirebaseFirestore
) : ShoppingListDataSource {

    private val collection = firestore.collection("shopping_lists")

    override suspend fun getLists(userId: String): List<ShoppingList> {
        val snapshot = collection.whereEqualTo("ownerId", userId).get().await()
        return snapshot.toObjects(ShoppingList::class.java)
    }

    override suspend fun addList(list: ShoppingList): String {
        val newListId = collection.document().id
        val listToSave = list.copy(
            id = newListId,
            nameLower = list.name.lowercase()
        )
        collection.document(newListId).set(listToSave).await()
        return newListId
    }

    override suspend fun updateList(list: ShoppingList) {
        val updates = mapOf(
            "name" to list.name,
            "nameLower" to list.name.lowercase(),
            "imagePath" to list.imagePath
        )
        collection.document(list.id).update(updates).await()
    }

    override suspend fun deleteList(listId: String) {
        collection.document(listId).delete().await()
    }

    override suspend fun getListById(listId: String): ShoppingList? {
        val doc = collection.document(listId).get().await()
        return doc.toObject(ShoppingList::class.java)
    }

    override suspend fun searchLists(userId: String, query: String): List<ShoppingList> {
        val queryLower = query.lowercase()
        val snapshot = collection
            .whereEqualTo("ownerId", userId)
            .whereGreaterThanOrEqualTo("nameLower", queryLower)
            .whereLessThanOrEqualTo("nameLower", queryLower + "\uf8ff")
            .get()
            .await()
        return snapshot.toObjects(ShoppingList::class.java)
    }

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