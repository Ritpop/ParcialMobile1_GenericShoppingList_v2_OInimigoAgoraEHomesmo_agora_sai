package com.example.hypergenericlistforbuyingstuff.features.auth.presentation.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.example.hypergenericlistforbuyingstuff.R
import com.example.hypergenericlistforbuyingstuff.core.utils.Resource
import com.example.hypergenericlistforbuyingstuff.databinding.ActivityLoginBinding
import com.example.hypergenericlistforbuyingstuff.features.auth.presentation.viewmodel.LoginViewModel
import com.example.hypergenericlistforbuyingstuff.features.shoppinglist.presentation.view.ListsActivity
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModel()
    private lateinit var credentialManager: CredentialManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        credentialManager = CredentialManager.create(this)

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

        binding.buttonGoogleLogin.setOnClickListener {
            startGoogleSignIn()
        }

        binding.buttonRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.textViewForgotPassword.setOnClickListener {
            showRecoverPasswordDialog()
        }
    }

    private fun startGoogleSignIn() {
        lifecycleScope.launch {
            try {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setAutoSelectEnabled(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    request = request,
                    context = this@LoginActivity
                )

                handleSignIn(result)

            } catch (e: GetCredentialException) {
                Log.e("LoginActivity", "Erro no login: ${e.message}")
                if (!e.message.toString().contains("User canceled")) {
                    Toast.makeText(this@LoginActivity, "Erro ao conectar com Google", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        val credential = result.credential

        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {

            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                viewModel.loginWithGoogle(idToken)

            } catch (e: Exception) {
                Log.e("LoginActivity", "Erro ao extrair token", e)
                Toast.makeText(this, "Erro ao processar dados do Google", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.e("LoginActivity", "Credencial desconhecida")
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.loginState.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        binding.buttonLogin.isEnabled = false
                        binding.buttonGoogleLogin.isEnabled = false
                        binding.buttonLogin.text = "Carregando..."
                    }
                    is Resource.Success -> {
                        binding.buttonLogin.isEnabled = true
                        binding.buttonGoogleLogin.isEnabled = true
                        binding.buttonLogin.text = "Acessar"
                        navigateToHome()
                    }
                    is Resource.Error -> {
                        binding.buttonLogin.isEnabled = true
                        binding.buttonGoogleLogin.isEnabled = true
                        binding.buttonLogin.text = "Acessar"
                        Toast.makeText(this@LoginActivity, "Erro: ${resource.message}", Toast.LENGTH_SHORT).show()
                    }
                    null -> {}
                }
            }
        }

        lifecycleScope.launch {
            viewModel.recoverState.collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        Toast.makeText(this@LoginActivity, "Email de recuperação enviado!", Toast.LENGTH_LONG).show()
                    }
                    is Resource.Error -> {
                        Toast.makeText(this@LoginActivity, resource.message, Toast.LENGTH_LONG).show()
                    }
                    is Resource.Loading -> { }
                    null -> {}
                }
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