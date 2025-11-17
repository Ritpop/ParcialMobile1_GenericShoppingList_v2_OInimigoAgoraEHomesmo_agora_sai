package com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.models

data class ShoppingList(
    val id: Int,
    var name: String,
    var imagePath: String? = null,
    val ownerId: Int
)