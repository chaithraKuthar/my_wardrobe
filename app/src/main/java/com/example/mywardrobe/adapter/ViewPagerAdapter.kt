package com.example.mywardrobe.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mywardrobe.R
import com.example.mywardrobe.db.entity.Wardrobe
import java.io.File


class ViewPagerAdapter(val wardrobeItemList: ArrayList<Wardrobe>) :
    RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_wardrobe, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViewHolder(position)
    }

    override fun getItemCount(): Int {
        return wardrobeItemList.size
    }

    fun addItemList(list: ArrayList<Wardrobe>) {
        wardrobeItemList.clear()
        wardrobeItemList.addAll(list)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindViewHolder(position: Int) {
            val imageView = itemView.findViewById<AppCompatImageView>(R.id.imageView)
            wardrobeItemList[position].itemPath?.let {
                val imgFile = File(it)
                if (imgFile.exists()) {
                    Glide.with(imageView.context).load(imgFile).into(imageView)
                } else {
                    Log.d("TAG", "Not Exist")
                }
            }
        }
    }
}