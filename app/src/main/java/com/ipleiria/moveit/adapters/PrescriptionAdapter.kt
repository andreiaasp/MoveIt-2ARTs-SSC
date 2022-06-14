package com.ipleiria.moveit.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ipleiria.moveit.R
import com.ipleiria.moveit.models.Prescription

class PrescriptionAdapter(private val prescriptionList:ArrayList<Prescription>):RecyclerView.Adapter<PrescriptionAdapter.PrescriptionViewHolder>(){

    private lateinit var mlistener:onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener:onItemClickListener){
        mlistener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrescriptionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v  = inflater.inflate(R.layout.item_prescription,parent,false)

        return PrescriptionViewHolder(v,mlistener)
    }

    override fun onBindViewHolder(holder: PrescriptionViewHolder, position: Int) {
        val newList = prescriptionList[position]
        holder.name.text = newList.name
        holder.duration.text = newList.duration.toString()
    }

    override fun getItemCount(): Int {
        return  prescriptionList.size
    }

    class PrescriptionViewHolder(v: View,listener:onItemClickListener): RecyclerView.ViewHolder(v){
        //var name:TextView
        //var duration:TextView
        //var mMenus: ImageView
        var name = v.findViewById(R.id.mTitle) as TextView
        var duration = v.findViewById(R.id.mDuration) as TextView

        init {
            v.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }

        /*mMenus = v.findViewById(R.id.mMenus)
        mMenus.setOnClickListener { popupMenus(it) }*/

    }

}