package com.ipleiria.moveit.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ipleiria.moveit.MainActivity
import com.ipleiria.moveit.activity.MainApp
import com.ipleiria.moveit.databinding.LoginBinding

class Login : AppCompatActivity(){

    private lateinit var binding: LoginBinding
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var mainApp: MainApp
    val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        mainApp= MainApp()

        setContentView(binding.root)

        binding.btnSignUp.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }

        binding.txtForgetPassword.setOnClickListener {
            startActivity(Intent(this, ForgetPassword::class.java))
        }

        binding.btnLogin.setOnClickListener {
            if (areFieldReady()) {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if(task.isSuccessful && auth.currentUser?.isEmailVerified!!){
                        Log.d("LOGIN","Login Success")
                        val intent = Intent(this@Login, MainActivity::class.java)
                        startActivity(intent)
                    }
                }.addOnFailureListener { _ ->
                    auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
                        println("Email Sent")
                    }?.addOnFailureListener { _ ->
                        println("Email Error")
                    }
                }
            }
        }
    }

    private fun areFieldReady(): Boolean {
        email = binding.email.text.trim().toString()
        password = binding.password.text.trim().toString()

        var view: View? = null
        var error = false

        when {
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