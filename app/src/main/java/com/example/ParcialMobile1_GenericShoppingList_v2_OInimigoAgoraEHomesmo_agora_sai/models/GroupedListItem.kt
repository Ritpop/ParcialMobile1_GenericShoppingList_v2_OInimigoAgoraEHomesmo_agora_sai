package com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.models

sealed class GroupedListItem {
    data class Header(val categoryName: String, val emoji: String) : GroupedListItem()
    data class Item(val listItem: ListItem) : GroupedListItem()
}