package com.ipleiria.moveit.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ipleiria.moveit.R
import com.ipleiria.moveit.activity.Map
import com.ipleiria.moveit.adapters.PrescriptionAdapter
import com.ipleiria.moveit.databinding.FragmentPrescriptionsBinding
import com.ipleiria.moveit.models.Prescription
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PrescriptionFragment : Fragment() {
    private lateinit var prescriptionBinding: FragmentPrescriptionsBinding
    private lateinit var addsBtn: FloatingActionButton
    private lateinit var recv: RecyclerView
    private lateinit var prescriptionList:ArrayList<Prescription>
    private lateinit var prescriptionAdapter: PrescriptionAdapter
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        prescriptionBinding = FragmentPrescriptionsBinding.inflate(layoutInflater, container, false)
        prescriptionList = ArrayList()
        addsBtn = prescriptionBinding.addingBtn
        recv = prescriptionBinding.mRecycler
        auth = Firebase.auth

        prescriptionAdapter = PrescriptionAdapter(prescriptionList)

        addsBtn.setOnClickListener { addInfo() }

        return prescriptionBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //(activity as AppCompatActivity?)!!.supportActionBar!!.title = "Prescriptions"
        prescriptionBinding.mRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = prescriptionAdapter
        }

        getPrescriptionsFirebase()

    }

    private fun getPrescriptionsFirebase() {

        var ref = Firebase.database.getReference("Users").child(auth.uid!!).child("Prescriptions")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    prescriptionList = ArrayList()
                    for (postSnapshot in snapshot.children) {
                        val duration = postSnapshot.child("duration").getValue().toString().toInt()
                        val name = postSnapshot.child("name").getValue() as String
                        val prescription = Prescription(name, duration)
                        println(prescription)
                        prescriptionList.add(prescription!!)
                    }
                    var adapter = PrescriptionAdapter(prescriptionList)
                    prescriptionBinding.mRecycler.adapter = adapter
                    adapter.setOnItemClickListener(object: PrescriptionAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            Toast.makeText(
                                requireContext(),
                                "You clicked on item no. ${position}",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(getActivity(),Map::class.java)
                            intent.putExtra("duration",prescriptionList[position].duration.toInt())
                            startActivity(intent)

                        }


                    })
                    println("AQUI " + prescriptionList)

                    Toast.makeText(
                        requireContext(),
                        "${prescriptionList.size}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
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