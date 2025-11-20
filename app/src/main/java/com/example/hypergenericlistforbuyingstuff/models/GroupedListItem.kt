package com.example.hypergenericlistforbuyingstuff.models

sealed class GroupedListItem {
    data class Header(val categoryName: String, val emoji: String) : GroupedListItem()
    data class Item(val listItem: ListItem) : GroupedListItem()
}