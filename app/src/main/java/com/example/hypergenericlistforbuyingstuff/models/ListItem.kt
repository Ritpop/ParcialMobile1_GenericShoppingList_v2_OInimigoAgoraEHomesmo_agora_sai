package com.example.hypergenericlistforbuyingstuff.models

data class ListItem(
    val id: String = "",
    val name: String = "",
    val quantity: Double = 0.0,
    val unit: String = "",
    val category: String = "",
    val isChecked: Boolean = false,
    val listId: String = ""
)
