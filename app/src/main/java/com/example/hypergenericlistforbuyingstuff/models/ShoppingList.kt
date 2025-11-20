package com.example.hypergenericlistforbuyingstuff.models

data class ShoppingList(
    val id: Int,
    var name: String,
    var imagePath: String? = null,
    val ownerId: Int
)