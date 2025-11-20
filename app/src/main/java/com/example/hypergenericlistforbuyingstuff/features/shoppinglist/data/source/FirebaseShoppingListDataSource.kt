package com.example.hypergenericlistforbuyingstuff.features.shoppinglist.data.source

import android.net.Uri
import com.example.hypergenericlistforbuyingstuff.models.ShoppingList
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseShoppingListDataSource(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ShoppingListDataSource {

    private val collection = firestore.collection("shopping_lists")

    override suspend fun getLists(userId: String): List<ShoppingList> {
        val snapshot = collection
            .whereEqualTo("ownerId", userId)
            .get()
            .await()
        return snapshot.toObjects(ShoppingList::class.java)
    }

    override suspend fun addList(list: ShoppingList, imageUri: Uri?): String {
        val newListId = collection.document().id
        var imageUrl: String? = null

        if (imageUri != null) {
            val ref = storage.reference.child("list_images/$newListId.jpg")
            ref.putFile(imageUri).await()
            imageUrl = ref.downloadUrl.await().toString()
        }

        val listToSave = list.copy(id = newListId, imagePath = imageUrl)
        collection.document(newListId).set(listToSave).await()
        return newListId
    }

    override suspend fun updateList(list: ShoppingList, imageUri: Uri?) {
        var imageUrl = list.imagePath

        if (imageUri != null) {

            val ref = storage.reference.child("list_images/${list.id}.jpg")

            ref.putFile(imageUri).await()
            imageUrl = ref.downloadUrl.await().toString()
        }

        val updates = mapOf(
            "name" to list.name,
            "imagePath" to imageUrl
        )
        collection.document(list.id).update(updates).await()
    }

    override suspend fun deleteList(listId: String) {
        try {
            storage.reference.child("list_images/$listId.jpg").delete().await()
        } catch (e: Exception) {
        }
        collection.document(listId).delete().await()

    }

    override suspend fun getListById(listId: String): ShoppingList? {
        val doc = collection.document(listId).get().await()
        return doc.toObject(ShoppingList::class.java)
    }
}