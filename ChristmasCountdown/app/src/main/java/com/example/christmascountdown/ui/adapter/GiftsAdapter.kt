package com.example.christmascountdown.ui.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.christmascountdown.data.GiftItem
import com.example.christmascountdown.databinding.ItemGiftBinding

class GiftsAdapter(
    private val items: List<GiftItem>,
    private val onDeleteClick: (GiftItem) -> Unit,
    private val onTogglePurchased: (GiftItem) -> Unit,
    private val onToggleWrapped: (GiftItem) -> Unit
) : RecyclerView.Adapter<GiftsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemGiftBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGiftBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.binding.apply {
            tvRecipient.text = "For: ${item.recipientName}"
            tvGiftName.text = item.giftName

            // Checkbox states
            cbPurchased.isChecked = item.isPurchased
            cbWrapped.isChecked = item.isWrapped

            // Strikethrough if purchased
            if (item.isPurchased) {
                tvGiftName.paintFlags = tvGiftName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                tvGiftName.paintFlags = tvGiftName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            // Notes
            if (item.notes.isNotBlank()) {
                tvNotes.text = item.notes
                tvNotes.visibility = android.view.View.VISIBLE
            } else {
                tvNotes.visibility = android.view.View.GONE
            }

            // Click listeners
            cbPurchased.setOnClickListener {
                onTogglePurchased(item)
            }

            cbWrapped.setOnClickListener {
                onToggleWrapped(item)
            }

            btnDelete.setOnClickListener {
                onDeleteClick(item)
            }
        }
    }

    override fun getItemCount() = items.size
}