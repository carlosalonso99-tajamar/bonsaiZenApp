package com.bonsaizen.bonsaizenapp.ui.bonsais

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bonsaizen.bonsaizenapp.data.model.bonsais.Bonsai
import com.bonsaizen.bonsaizenapp.databinding.ItemListBonsaisBinding


class BonsaiAdapter(
    private var bonsaiList: List<Bonsai>,
    private val onBonsaiClicked: (Bonsai) -> Unit
) : RecyclerView.Adapter<BonsaiViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BonsaiViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemListBonsaisBinding.inflate(layoutInflater, parent, false)
        return BonsaiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BonsaiViewHolder, position: Int) {
        val bonsai = bonsaiList[position]
        Log.d("BonsaiAdapter", "Binding bonsai: ${bonsai.name} at position: $position")
        holder.onBind(bonsai, onBonsaiClicked)
    }

    override fun getItemCount(): Int = bonsaiList.size

    fun updateList(newBonsaiList: List<Bonsai>) {
        Log.d("BonsaiAdapter", "Updating adapter list. New size: ${newBonsaiList.size}")
        bonsaiList = newBonsaiList
        notifyDataSetChanged()
    }
}