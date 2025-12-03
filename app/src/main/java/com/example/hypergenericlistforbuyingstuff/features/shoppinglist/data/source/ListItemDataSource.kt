package com.example.hypergenericlistforbuyingstuff.features.shoppinglist.data.source

import com.example.hypergenericlistforbuyingstuff.models.ListItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ListItemDataSource(private val firestore: FirebaseFirestore) {

    private fun getCollection(listId: String) =
        firestore.collection("shopping_lists").document(listId).collection("items")

    suspend fun getItems(listId: String): List<ListItem> {
        return getCollection(listId).get().await().toObjects(ListItem::class.java)
    }

    suspend fun addItem(listId: String, item: ListItem): String {
        val newItemId = getCollection(listId).document().id
        val itemToSave = item.copy(
            id = newItemId,
            listId = listId,
            nameLower = item.name.lowercase()
        )
        getCollection(listId).document(newItemId).set(itemToSave).await()
        return newItemId
    }

    suspend fun updateItem(listId: String, item: ListItem) {
        val itemToUpdate = item.copy(nameLower = item.name.lowercase())
        getCollection(listId).document(item.id).set(itemToUpdate).await()
    }

    suspend fun deleteItem(listId: String, itemId: String) {
        getCollection(listId).document(itemId).delete().await()
    }

    suspend fun toggleItemChecked(listId: String, itemId: String, isChecked: Boolean) {
        getCollection(listId).document(itemId).update("isChecked", isChecked).await()
    }

    suspend fun searchItems(listId: String, query: String): List<ListItem> {
        val queryLower = query.lowercase()
        val snapshot = getCollection(listId)
            .whereGreaterThanOrEqualTo("nameLower", queryLower)
            .whereLessThanOrEqualTo("nameLower", queryLower + "\uf8ff")
            .get()
            .await()
        return snapshot.toObjects(ListItem::class.java)
    }
}