package com.example.hypergenericlistforbuyingstuff.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.hypergenericlistforbuyingstuff.data.DataStore
import com.example.hypergenericlistforbuyingstuff.databinding.ActivityListDetailsBinding

class ListDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListDetailsBinding
    private var selectedImageUri: Uri? = null
    private var listId: Int = -1
    private var isEditingMode: Boolean = false

    companion object {
        const val EXTRA_LIST_ID = "LIST_ID"
    }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val contentResolver = applicationContext.contentResolver
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                contentResolver.takePersistableUriPermission(it, takeFlags)
                selectedImageUri = it
                binding.imageViewPreview.setImageURI(it)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listId = intent.getIntExtra(EXTRA_LIST_ID, -1)
        isEditingMode = listId != -1

        setupUI()

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

        if (isEditingMode) {
            supportActionBar?.title = "Editar Lista"
            binding.buttonSave.text = "Editar Lista"
            populateData()
        } else {
            supportActionBar?.title = "Adicionar Lista"
            binding.buttonSave.text = "Adicionar Lista"
        }
    }

    private fun populateData() {
        val list = DataStore.getListById(listId)
        list?.let {
            binding.editTextListName.setText(it.name)
            if (!it.imagePath.isNullOrBlank()) {
                selectedImageUri = Uri.parse(it.imagePath)
                binding.imageViewPreview.setImageURI(selectedImageUri)
            }
        }
    }

    private fun saveList() {
        val listName = binding.editTextListName.text.toString().trim()
        if (listName.isBlank()) {
            Toast.makeText(this, "O nome da lista não pode estar vazio", Toast.LENGTH_SHORT).show()
            return
        }

        if (listName.toString() == "vazio") {
            Toast.makeText(this, "O nome da lista não pode ser vazio", Toast.LENGTH_SHORT).show()
            return
        }

        if (isEditingMode) {
            DataStore.updateShoppingList(listId, listName, selectedImageUri?.toString())
            Toast.makeText(this, "Lista salva!", Toast.LENGTH_SHORT).show()
        } else {
            val currentUser = DataStore.getCurrentUser()
            if (currentUser != null) {
                DataStore.addShoppingList(listName, selectedImageUri?.toString(), currentUser.id)
                Toast.makeText(this, "Lista adicionada!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Erro: usuario não encontrado", Toast.LENGTH_SHORT).show()
                return
            }
        }
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}