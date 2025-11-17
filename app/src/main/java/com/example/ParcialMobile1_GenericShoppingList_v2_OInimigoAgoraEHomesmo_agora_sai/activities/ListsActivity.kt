package com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.R
import com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.data.DataStore
import com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.databinding.ActivityListsBinding
import com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.models.ShoppingList
import com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.adapters.ShoppingListAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class ListsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListsBinding
    private lateinit var adapter: ShoppingListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        setupRecyclerView()

        binding.fabAddList.setOnClickListener {
            val intent = Intent(this, ListDetailsActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        updateListsAndCheckEmptyState()
    }

    private fun updateListsAndCheckEmptyState() {
        val lists = DataStore.getShoppingListsForCurrentUser()
        adapter.updateLists(lists)

        if (lists.isEmpty()) {
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
        val options = arrayOf("Editar", "EXcluir")

        MaterialAlertDialogBuilder(this)
            .setTitle(list.name)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {                         val intent = Intent(this, ListDetailsActivity::class.java)
                        intent.putExtra(ListDetailsActivity.EXTRA_LIST_ID, list.id)
                        startActivity(intent)
                    }

                    1 -> {                                                 val listToDelete = list
                        val itemsOfList = DataStore.getItemsForList(list.id)

                                                DataStore.deleteShoppingList(listToDelete.id)
                        updateListsAndCheckEmptyState()

                                                Snackbar.make(
                            binding.root,
                            "'${listToDelete.name}' excluida",
                            Snackbar.LENGTH_LONG
                        )
                            .setAction("Undo") {
                                                                DataStore.restoreShoppingList(listToDelete, itemsOfList)
                                updateListsAndCheckEmptyState()                             }
                            .show()
                    }
                }
            }
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.lists_menu, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val fullList = DataStore.getShoppingListsForCurrentUser()
                val filteredList = if (newText.isNullOrBlank()) {
                    fullList
                } else {
                    fullList.filter { it.name.contains(newText, ignoreCase = true) }
                }
                adapter.updateLists(filteredList)
                if (filteredList.isEmpty()) {

                    binding.recyclerViewLists.visibility = View.GONE
                    binding.emptyStateLayout.visibility = View.VISIBLE
                } else {

                    binding.recyclerViewLists.visibility = View.VISIBLE
                    binding.emptyStateLayout.visibility = View.GONE
                }

                return true
            }
        })
// a testas
                searchView?.setOnCloseListener {
            updateListsAndCheckEmptyState()
            false
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_manage_categories -> {
                val intent = Intent(this, CategoryManagerActivity::class.java)
                startActivity(intent)
                true
            }

            R.id.action_logout -> {
                DataStore.logout()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}