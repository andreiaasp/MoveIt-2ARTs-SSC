package com.ipleiria.moveit.activity

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ipleiria.moveit.models.User
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.Exception


class MainApp : AppCompatActivity() {
    fun signUp(
        email: String,
        password: String,
        username: String,
        progressBar: ProgressBar,
        applicationContext: Context
    ) {

        val auth = Firebase.auth

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if(task.isSuccessful){

                val user = User(
                    email, username
                )

                GlobalScope.launch{
                    createUser(user, auth)
                }

                Log.d("REGISTER","Email Verification Sent")
                progressBar.visibility = View.INVISIBLE
                Toast.makeText(applicationContext , "Email de verificação enviado", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            progressBar.visibility = View.INVISIBLE
            Toast.makeText(applicationContext, "Ocorreu um erro", Toast.LENGTH_LONG).show()
        }
    }

    private suspend fun createUser(userModel: User, auth: FirebaseAuth) {
        //CRIAR CHILD USERS
        val firebase = Firebase.database.getReference("Users")
        firebase.child(auth.uid!!).setValue(userModel).await()
        Log.d("REGISTER",auth.uid!!)
        val profileChangeRequest = UserProfileChangeRequest.Builder()
            .setDisplayName(userModel.username)
            .build()
        auth.currentUser?.apply {
            updateProfile(profileChangeRequest).await()
            sendEmailVerification().await()
        }
    }

    suspend fun forgetPassword(email: String, applicationContext: Context) {
        val auth = Firebase.auth
        //Toast.makeText(applicationContext,"Password Reset Email Sent" ,Toast.LENGTH_LONG).show();
        try {
            auth.sendPasswordResetEmail(email).await()
        } catch (e: Exception) {
            runOnUiThread {
                Log.d("RESPONSE", "ERROR" + e.message)
            }
        }
        runOnUiThread {
            Toast.makeText(applicationContext,"Email enviado para recuperar a password" ,Toast.LENGTH_LONG).show()
        }


    }

}