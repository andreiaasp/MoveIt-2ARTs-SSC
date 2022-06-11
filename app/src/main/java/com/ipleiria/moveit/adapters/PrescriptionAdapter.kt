package com.ipleiria.moveit.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ipleiria.moveit.R
import com.ipleiria.moveit.models.Prescription

class PrescriptionAdapter(val prescriptionList:ArrayList<Prescription>):RecyclerView.Adapter<PrescriptionAdapter.PrescriptionViewHolder>(){

    inner class PrescriptionViewHolder(val v: View): RecyclerView.ViewHolder(v){
        var name:TextView
        var duration:TextView
        //var mMenus: ImageView

        init {
            name = v.findViewById(R.id.mTitle)
            duration = v.findViewById(R.id.mDuration)
            /*mMenus = v.findViewById(R.id.mMenus)
            mMenus.setOnClickListener { popupMenus(it) }*/
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrescriptionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v  = inflater.inflate(R.layout.item_prescription,parent,false)
        return PrescriptionViewHolder(v)
    }

    override fun onBindViewHolder(holder: PrescriptionViewHolder, position: Int) {
        val newList = prescriptionList[position]
        holder.name.text = newList.name
        holder.duration.text = newList.duration.toString()
    }

    override fun getItemCount(): Int {
        return  prescriptionList.size
    }
}