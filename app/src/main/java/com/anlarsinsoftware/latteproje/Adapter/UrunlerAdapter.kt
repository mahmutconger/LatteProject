package com.anlarsinsoftware.latteproje

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anlarsinsoftware.latteproje.databinding.ItemUrunBinding

class UrunlerAdapter(val veriler: MutableList<String>) :
    RecyclerView.Adapter<UrunlerAdapter.UrunlerViewHolder>() {

    inner class UrunlerViewHolder(val binding: ItemUrunBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UrunlerViewHolder {
        val binding = ItemUrunBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UrunlerViewHolder(binding)
    }

    override fun getItemCount(): Int = veriler.size

    override fun onBindViewHolder(holder: UrunlerViewHolder, position: Int) {
        holder.binding.textViewTarih.text = veriler[position]
    }
}