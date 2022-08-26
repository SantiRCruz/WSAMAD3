package com.example.wsamad3.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.wsamad3.data.models.Symptom
import com.example.wsamad3.databinding.FragmentCheckListBinding
import com.example.wsamad3.databinding.ItemSymptomBinding

class SymptomsAdapter(private val list : List<Symptom>):RecyclerView.Adapter<SymptomsAdapter.SymptomsViewHolder>() {
    private val checkList = mutableListOf<CheckBox>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymptomsViewHolder {
        val binding = ItemSymptomBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SymptomsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SymptomsViewHolder, position: Int) {
        holder.bind(list[position])
    }
    override fun getItemCount(): Int = list.size

    inner class SymptomsViewHolder(private val binding: ItemSymptomBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(item : Symptom){
            checkList.add(binding.checkbox)
            binding.txt.text = item.title
        }
    }
    fun checkedBoxes():List<Int>{
        val listInt = mutableListOf<Int>()
        checkList.indices.forEachIndexed { index, i ->
            if (checkList[index].isChecked) listInt.add(list[index].id)
        }
        return listInt
    }
}