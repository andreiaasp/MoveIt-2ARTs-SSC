package com.ipleiria.moveit.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ipleiria.moveit.R
import com.ipleiria.moveit.adapters.PrescriptionAdapter
import com.ipleiria.moveit.databinding.FragmentPrescriptionsBinding
import com.ipleiria.moveit.models.Prescription
import com.ipleiria.moveit.models.User
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PrescriptionFragment : Fragment() {
    private lateinit var prescriptionBinding: FragmentPrescriptionsBinding
    private lateinit var addsBtn: FloatingActionButton
    private lateinit var recv: RecyclerView
    private lateinit var prescriptionList:ArrayList<Prescription>
    private lateinit var prescriptionAdapter: PrescriptionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        prescriptionBinding = FragmentPrescriptionsBinding.inflate(layoutInflater, container, false)
        prescriptionList = ArrayList()
        addsBtn = prescriptionBinding.addingBtn
        recv = prescriptionBinding.mRecycler

        prescriptionAdapter = PrescriptionAdapter(prescriptionList)

        recv.layoutManager = LinearLayoutManager(requireContext())
        recv.adapter = prescriptionAdapter
        /**set Dialog*/
        addsBtn.setOnClickListener { addInfo() }

        return prescriptionBinding.root
    }


    private fun addInfo() {
        val auth = Firebase.auth
        val inflater = LayoutInflater.from(requireContext())
        val v = inflater.inflate(R.layout.add_prescription_dialog,null)

        val name = v.findViewById<EditText>(R.id.name)
        val duration = v.findViewById<EditText>(R.id.duration)

        val addDialog = AlertDialog.Builder(requireContext())

        addDialog.setView(v)
        addDialog.setPositiveButton("Ok"){
                dialog,_->
            val name = name.text.toString()
            val number = Integer.parseInt(duration.getText().toString())
            val prescription = Prescription(name,number)

            prescriptionList.add(Prescription(name,number))
            prescriptionAdapter.notifyDataSetChanged()
            Toast.makeText(requireContext(),"Adding Prescription Information Success",Toast.LENGTH_SHORT).show()

            GlobalScope.launch{
                createPrescriptionFirebase(prescription, auth);
            }
            dialog.dismiss()
        }
        addDialog.setNegativeButton("Cancel"){
                dialog,_->
            dialog.dismiss()
            Toast.makeText(requireContext(),"Cancel",Toast.LENGTH_SHORT).show()

        }
        addDialog.create()
        addDialog.show()
    }

    private suspend fun createPrescriptionFirebase(prescription: Prescription, auth: FirebaseAuth) {
        //CRIAR CHILD Prescription
        val firebase = Firebase.database.getReference("Users")
        firebase.child(auth.uid!!).child("Prescriptions").push().setValue(prescription).await()
        Log.d("PRESCRIPTION",prescription.toString())
    }
}