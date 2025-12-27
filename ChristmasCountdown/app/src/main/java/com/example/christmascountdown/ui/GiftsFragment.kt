package com.example.christmascountdown.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.christmascountdown.data.GiftItem
import com.example.christmascountdown.databinding.FragmentGiftsBinding
import com.example.christmascountdown.databinding.DialogAddGiftBinding
import com.example.christmascountdown.ui.adapter.GiftsAdapter
import com.google.android.material.snackbar.Snackbar

class GiftsFragment : Fragment() {

    private var _binding: FragmentGiftsBinding? = null
    private val binding get() = _binding!!

    private val giftItems = mutableListOf<GiftItem>()
    private lateinit var adapter: GiftsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGiftsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFab()
        updateEmptyState()
    }

    private fun setupRecyclerView() {
        adapter = GiftsAdapter(
            items = giftItems,
            onDeleteClick = { item ->
                deleteItem(item)
            },
            onTogglePurchased = { item ->
                togglePurchased(item)
            },
            onToggleWrapped = { item ->
                toggleWrapped(item)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            showAddGiftDialog()
        }
    }

    private fun showAddGiftDialog() {
        val dialogBinding = DialogAddGiftBinding.inflate(layoutInflater)

        AlertDialog.Builder(requireContext())
            .setTitle("🎁 Add Gift")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val recipientName = dialogBinding.etRecipientName.text.toString()
                val giftName = dialogBinding.etGiftName.text.toString()
                val notes = dialogBinding.etNotes.text.toString()

                if (recipientName.isNotBlank() && giftName.isNotBlank()) {
                    addItem(recipientName, giftName, notes)
                } else {
                    val rootView = view
                    if (rootView != null) {
                        Snackbar.make(rootView, "Please fill in all fields", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addItem(recipientName: String, giftName: String, notes: String) {
        val item = GiftItem(
            recipientName = recipientName,
            giftName = giftName,
            notes = notes
        )
        giftItems.add(0, item)
        adapter.notifyItemInserted(0)
        binding.recyclerView.scrollToPosition(0)
        updateEmptyState()

        val rootView = view
        if (rootView != null) {
            Snackbar.make(rootView, "Gift added!", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun deleteItem(item: GiftItem) {
        val position = giftItems.indexOf(item)
        if (position != -1) {
            giftItems.removeAt(position)
            adapter.notifyItemRemoved(position)
            updateEmptyState()

            val rootView = view
            if (rootView != null) {
                Snackbar.make(rootView, "Gift removed", Snackbar.LENGTH_SHORT)
                    .setAction("Undo") {
                        giftItems.add(position, item)
                        adapter.notifyItemInserted(position)
                        updateEmptyState()
                    }
                    .show()
            }
        }
    }

    private fun togglePurchased(item: GiftItem) {
        val position = giftItems.indexOf(item)
        if (position != -1) {
            val updatedItem = item.copy(isPurchased = !item.isPurchased)
            giftItems[position] = updatedItem
            adapter.notifyItemChanged(position)
        }
    }

    private fun toggleWrapped(item: GiftItem) {
        val position = giftItems.indexOf(item)
        if (position != -1) {
            val updatedItem = item.copy(isWrapped = !item.isWrapped)
            giftItems[position] = updatedItem
            adapter.notifyItemChanged(position)
        }
    }

    private fun updateEmptyState() {
        if (giftItems.isEmpty()) {
            binding.recyclerView.visibility = View.GONE
            binding.tvEmpty.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}