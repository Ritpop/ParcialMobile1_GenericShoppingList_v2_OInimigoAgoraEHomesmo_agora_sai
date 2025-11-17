package com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.data

import com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.models.Category
import com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.models.GroupedListItem
import com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.models.ListItem
import com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.models.ShoppingList
import com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.models.User

object DataStore {

    private val users = mutableListOf<User>()
    private val shoppingLists = mutableListOf<ShoppingList>()
    private val listItems = mutableListOf<ListItem>()

    private val categories = mutableListOf<Category>()

    private var currentUserId: Int? = null
    private var nextUserId = 1
    private var nextListId = 1
    private var nextItemId = 1


    init {
        registerUser("Usuário Teste", "teste@mail.com", "passOword")
        val user = login("teste@mail.com", "passOword")
        if (user != null) {
            categories.addAll(
                listOf(
                    Category("Fruta", "🍎"),
                    Category("Verdura", "🥦"),
                    Category("Carne", "🥩"),
                    Category("Laticinios", "🥛"),
                    Category("Padaria", "🍞"),
                    Category("Limpeza", "🧼")
                )
            )

            addShoppingList("ShoppingList", null, user.id)
            addShoppingList("ListDeCompras", null, user.id)

                        addItemToList(1, "Fruta", 6.0, "un", "Fruta")
            addItemToList(1, "Food", 2.0, "kg", "Carne")
            addItemToList(1, "Comida", 4.0, "un", "Fruta")
        }
        logout()
    }


        fun registerUser(name: String, email: String, password: String): User? {
        if (users.any { it.email.equals(email, ignoreCase = true) }) {
            return null         }
        val newUser = User(
            id = nextUserId++,
            name = name,
            email = email,
            passwordHash = password
        )
            users.add(newUser)
        return newUser
    }

    fun login(email: String, password: String): User? {
        val user =
            users.find { it.email.equals(email, ignoreCase = true) && it.passwordHash == password }
        currentUserId = user?.id
        return user
    }

    fun logout() {
        currentUserId = null
    }

    fun getCurrentUser(): User? {
        return users.find { it.id == currentUserId }
    }


        fun getShoppingListsForCurrentUser(): List<ShoppingList> {
        if (currentUserId == null) return emptyList()
        return shoppingLists.filter { it.ownerId == currentUserId }.sortedBy { it.name }
    }

    fun addShoppingList(name: String, imagePath: String?, ownerId: Int): ShoppingList {
        val newList =
            ShoppingList(id = nextListId++, name = name, imagePath = imagePath, ownerId = ownerId)
        shoppingLists.add(newList)
        return newList
    }


    fun deleteShoppingList(listId: Int) {
        shoppingLists.removeAll { it.id == listId }
        listItems.removeAll { it.listId == listId }
    }


    fun restoreShoppingList(list: ShoppingList, items: List<ListItem>) {
        if (!shoppingLists.any { it.id == list.id }) {
            shoppingLists.add(list)
            listItems.addAll(items)
        }
    }
    fun getListById(listId: Int): ShoppingList? {
        return shoppingLists.find { it.id == listId }
    }


    fun updateShoppingList(listId: Int, newName: String, newImagePath: String?) {
        shoppingLists.find { it.id == listId }?.apply {
            name = newName
            imagePath = newImagePath
        }
    }



        fun getGroupedItemsForList(listId: Int): List<GroupedListItem> {
        val groupedItems = mutableListOf<GroupedListItem>()
        val items = getItemsForList(listId)

                val itemsByCategory = items.groupBy { it.category }

                val sortedCategories = itemsByCategory.keys.sorted()

        sortedCategories.forEach { categoryName ->
                        val emoji = getEmojiForCategory(categoryName)
            groupedItems.add(GroupedListItem.Header(categoryName, emoji))

                        itemsByCategory[categoryName]?.forEach { item ->
                groupedItems.add(GroupedListItem.Item(item))
            }
        }
        return groupedItems
    }

    fun getItemsForList(listId: Int): List<ListItem> {
        return listItems
            .filter { it.listId == listId }
            .sortedWith(compareBy({ it.isChecked }, { it.name }))
    }

    fun getItemById(itemId: Int): ListItem? {
        return listItems.find { it.id == itemId }
    }

    fun addItemToList(
        listId: Int,
        name: String,
        quantity: Double,
        unit: String,
        category: String
    ): ListItem {
        val newItem = ListItem(
            id = nextItemId++,
            name = name,
            quantity = quantity,
            unit = unit,
            category = category,
            listId = listId
        )
        listItems.add(newItem)
        return newItem
    }
    fun deleteItem(itemId: Int) {
        listItems.removeAll { it.id == itemId }
    }
    fun updateItem(itemId: Int, name: String, quantity: Double, unit: String, category: String) {
        getItemById(itemId)?.apply {
            this.name = name
            this.quantity = quantity
            this.unit = unit
            this.category = category
        }
    }



        fun restoreItem(item: ListItem) {
        if (!listItems.any { it.id == item.id }) {
            listItems.add(item)
        }
    }

    fun toggleItemChecked(itemId: Int) {
        listItems.find { it.id == itemId }?.let {
            it.isChecked = !it.isChecked
        }
    }

    fun getCategories(): List<Category> {
        return categories.sortedBy { it.name }
    }

    fun addCategory(name: String, emoji: String) {
        if (name.isNotBlank() && emoji.isNotBlank() && !categories.any {
                it.name.equals(
                    name,
                    ignoreCase = true
                )
            }) {
            categories.add(Category(name, emoji))
        }
    }
    fun deleteCategory(categoryName: String) {
        categories.removeAll { it.name.equals(categoryName, ignoreCase = true) }
    }
    fun getEmojiForCategory(categoryName: String): String {
        return categories.find { it.name.equals(categoryName, ignoreCase = true) }?.emoji ?: "🛒"
    }



}