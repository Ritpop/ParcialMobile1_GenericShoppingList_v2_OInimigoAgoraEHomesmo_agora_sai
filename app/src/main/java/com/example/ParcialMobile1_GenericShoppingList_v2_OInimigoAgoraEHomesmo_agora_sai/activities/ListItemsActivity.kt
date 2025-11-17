package com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.activities
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.R
import com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.data.DataStore
import com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.databinding.ActivityAddItemBinding
import com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.databinding.ActivityListItemsBinding
import com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.models.Category
import com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.models.GroupedListItem
import com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.models.ListItem
import com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.adapters.CategorySpinnerAdapter
import com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.adapters.ListItemAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class ListItemsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListItemsBinding
    private lateinit var adapter: ListItemAdapter
    private var listId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listId = intent.getIntExtra("LIST_ID", -1)
        if (listId == -1) {
            Toast.makeText(this, "Erro: ID da Lista não Encontrado", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupRecyclerView()

        binding.fabAddItem.setOnClickListener {
            showItemDialog(null)
        }
    }

    override fun onResume() {
        super.onResume()
        updateDataAndCheckEmptyState()
    }

    private fun updateDataAndCheckEmptyState() {
        val currentList = DataStore.getListById(listId)
        if (currentList == null) {
            finish()
            return
        }
        binding.toolbar.title = currentList.name

        val groupedItems = DataStore.getGroupedItemsForList(listId)
        adapter.updateItems(groupedItems)

                if (groupedItems.isEmpty()) {
            binding.recyclerViewItems.visibility = View.GONE
            binding.emptyStateLayoutItems.visibility = View.VISIBLE
        } else {
            binding.recyclerViewItems.visibility = View.VISIBLE
            binding.emptyStateLayoutItems.visibility = View.GONE
        }
    }

    private fun setupRecyclerView() {
        adapter = ListItemAdapter(
            emptyList(),
            onCheckboxClick = { clickedItem ->
                DataStore.toggleItemChecked(clickedItem.id)
                updateDataAndCheckEmptyState()
            },
            onItemLongClick = { clickedItem ->
                showItemOptionsDialog(clickedItem)
            }
        )
        binding.recyclerViewItems.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewItems.adapter = adapter
    }

    private fun showItemOptionsDialog(item: ListItem) {
        val options = arrayOf("Editar", "Excluir")

        MaterialAlertDialogBuilder(this)
            .setTitle(item.name)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showItemDialog(item)
                    1 -> {
                        val itemToDelete = item
                        DataStore.deleteItem(itemToDelete.id)
                        updateDataAndCheckEmptyState()

                        Snackbar.make(
                            binding.root,
                            "${itemToDelete.name} excluído",
                            Snackbar.LENGTH_LONG
                        )
                            .setAction("Undo") {
                                DataStore.restoreItem(itemToDelete)
                                updateDataAndCheckEmptyState()
                            }
                            .show()
                    }
                }
            }
            .show()
    }

    private fun showItemDialog(item: ListItem?) {
        val dialogBinding = ActivityAddItemBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(this)
        builder.setView(dialogBinding.root)

        val isEditing = item != null

        val titleView = dialogBinding.root.findViewById<TextView>(R.id.textViewTitle)
        titleView.text = if (isEditing) "Editar Item" else "Adicionar Item"

        dialogBinding.buttonAddItem.text = if (isEditing) "Salvar" else "Adicionar"

        val unitAdapter = ArrayAdapter.createFromResource(
            this, R.array.units_array, android.R.layout.simple_spinner_item
        )
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogBinding.spinnerUnit.adapter = unitAdapter

        val categories = DataStore.getCategories()
        val categoryAdapter = CategorySpinnerAdapter(this, categories)
        dialogBinding.spinnerCategory.adapter = categoryAdapter

        if (item != null) {
            dialogBinding.editTextItemName.setText(item.name)
            dialogBinding.editTextQuantity.setText(item.quantity.toString())

            val unitPosition = resources.getStringArray(R.array.units_array).indexOf(item.unit)
            dialogBinding.spinnerUnit.setSelection(if (unitPosition >= 0) unitPosition else 0)

            val categoryPosition = categories.indexOfFirst { it.name == item.category }
            dialogBinding.spinnerCategory.setSelection(if (categoryPosition >= 0) categoryPosition else 0)
        }

        val dialog = builder.create()

        dialogBinding.buttonAddItem.setOnClickListener {
            val name = dialogBinding.editTextItemName.text.toString().trim()
            val quantityStr = dialogBinding.editTextQuantity.text.toString().trim()
            val unit = dialogBinding.spinnerUnit.selectedItem.toString()
            val selectedCategory = dialogBinding.spinnerCategory.selectedItem as Category
            val categoryName = selectedCategory.name

            dialogBinding.textInputLayoutItemName.error = null
            dialogBinding.textInputLayoutQuantity.error = null

            var hasError = false
            if (name.isBlank()) {
                dialogBinding.textInputLayoutItemName.error = "O nome pode ser vazio"
                hasError = true
            }

            if (quantityStr.isBlank()) {
                dialogBinding.textInputLayoutQuantity.error = "A quantidade é obrigatoria"
                hasError = true
            }

            val quantity = quantityStr.toDoubleOrNull()
            if (quantityStr.isNotBlank() && quantity == null) {
                dialogBinding.textInputLayoutQuantity.error = "Valor invalido"
                hasError = true
            }

            if (hasError) {
                return@setOnClickListener
            }

            if (item != null) {
                DataStore.updateItem(item.id, name, quantity!!, unit, categoryName)
                Snackbar.make(binding.root, "Item salvo", Snackbar.LENGTH_SHORT).show()
            } else {
                DataStore.addItemToList(listId, name, quantity!!, unit, categoryName)
                Snackbar.make(binding.root, "Item adicionado", Snackbar.LENGTH_SHORT).show()
            }

            updateDataAndCheckEmptyState()
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_items_menu, menu)
        val searchItem = menu?.findItem(R.id.action_search_items)
        val searchView = searchItem?.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val fullList = DataStore.getGroupedItemsForList(listId)
                val filteredList = if (newText.isNullOrBlank()) {
                    fullList
                } else {
                    val result = mutableListOf<GroupedListItem>()
                    var currentHeader: GroupedListItem.Header? = null

                    fullList.forEach { groupedItem ->
                        when (groupedItem) {
                            is GroupedListItem.Header -> currentHeader = groupedItem
                            is GroupedListItem.Item -> {
                                if (groupedItem.listItem.name.contains(
                                        newText,
                                        ignoreCase = true
                                    )
                                ) {
                                    if (currentHeader != null && !result.contains(currentHeader)) {
                                        result.add(currentHeader!!)
                                    }
                                    result.add(groupedItem)
                                }
                            }
                        }
                    }
                    result
                }
                adapter.updateItems(filteredList)

                                if (filteredList.isEmpty()) {
                    binding.recyclerViewItems.visibility = View.GONE
                    binding.emptyStateLayoutItems.visibility = View.VISIBLE
                } else {
                    binding.recyclerViewItems.visibility = View.VISIBLE
                    binding.emptyStateLayoutItems.visibility = View.GONE
                }

                return true
            }
        })

        searchView?.setOnCloseListener {
            updateDataAndCheckEmptyState()
            false
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}