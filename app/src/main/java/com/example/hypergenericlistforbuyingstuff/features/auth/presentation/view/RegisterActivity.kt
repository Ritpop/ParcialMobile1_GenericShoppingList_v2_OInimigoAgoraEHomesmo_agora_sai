package com.example.hypergenericlistforbuyingstuff.features.auth.presentation.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hypergenericlistforbuyingstuff.core.utils.Resource
import com.example.hypergenericlistforbuyingstuff.databinding.ActivityRegisterBinding
import com.example.hypergenericlistforbuyingstuff.features.auth.presentation.viewmodel.RegisterViewModel
import com.example.hypergenericlistforbuyingstuff.features.shoppinglist.presentation.view.ListsActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val viewModel: RegisterViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()

        binding.buttonCreate.setOnClickListener {
            val name = binding.editTextName.text.toString().trim()
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            val confirmPassword = binding.editTextConfirmPassword.text.toString().trim()

            if (name.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Senhas não conferem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.register(name, email, password)
        }
    }

    private fun setupObservers() {
        viewModel.registerState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.buttonCreate.isEnabled = false
                    binding.buttonCreate.text = "Criando..."
                }
                is Resource.Success -> {
                    binding.buttonCreate.isEnabled = true
                    Toast.makeText(this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, ListsActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                is Resource.Error -> {
                    binding.buttonCreate.isEnabled = true
                    binding.buttonCreate.text = "Criar"
                    Toast.makeText(this, "Erro: ${resource.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}