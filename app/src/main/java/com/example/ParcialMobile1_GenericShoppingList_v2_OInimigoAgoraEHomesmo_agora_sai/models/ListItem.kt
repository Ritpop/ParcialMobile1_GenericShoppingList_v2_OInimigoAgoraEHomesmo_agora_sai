package com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.models

data class ListItem(
    val id: Int,
    var name: String,
    var quantity: Double,
    var unit: String,
    var category: String,
    var isChecked: Boolean = false,
    val listId: Int
)