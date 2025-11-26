package com.example.hypergenericlistforbuyingstuff.models

import com.google.firebase.firestore.PropertyName

data class ListItem(
    val id: String = "",
    val name: String = "",
    val nameLower: String = "",
    val quantity: Double = 0.0,
    val unit: String = "",
    val category: String = "",
    @get:PropertyName("isChecked") @set:PropertyName("isChecked") var isChecked: Boolean = false,
    val listId: String = ""
)