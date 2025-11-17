package com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.R
import com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.data.DataStore
import com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.databinding.ActivityCategoryManagerBinding
import com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.adapters.CategoryAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class CategoryManagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryManagerBinding
    private lateinit var adapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

                setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Gerenciar Categorias"

        setupRecyclerView()

        binding.fabAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }
    }

    private fun setupRecyclerView() {
        adapter = CategoryAdapter(DataStore.getCategories()) { category ->
                        MaterialAlertDialogBuilder(this)
                .setTitle("Excluir Categoria")
                .setMessage("Tem certeza de quer excluir a categoria '${category.name}'?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Excluir") { _, _ ->
                    DataStore.deleteCategory(category.name)
                    adapter.updateCategories(DataStore.getCategories())
                    Snackbar.make(binding.root, "Categoria excluida", Snackbar.LENGTH_SHORT).show()
                }
                .show()
        }
        binding.recyclerViewCategories.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewCategories.adapter = adapter
    }

    private fun showAddCategoryDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_category, null)
        val editTextName = dialogView.findViewById<EditText>(R.id.editTextNewCategoryName)
        val editTextEmoji = dialogView.findViewById<EditText>(R.id.editTextNewCategoryEmoji)

        MaterialAlertDialogBuilder(this)
            .setTitle("Nova Category")
            .setView(dialogView)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Adicionar") { _, _ ->
                val name = editTextName.text.toString().trim()
                val emoji = editTextEmoji.text.toString().trim()
                if (name.isNotBlank() && emoji.isNotBlank()) {
                    DataStore.addCategory(name, emoji)
                    adapter.updateCategories(DataStore.getCategories())
                } else {
                    Toast.makeText(this, "Preencha com nome e o emoji (logo)", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}