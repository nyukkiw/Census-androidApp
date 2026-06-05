package com.example.census.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.census.R
import com.example.census.data.UmatEntity

class UmatAdapter(
    private val onItemClick: (UmatEntity) -> Unit,
    private val onItemLongClick: (UmatEntity) -> Unit
) : ListAdapter<UmatEntity, UmatAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tvNama)
        val tvNIK: TextView = view.findViewById(R.id.tvNIK)
        val tvKK: TextView = view.findViewById(R.id.tvKK)
        val tvJenisKelamin: TextView = view.findViewById(R.id.tvJenisKelamin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_umat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val umat = getItem(position)
        holder.tvNama.text = umat.namaLengkap
        holder.tvNIK.text = "NIK: ${umat.nomorNIK}"
        holder.tvKK.text = "No. KK: ${umat.nomorKK}"
        holder.tvJenisKelamin.text = umat.jenisKelamin

        holder.itemView.setOnClickListener { onItemClick(umat) }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(umat)
            true
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<UmatEntity>() {
        override fun areItemsTheSame(oldItem: UmatEntity, newItem: UmatEntity) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: UmatEntity, newItem: UmatEntity) =
            oldItem == newItem
    }
}