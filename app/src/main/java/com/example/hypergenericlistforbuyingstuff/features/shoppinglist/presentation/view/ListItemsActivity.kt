package com.example.hypergenericlistforbuyingstuff.features.shoppinglist.presentation.view

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
import com.example.hypergenericlistforbuyingstuff.R
import com.example.hypergenericlistforbuyingstuff.adapters.CategorySpinnerAdapter
import com.example.hypergenericlistforbuyingstuff.adapters.ListItemAdapter
import com.example.hypergenericlistforbuyingstuff.core.utils.Resource
import com.example.hypergenericlistforbuyingstuff.databinding.ActivityAddItemBinding
import com.example.hypergenericlistforbuyingstuff.databinding.ActivityListItemsBinding
import com.example.hypergenericlistforbuyingstuff.features.shoppinglist.presentation.viewmodel.ListItemsViewModel
import com.example.hypergenericlistforbuyingstuff.models.Category
import com.example.hypergenericlistforbuyingstuff.models.GroupedListItem
import com.example.hypergenericlistforbuyingstuff.models.ListItem
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class ListItemsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListItemsBinding
    private lateinit var adapter: ListItemAdapter

    private val viewModel: ListItemsViewModel by viewModel()

    private var listId: String? = null
    private var listName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listId = intent.getStringExtra("LIST_ID")
        listName = intent.getStringExtra("LIST_NAME")

        if (listId == null) {
            Toast.makeText(this, "Erro: ID da Lista não Encontrado", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupUI()
        setupRecyclerView()
        setupObservers()

        viewModel.loadItems(listId!!)

        binding.fabAddItem.setOnClickListener {
            showItemDialog(null)
        }
    }

    private fun setupUI() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = listName ?: "Lista de Compras"
    }

    private fun setupObservers() {
        viewModel.itemsState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    val items = resource.data
                    adapter.updateItems(items)
                    toggleEmptyState(items.isEmpty())
                }
                is Resource.Error -> {
                    Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.operationState.observe(this) { resource ->
            if (resource is Resource.Error) {
                Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun toggleEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
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
                viewModel.toggleItemChecked(listId!!, clickedItem)
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
                        viewModel.deleteItem(listId!!, item.id)
                        Snackbar.make(binding.root, "Item excluído", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
            .show()
    }

    private fun showItemDialog(item: ListItem?) {
        val dialogBinding = ActivityAddItemBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)

        val isEditing = item != null
        dialogBinding.textViewTitle.text = if (isEditing) "Editar Item" else "Adicionar Item"
        dialogBinding.buttonAddItem.text = if (isEditing) "Salvar" else "Adicionar"


        val categories = listOf(
            Category("Fruta", "🍎"), Category("Verdura", "🥦"), Category("Carne", "🥩"),
            Category("Laticínios", "🥛"), Category("Padaria", "🍞"), Category("Bebidas", "🥤"),
            Category("Limpeza", "🧼"), Category("Higiene", "🪥"), Category("Outros", "📦")
        )

        val unitAdapter = ArrayAdapter.createFromResource(this, R.array.units_array, android.R.layout.simple_spinner_item)
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogBinding.spinnerUnit.adapter = unitAdapter

        val categoryAdapter = CategorySpinnerAdapter(this, categories)
        dialogBinding.spinnerCategory.adapter = categoryAdapter

        if (item != null) {
            dialogBinding.editTextItemName.setText(item.name)
            dialogBinding.editTextQuantity.setText(item.quantity.toString())

            val units = resources.getStringArray(R.array.units_array)
            val unitIndex = units.indexOf(item.unit).coerceAtLeast(0)
            dialogBinding.spinnerUnit.setSelection(unitIndex)

            val catIndex = categories.indexOfFirst { it.name == item.category }.coerceAtLeast(0)
            dialogBinding.spinnerCategory.setSelection(catIndex)
        }

        val dialog = builder.create()

        dialogBinding.buttonAddItem.setOnClickListener {
            val name = dialogBinding.editTextItemName.text.toString().trim()
            val quantityStr = dialogBinding.editTextQuantity.text.toString().trim()

            if (name.isBlank() || quantityStr.isBlank()) {
                Toast.makeText(this, "Preencha nome e quantidade", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val quantity = quantityStr.toDoubleOrNull() ?: 0.0
            val unit = dialogBinding.spinnerUnit.selectedItem.toString()
            val category = (dialogBinding.spinnerCategory.selectedItem as Category).name

            if (isEditing) {
                val updatedItem = item!!.copy(name = name, quantity = quantity, unit = unit, category = category)
                viewModel.updateItem(listId!!, updatedItem)
            } else {
                viewModel.addItem(listId!!, name, quantity, unit, category)
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_items_menu, menu)
        val searchItem = menu?.findItem(R.id.action_search_items)
        val searchView = searchItem?.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.searchItems(listId!!, query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    viewModel.loadItems(listId!!)
                }
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}