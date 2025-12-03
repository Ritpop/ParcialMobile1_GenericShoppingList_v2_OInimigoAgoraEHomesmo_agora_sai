package com.example.hypergenericlistforbuyingstuff.features.shoppinglist.data.source

import android.net.Uri

interface StorageDataSource {
    suspend fun uploadImage(uri: Uri, listId: String): String
    suspend fun deleteImage(listId: String)
}