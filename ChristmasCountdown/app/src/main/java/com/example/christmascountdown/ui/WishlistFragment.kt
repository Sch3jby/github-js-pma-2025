package com.example.christmascountdown.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.christmascountdown.data.WishlistItem
import com.example.christmascountdown.databinding.FragmentWishlistBinding
import com.example.christmascountdown.databinding.DialogAddWishBinding
import com.example.christmascountdown.ui.adapter.WishlistAdapter
import com.google.android.material.snackbar.Snackbar

class WishlistFragment : Fragment() {

    private var _binding: FragmentWishlistBinding? = null
    private val binding get() = _binding!!

    private val wishlistItems = mutableListOf<WishlistItem>()
    private lateinit var adapter: WishlistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWishlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFab()
        updateEmptyState()
    }

    private fun setupRecyclerView() {
        adapter = WishlistAdapter(
            items = wishlistItems,
            onDeleteClick = { item ->
                deleteItem(item)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            showAddWishDialog()
        }
    }

    private fun showAddWishDialog() {
        val dialogBinding = DialogAddWishBinding.inflate(layoutInflater)

        AlertDialog.Builder(requireContext())
            .setTitle("🎁 Add to Wishlist")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val name = dialogBinding.etItemName.text.toString()
                val category = dialogBinding.spinnerCategory.selectedItem.toString()
                val priority = dialogBinding.ratingBar.rating.toInt()
                val notes = dialogBinding.etNotes.text.toString()

                if (name.isNotBlank()) {
                    addItem(name, category, priority, notes)
                } else {
                    Snackbar.make(binding.root, "Please enter item name", Snackbar.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addItem(name: String, category: String, priority: Int, notes: String) {
        val item = WishlistItem(
            name = name,
            category = category,
            priority = priority,
            notes = notes
        )
        wishlistItems.add(0, item)
        adapter.notifyItemInserted(0)
        binding.recyclerView.scrollToPosition(0)
        updateEmptyState()

        Snackbar.make(binding.root, "Added to wishlist!", Snackbar.LENGTH_SHORT).show()
    }

    private fun deleteItem(item: WishlistItem) {
        val position = wishlistItems.indexOf(item)
        if (position != -1) {
            wishlistItems.removeAt(position)
            adapter.notifyItemRemoved(position)
            updateEmptyState()

            Snackbar.make(binding.root, "Item removed", Snackbar.LENGTH_SHORT)
                .setAction("Undo") {
                    wishlistItems.add(position, item)
                    adapter.notifyItemInserted(position)
                    updateEmptyState()
                }
                .show()
        }
    }

    private fun updateEmptyState() {
        if (wishlistItems.isEmpty()) {
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