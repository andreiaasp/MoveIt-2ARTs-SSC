package com.ipleiria.moveit.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ipleiria.moveit.R
import com.ipleiria.moveit.models.GooglePlace

class GooglePlaceAdapter(val placeList:ArrayList<GooglePlace>,var listener: onItemClickListener): RecyclerView.Adapter<GooglePlaceAdapter.GooglePlaceViewHolder>(){
   //private lateinit var mlistener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(item:GooglePlace, position: Int)
    }

    /*fun setOnItemClickListener(listener: onItemClickListener){
        mlistener = listener
    }*/

    //var onItemClick: ((GooglePlace) -> Unit) ? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GooglePlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_item,parent,false)
        return GooglePlaceViewHolder (view)
    }

    override fun onBindViewHolder(holder: GooglePlaceViewHolder, position: Int) {
        /*val place = placeList[position];
        holder.name.text = placeList[position].name;
        holder.address.text = placeList[position].vicinity;
        holder.rating.text = placeList[position].rating.toString();

        holder.directions.setOnClickListener {
            onItemClick?.invoke(place)
        }*/
        holder.initialize(placeList.get(position),listener)
    }

    override fun getItemCount(): Int {
        return placeList.size
    }

    class GooglePlaceViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView){
        val name : TextView = itemView.findViewById(R.id.txtPlaceName)
        val address : TextView = itemView.findViewById(R.id.txtPlaceAddress)
        val rating : TextView = itemView.findViewById(R.id.txtPlaceDRating)

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