package com.ipleiria.moveit.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ipleiria.moveit.R
import com.ipleiria.moveit.models.GooglePlace

class GooglePlaceAdapter(private val placeList:ArrayList<GooglePlace>,private var listener: onItemClickListener): RecyclerView.Adapter<GooglePlaceAdapter.GooglePlaceViewHolder>(){

    interface onItemClickListener{
        fun onItemClick(item:GooglePlace, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GooglePlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_item,parent,false)
        return GooglePlaceViewHolder (view)
    }

    override fun onBindViewHolder(holder: GooglePlaceViewHolder, position: Int) {
        holder.initialize(placeList[position],listener)
    }

    override fun getItemCount(): Int {
        return placeList.size
    }

    class GooglePlaceViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView){
        private val name : TextView = itemView.findViewById(R.id.txtPlaceName)
        private val address : TextView = itemView.findViewById(R.id.txtPlaceAddress)
        private val rating : TextView = itemView.findViewById(R.id.txtPlaceDRating)

        fun initialize(item:GooglePlace, action:onItemClickListener){
            name.text = item.name;
            address.text = item.vicinity;
            rating.text = item.rating.toString()

            itemView.setOnClickListener{
                action.onItemClick(item,adapterPosition)
            }

        }
    }
}