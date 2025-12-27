package com.example.christmascountdown.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.christmascountdown.data.WishlistItem
import com.example.christmascountdown.databinding.ItemWishlistBinding

class WishlistAdapter(
    private val items: List<WishlistItem>,
    private val onDeleteClick: (WishlistItem) -> Unit
) : RecyclerView.Adapter<WishlistAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemWishlistBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWishlistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.binding.apply {
            tvItemName.text = item.name
            tvCategory.text = item.category
            tvPriority.text = "Priority: ${"⭐".repeat(item.priority)}"

            if (item.notes.isNotBlank()) {
                tvNotes.text = item.notes
                tvNotes.visibility = android.view.View.VISIBLE
            } else {
                tvNotes.visibility = android.view.View.GONE
            }

            btnDelete.setOnClickListener {
                onDeleteClick(item)
            }
        }
    }

    override fun getItemCount() = items.size
}