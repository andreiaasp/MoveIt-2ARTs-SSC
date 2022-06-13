package com.ipleiria.moveit.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ipleiria.moveit.R
import com.ipleiria.moveit.databinding.PlaceItemBinding
import com.ipleiria.moveit.models.GooglePlace
import com.ipleiria.moveit.models.Prescription

class GooglePlaceAdapter(val placeList:ArrayList<GooglePlace>): RecyclerView.Adapter<GooglePlaceAdapter.GooglePlaceViewHolder>(){
    private lateinit var mlistener: GooglePlaceAdapter.onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: GooglePlaceAdapter.onItemClickListener){
        mlistener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GooglePlaceAdapter.GooglePlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_item,parent,false)
        return GooglePlaceViewHolder (view)
    }

    override fun onBindViewHolder(holder: GooglePlaceViewHolder, position: Int) {
        holder.name.text = placeList[position].name;
        holder.address.text = placeList[position].vicinity;
        holder.rating.text = placeList[position].rating.toString();
    }

    override fun getItemCount(): Int {
        return if (placeList != null) placeList!!.size else 0
    }

    class GooglePlaceViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView){
        val name : TextView = itemView.findViewById(R.id.txtPlaceName)
        val address : TextView = itemView.findViewById(R.id.txtPlaceAddress)
        val rating : TextView = itemView.findViewById(R.id.txtPlaceDRating)

    }

}