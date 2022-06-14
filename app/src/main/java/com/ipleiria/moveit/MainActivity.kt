package com.ipleiria.moveit


import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ipleiria.moveit.activity.Login
import com.ipleiria.moveit.databinding.ActivityMainBinding
import com.ipleiria.moveit.databinding.NavDrawerBinding
import com.ipleiria.moveit.databinding.ToolbarBinding
import com.ipleiria.moveit.models.User
import com.ipleiria.moveit.activity.LivePreviewActivity


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var navDrawerBinding: NavDrawerBinding
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var toolbarBinding: ToolbarBinding
    private lateinit var drLayout: DrawerLayout
    private lateinit var nvView: NavigationView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var txtName: TextView
    private lateinit var txtEmail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isUserAuthenticated();

        navDrawerBinding = NavDrawerBinding.inflate(layoutInflater)
        setContentView(navDrawerBinding.root)
        mainBinding = navDrawerBinding.content

        firebaseAuth = Firebase.auth

        toolbarBinding = mainBinding.toolbar
        setSupportActionBar(toolbarBinding.toolbar)

        drLayout = navDrawerBinding.navDrawer
        nvView = navDrawerBinding.navigationView

        val toggle = ActionBarDrawerToggle(
            this, drLayout, toolbarBinding.toolbar,0,0
        )
        drLayout.addDrawerListener(toggle)
        toggle.syncState()
        nvView.setNavigationItemSelectedListener(this)

        txtName = nvView.getHeaderView(0).findViewById(R.id.txtHeaderName)
        txtEmail = nvView.getHeaderView(0).findViewById(R.id.txtHeaderEmail)

        getUserData()

    }
    private fun isUserAuthenticated(){
        val pref = applicationContext
            .getSharedPreferences("MyPref", 0)
        println(pref.getString("email", null) )

        if(pref.getString("email", null) == null){
            startActivity(Intent(this, Login::class.java))
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.btnPrescriptions -> {
                Toast.makeText(this, "Prescriptions clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.btnPoseDetection -> {
                startActivity(Intent(this, LivePreviewActivity::class.java))
                //Toast.makeText(this, "PoseDetection clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.btnSetting -> {
                Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.btnLogout -> {
                Logout()
            }
        }
        drLayout.closeDrawer(GravityCompat.START)
        return true
    }

     private fun Logout(){
         val pref = applicationContext
             .getSharedPreferences("MyPref", 0)
         pref.edit().remove("email").apply();
         startActivity(Intent(this, Login::class.java))
     }

    private fun getUserData() {
        val database = Firebase.database.getReference("Users").child(firebaseAuth.uid!!)

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userModel = snapshot.getValue(User::class.java)
                    txtEmail.text = userModel?.email
                    txtName.text = userModel?.username
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}