package com.example.hypergenericlistforbuyingstuff.features.category.presentation.view

import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hypergenericlistforbuyingstuff.R
import com.example.hypergenericlistforbuyingstuff.features.category.presentation.adapter.CategoryAdapter
import com.example.hypergenericlistforbuyingstuff.databinding.ActivityCategoryManagerBinding
import com.example.hypergenericlistforbuyingstuff.features.category.presentation.viewmodel.CategoryViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class CategoryManagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryManagerBinding
    private lateinit var adapter: CategoryAdapter
    private val viewModel: CategoryViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupRecyclerView()

        viewModel.categories.observe(this) {
            adapter.updateCategories(it)
        }
        viewModel.loadCategories()

        binding.fabAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }
    }

    private fun setupRecyclerView() {
        adapter = CategoryAdapter(emptyList()) { category ->
            MaterialAlertDialogBuilder(this)
                .setTitle("Excluir Categoria")
                .setMessage("Tem certeza de quer excluir '${category.name}'?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Excluir") { _, _ ->
                    viewModel.deleteCategory(category.id)
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
            .setTitle("Nova Categoria")
            .setView(dialogView)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Adicionar") { _, _ ->
                val name = editTextName.text.toString().trim()
                val emoji = editTextEmoji.text.toString().trim()
                if (name.isNotBlank() && emoji.isNotBlank()) {
                    viewModel.addCategory(name, emoji)
                } else {
                    Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}