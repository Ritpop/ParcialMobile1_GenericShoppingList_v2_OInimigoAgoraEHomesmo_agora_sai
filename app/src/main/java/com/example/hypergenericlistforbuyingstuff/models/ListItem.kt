package com.example.hypergenericlistforbuyingstuff.models

data class ListItem(
    val id: Int,
    var name: String,
    var quantity: Double,
    var unit: String,
    var category: String,
    var isChecked: Boolean = false,
    val listId: Int
)