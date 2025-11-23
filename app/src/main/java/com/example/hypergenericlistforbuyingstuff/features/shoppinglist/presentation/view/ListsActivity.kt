package com.example.hypergenericlistforbuyingstuff.features.shoppinglist.presentation.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.example.hypergenericlistforbuyingstuff.R
import com.example.hypergenericlistforbuyingstuff.adapters.ShoppingListAdapter
import com.example.hypergenericlistforbuyingstuff.core.utils.Resource
import com.example.hypergenericlistforbuyingstuff.databinding.ActivityListsBinding
import com.example.hypergenericlistforbuyingstuff.features.auth.presentation.view.LoginActivity
import com.example.hypergenericlistforbuyingstuff.features.shoppinglist.presentation.viewmodel.ListsViewModel
import com.example.hypergenericlistforbuyingstuff.models.ShoppingList
import com.example.hypergenericlistforbuyingstuff.features.shoppinglist.presentation.view.ListItemsActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class ListsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListsBinding
    private lateinit var adapter: ShoppingListAdapter

    private val viewModel: ListsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        setupRecyclerView()
        setupObservers()

        binding.fabAddList.setOnClickListener {
            val intent = Intent(this, ListDetailsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadLists()
    }

    private fun setupObservers() {
        viewModel.listsState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.emptyStateLayout.visibility = View.GONE
                }
                is Resource.Success -> {
                    val lists = resource.data
                    adapter.updateLists(lists)
                    toggleEmptyState(lists.isEmpty())
                }
                is Resource.Error -> {
                    Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.deleteState.observe(this) { resource ->
            when (resource) {
                is Resource.Success -> {
                    Snackbar.make(binding.root, "Lista excluída com sucesso", Snackbar.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    Snackbar.make(binding.root, "Erro ao excluir: ${resource.message}", Snackbar.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }

    private fun toggleEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.recyclerViewLists.visibility = View.GONE
            binding.emptyStateLayout.visibility = View.VISIBLE
        } else {
            binding.recyclerViewLists.visibility = View.VISIBLE
            binding.emptyStateLayout.visibility = View.GONE
        }
    }

    private fun setupRecyclerView() {
        adapter = ShoppingListAdapter(
            emptyList(),
            onItemClick = { clickedList ->
                val intent = Intent(this, ListItemsActivity::class.java)
                intent.putExtra("LIST_ID", clickedList.id)
                intent.putExtra("LIST_NAME", clickedList.name)
                startActivity(intent)
            },
            onItemLongClick = { clickedList ->
                showListOptionsDialog(clickedList)
            }
        )
        binding.recyclerViewLists.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerViewLists.adapter = adapter
    }

    private fun showListOptionsDialog(list: ShoppingList) {
        val options = arrayOf("Editar", "Excluir")

        MaterialAlertDialogBuilder(this)
            .setTitle(list.name)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> { // Editar
                        val intent = Intent(this, ListDetailsActivity::class.java)
                        intent.putExtra(ListDetailsActivity.EXTRA_LIST_ID, list.id)
                        startActivity(intent)
                    }
                    1 -> { // Excluir
                        showDeleteConfirmation(list)
                    }
                }
            }
            .show()
    }

    private fun showDeleteConfirmation(list: ShoppingList) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Excluir ${list.name}?")
            .setMessage("Essa ação não pode ser desfeita e apagará todos os itens da lista.")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Excluir") { _, _ ->
                viewModel.deleteList(list.id)
            }
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.lists_menu, menu)

        // Configuração básica da busca local (filtragem na lista já carregada)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val currentList = (viewModel.listsState.value as? Resource.Success)?.data ?: emptyList()

                val filteredList = if (newText.isNullOrBlank()) {
                    currentList
                } else {
                    currentList.filter { it.name.contains(newText, ignoreCase = true) }
                }

                adapter.updateLists(filteredList)
                toggleEmptyState(filteredList.isEmpty())
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_manage_categories -> {
                Toast.makeText(this, "Gerenciamento de categorias em manutenção", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_logout -> {
                viewModel.logout()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}