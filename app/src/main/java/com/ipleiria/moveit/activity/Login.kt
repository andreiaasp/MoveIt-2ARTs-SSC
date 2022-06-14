package com.ipleiria.moveit.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ipleiria.moveit.MainActivity
import com.ipleiria.moveit.databinding.LoginBinding

class Login : AppCompatActivity(){

    private lateinit var binding: LoginBinding
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var mainApp: MainApp
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        mainApp= MainApp()
        val pb = binding.progressBar2
        pb.visibility = View.INVISIBLE

        setContentView(binding.root)
        val pref = applicationContext
            .getSharedPreferences("MyPref", 0)
        val editor: SharedPreferences.Editor = pref.edit()

        binding.btnSignUp.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }

        binding.txtForgetPassword.setOnClickListener {
            startActivity(Intent(this, ForgetPassword::class.java))
        }

        binding.btnLogin.setOnClickListener {
            if (areFieldReady()) {
                binding.progressBar2.visibility = View.VISIBLE

                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if(task.isSuccessful && auth.currentUser?.isEmailVerified!!){
                        Log.d("LOGIN","Login Success")
                        binding.progressBar2.visibility = View.INVISIBLE
                        editor.putString("email", email)
                        editor.commit()
                        val intent = Intent(this@Login, MainActivity::class.java)
                        startActivity(intent)
                    }
                }.addOnFailureListener {
                    auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { _ ->
                        println("Email Sent")
                    }?.addOnFailureListener { _ ->
                        println("Email Error")
                        binding.progressBar2.visibility = View.INVISIBLE
                        Toast.makeText(applicationContext,"Email ou password inválidos." , Toast.LENGTH_LONG).show()
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
                binding.email.error = "Campo é obrigatório"
                view = binding.email
                error = true
            }
            password.isEmpty() -> {
                binding.password.error = "Campo é obrigatório"
                view = binding.password
                error = true
            }
            password.length < 6 -> {
                binding.password.error = "Mínimo de 6 caracteres"
                view = binding.password
                error = true
            }
        }
        return if (error) {
            view?.requestFocus()
            false
        } else {
            true
        }
    }
}