package com.example.hypergenericlistforbuyingstuff.features.shoppinglist.data.source

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class StorageDataSource(private val storage: FirebaseStorage) {

    suspend fun uploadImage(uri: Uri, listId: String): String {
        val ref = storage.reference.child("list_images/$listId.jpg")
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }

    suspend fun deleteImage(listId: String) {
        try {
            storage.reference.child("list_images/$listId.jpg").delete().await()
        } catch (e: Exception) {
        }
    }
}