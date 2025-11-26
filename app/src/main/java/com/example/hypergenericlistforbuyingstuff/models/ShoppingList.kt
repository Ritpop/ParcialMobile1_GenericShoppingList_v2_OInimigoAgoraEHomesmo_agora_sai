package com.example.hypergenericlistforbuyingstuff.models

data class ShoppingList(
    val id: String = "",
    val name: String = "",
    val nameLower: String = "",
    val imagePath: String? = null,
    val ownerId: String = ""
)