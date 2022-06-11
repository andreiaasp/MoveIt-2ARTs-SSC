package com.ipleiria.moveit.activity

import android.util.Log
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


class MainApp : AppCompatActivity() {

    suspend fun login(
        email: String,
        password: String
    ): String? = withContext(Dispatchers.IO) {
        val auth = Firebase.auth
        var status=""
        try {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if(task.isSuccessful && auth.currentUser?.isEmailVerified!!){
                    Log.d("LOGIN","Login Success")
                    status = "Login Success"
                }
            }.addOnFailureListener { _ ->
                status = "Verify Email"
                auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
                    println("Email Sent")
                }?.addOnFailureListener { _ ->
                    println("Email Error")
                }

            }

            status
        } catch (t: Throwable) {
            null
        }
    }

    fun signUp(
        email: String,
        password: String,
        username: String
    ) {

        val auth = Firebase.auth

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if(task.isSuccessful){

                val user = User(
                    email, username
                )

                GlobalScope.launch{
                    createUser(user, auth);
                }

                Log.d("REGISTER","Email Verification Sent")

            }
        }.addOnFailureListener { exception ->
            Log.d("REGISTER","FAIL 2" + exception)
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

    suspend fun forgetPassword(email: String) {
        val auth = Firebase.auth
        auth.sendPasswordResetEmail(email).await()
        Toast.makeText(this,"Password Reset Email Sent",Toast.LENGTH_SHORT)
    }

}