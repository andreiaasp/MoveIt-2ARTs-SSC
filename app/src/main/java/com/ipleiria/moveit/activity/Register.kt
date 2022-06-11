package com.ipleiria.moveit.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ipleiria.moveit.activity.MainApp
import com.ipleiria.moveit.databinding.RegisterBinding

class Register : AppCompatActivity() {

    private lateinit var binding: RegisterBinding
    private lateinit var mainApp: MainApp
    private lateinit var username: String
    private lateinit var email: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = RegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainApp= MainApp()

        binding.btnBack.setOnClickListener { onBackPressed() }
        binding.txtLogin.setOnClickListener { onBackPressed() }
        binding.btnSignUp.setOnClickListener {
            if (areFieldReady()) {
                mainApp.signUp(email, password, username)
            }
        }

    }

    private fun areFieldReady(): Boolean {
        username = binding.username.text.trim().toString()
        email = binding.email.text.trim().toString()
        password = binding.password.text.trim().toString()

        var view: View? = null
        var error = false

        when {
            username.isEmpty() -> {
                binding.username.error = "Field is required"
                view = binding.username
                error = true
            }

            email.isEmpty() -> {
                binding.email.error = "Field is required"
                view = binding.email
                error = true
            }
            password.isEmpty() -> {
                binding.password.error = "Field is required"
                view = binding.password
                error = true
            }
            password.length < 6 -> {
                binding.password.error = "Minimum 6 characters"
                view = binding.password
                error = true
            }
        }
        if (error) {
            view?.requestFocus()
            return false
        } else {
            return true
        }
    }
}