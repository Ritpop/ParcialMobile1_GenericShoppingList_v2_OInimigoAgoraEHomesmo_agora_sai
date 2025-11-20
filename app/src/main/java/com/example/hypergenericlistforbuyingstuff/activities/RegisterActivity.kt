package com.example.hypergenericlistforbuyingstuff.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hypergenericlistforbuyingstuff.data.DataStore
import com.example.hypergenericlistforbuyingstuff.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonCreate.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            val confirmPassword = binding.editTextConfirmPassword.text.toString()

            if (name.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Preeencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "As senhas deve ser iguais", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newUser = DataStore.registerUser(name, email, password)
            if (newUser != null) {
                Toast.makeText(this, "Usuario criado com Sucesso!", Toast.LENGTH_SHORT).show()
                finish()             } else {
                Toast.makeText(this, "Este email esta em uso", Toast.LENGTH_SHORT).show()
            }
        }
    }
}