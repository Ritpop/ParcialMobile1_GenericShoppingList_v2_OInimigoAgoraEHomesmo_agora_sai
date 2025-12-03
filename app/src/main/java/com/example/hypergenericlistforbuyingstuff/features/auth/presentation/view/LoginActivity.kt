package com.example.hypergenericlistforbuyingstuff.features.auth.presentation.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hypergenericlistforbuyingstuff.core.utils.Resource
import com.example.hypergenericlistforbuyingstuff.databinding.ActivityLoginBinding
import com.example.hypergenericlistforbuyingstuff.features.auth.presentation.viewmodel.LoginViewModel
import com.example.hypergenericlistforbuyingstuff.features.shoppinglist.presentation.view.ListsActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val viewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkLoggedUser()
        setupObservers()
        setupListeners()
    }

    private fun checkLoggedUser() {
        CoroutineScope(Dispatchers.Main).launch {
            val user = viewModel.getCurrentUser()
            if (user != null) {
                navigateToHome()
            }
        }
    }

    private fun setupListeners() {
        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if (email.isNotBlank() && password.isNotBlank()) {
                viewModel.login(email, password)
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.textViewForgotPassword.setOnClickListener {
            showRecoverPasswordDialog()
        }
    }

    private fun setupObservers() {
        viewModel.loginState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.buttonLogin.isEnabled = false
                    binding.buttonLogin.text = "Carregando..."
                }
                is Resource.Success -> {
                    binding.buttonLogin.isEnabled = true
                    binding.buttonLogin.text = "Acessar"
                    navigateToHome()
                }
                is Resource.Error -> {
                    binding.buttonLogin.isEnabled = true
                    binding.buttonLogin.text = "Acessar"
                    Toast.makeText(this, "Erro: ${resource.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.recoverState.observe(this) { resource ->
            when (resource) {
                is Resource.Success -> {
                    Toast.makeText(this, "Email de recuperação enviado!", Toast.LENGTH_LONG).show()
                }
                is Resource.Error -> {
                    Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show()
                }
                else -> {  }
            }
        }
    }

    private fun showRecoverPasswordDialog() {
        val container = android.widget.FrameLayout(this)
        val params = android.widget.FrameLayout.LayoutParams(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(60, 20, 60, 0)

        val textInputLayout = com.google.android.material.textfield.TextInputLayout(this)
        textInputLayout.layoutParams = params
        textInputLayout.boxBackgroundMode = com.google.android.material.textfield.TextInputLayout.BOX_BACKGROUND_OUTLINE
        textInputLayout.hint = "Digite seu email"

        val editText = com.google.android.material.textfield.TextInputEditText(this)
        editText.inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        textInputLayout.addView(editText)
        container.addView(textInputLayout)

        MaterialAlertDialogBuilder(this)
            .setTitle("Recuperar Senha")
            .setMessage("Enviaremos um link para o seu e-mail.")
            .setView(container)
            .setPositiveButton("Enviar") { _, _ ->
                val email = editText.text.toString().trim()
                if (email.isNotBlank()) {
                    viewModel.recoverPassword(email)
                } else {
                    Toast.makeText(this, "Email inválido", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun navigateToHome() {
        val intent = Intent(this, ListsActivity::class.java)
        startActivity(intent)
        finish()
    }
}