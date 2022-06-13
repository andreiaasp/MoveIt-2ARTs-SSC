package com.ipleiria.moveit.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ipleiria.moveit.databinding.ForgetPasswordBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ForgetPassword : AppCompatActivity(){

    private lateinit var binding: ForgetPasswordBinding
    private lateinit var mainApp: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainApp= MainApp()
        binding = ForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { onBackPressed() }

        binding.btnForgetPassword.setOnClickListener {
            val email = binding.email.text.trim().toString()

            if (email.isEmpty()) {
                binding.email.error = "Field is required"
                binding.email.requestFocus()
            } else {
                GlobalScope.launch{
                    mainApp.forgetPassword(email, applicationContext)
                }
            }
        }
    }
}