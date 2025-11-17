package com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.data.DataStore
import com.example.ParcialMobile1_GenericShoppingList_v2_OInimigoAgoraEHomesmo_agora_sai.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

                binding.editTextEmail.setText("teste@mail.com")
        binding.editTextPassword.setText("passOword")

        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = DataStore.login(email, password)

            if (user != null) {
                                val intent = Intent(this, ListsActivity::class.java)
                startActivity(intent)
                finish()             } else {
                Toast.makeText(this, "Email e/ou senha invalidos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonRegister.setOnClickListener {
                        val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}