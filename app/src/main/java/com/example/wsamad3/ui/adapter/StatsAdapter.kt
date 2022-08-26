package com.example.wsamad3.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wsamad3.data.models.Stats
import com.example.wsamad3.data.models.UniqueStat
import com.example.wsamad3.databinding.ItemStatsBinding

class StatsAdapter(private val list : List<UniqueStat>):RecyclerView.Adapter<StatsAdapter.StatsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatsViewHolder {
    val binding = ItemStatsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return  StatsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StatsViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class StatsViewHolder(private val binding: ItemStatsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item : UniqueStat) {
            binding.txt1.text = item.txt1
            binding.txt2.text = item.txt2
            binding.txt3.text = item.txt3
            binding.txt4.text = item.txt4
        }
    }
}
