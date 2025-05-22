package com.anlarsinsoftware.latteproje

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anlarsinsoftware.latteproje.databinding.ItemHataliUrunBinding

class HataliUrunAdapter(val veriler: MutableList<String>) :
    RecyclerView.Adapter<HataliUrunAdapter.HataliUrunViewHolder>() {

    inner class HataliUrunViewHolder(val binding: ItemHataliUrunBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HataliUrunViewHolder {
        val binding = ItemHataliUrunBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HataliUrunViewHolder(binding)
    }

    override fun getItemCount(): Int = veriler.size

    override fun onBindViewHolder(holder: HataliUrunViewHolder, position: Int) {
        holder.binding.textViewTarih.text = veriler[position]
    }
}
