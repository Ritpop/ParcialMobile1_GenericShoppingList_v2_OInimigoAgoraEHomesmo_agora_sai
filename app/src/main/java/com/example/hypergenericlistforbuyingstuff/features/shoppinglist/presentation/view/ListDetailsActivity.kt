package com.example.hypergenericlistforbuyingstuff.features.shoppinglist.presentation.view

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.hypergenericlistforbuyingstuff.core.utils.Resource
import com.example.hypergenericlistforbuyingstuff.databinding.ActivityListDetailsBinding
import com.example.hypergenericlistforbuyingstuff.features.shoppinglist.presentation.viewmodel.ListDetailsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ListDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListDetailsBinding

    private val viewModel: ListDetailsViewModel by viewModel()

    private var selectedImageUri: Uri? = null
    private var listId: String? = null

    companion object {
        const val EXTRA_LIST_ID = "LIST_ID"
    }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                binding.imageViewPreview.setImageURI(it)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listId = intent.getStringExtra(EXTRA_LIST_ID)

        setupUI()
        setupObservers()

        if (listId != null) {
            viewModel.loadList(listId!!)
        }

        binding.buttonChooseImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.buttonSave.setOnClickListener {
            saveList()
        }
    }

    private fun setupUI() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (listId != null) {
            supportActionBar?.title = "Editar Lista"
            binding.buttonSave.text = "Salvar Alterações"
        } else {
            supportActionBar?.title = "Nova Lista"
            binding.buttonSave.text = "Criar Lista"
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.listData.collect { list ->
                list?.let {
                    binding.editTextListName.setText(it.name)
                    if (!it.imagePath.isNullOrBlank()) {
                        Glide.with(this@ListDetailsActivity)
                            .load(it.imagePath)
                            .centerCrop()
                            .into(binding.imageViewPreview)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.saveState.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        binding.buttonSave.isEnabled = false
                        binding.buttonSave.text = "Salvando..."
                        binding.editTextListName.isEnabled = false
                        binding.buttonChooseImage.isEnabled = false
                    }
                    is Resource.Success -> {
                        binding.buttonSave.isEnabled = true
                        Toast.makeText(this@ListDetailsActivity, "Lista salva com sucesso!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    is Resource.Error -> {
                        binding.buttonSave.isEnabled = true
                        binding.buttonSave.text = if (listId != null) "Salvar Alterações" else "Criar Lista"
                        binding.editTextListName.isEnabled = true
                        binding.buttonChooseImage.isEnabled = true
                        Toast.makeText(this@ListDetailsActivity, "Erro: ${resource.message}", Toast.LENGTH_LONG).show()
                    }
                    null -> {}
                }
            }
        }
    }

    private fun saveList() {
        val name = binding.editTextListName.text.toString().trim()

        if (name.isEmpty()) {
            binding.editTextListName.error = "O nome é obrigatório"
            return
        }

        viewModel.saveList(listId, name, selectedImageUri)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}