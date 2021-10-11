package com.app.buna.boxsimulatorforlol.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.buna.boxsimulatorforlol.activity.ClickListener
import com.app.buna.boxsimulatorforlol.databinding.ItemMinigameBinding
import com.app.buna.boxsimulatorforlol.dto.MiniGameItem
import com.bumptech.glide.Glide

class MiniGameAdapter(var gameList: ArrayList<MiniGameItem>, val listener: ClickListener) : RecyclerView.Adapter<MiniGameAdapter.MyViewHolder>(){

    inner class MyViewHolder(val binding: ItemMinigameBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            Glide.with(binding.backgroundImage.context).load(gameList[position].resId).into(binding.backgroundImage)
            binding.model = gameList[position]
            binding.backgroundImage.setOnClickListener { listener.onClick(position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemMinigameBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return gameList.size
    }

}