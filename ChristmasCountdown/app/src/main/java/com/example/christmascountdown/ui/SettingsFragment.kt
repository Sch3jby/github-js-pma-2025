package com.example.christmascountdown.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.christmascountdown.data.UserPreferencesManager
import com.example.christmascountdown.databinding.FragmentSettingsBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadSettings()
        setupListeners()
    }

    private fun loadSettings() {
        // Načtení jména uživatele
        viewLifecycleOwner.lifecycleScope.launch {
            UserPreferencesManager.getUserName(requireContext()).collect { name ->
                binding.etUserName.setText(name)
            }
        }

        // Načtení nastavení notifikací
        viewLifecycleOwner.lifecycleScope.launch {
            UserPreferencesManager.getNotificationsEnabled(requireContext()).collect { enabled ->
                binding.switchNotifications.isChecked = enabled
            }
        }

        // Načtení stavu prvního spuštění
        viewLifecycleOwner.lifecycleScope.launch {
            UserPreferencesManager.isFirstLaunch(requireContext()).collect { isFirst ->
                binding.tvFirstLaunchStatus.text = if (isFirst) {
                    "Status: First launch"
                } else {
                    "Status: Returning user 🎄"
                }
            }
        }
    }

    private fun setupListeners() {
        // Uložení jména
        binding.btnSaveName.setOnClickListener {
            val name = binding.etUserName.text.toString().trim()
            if (name.isNotBlank()) {
                viewLifecycleOwner.lifecycleScope.launch {
                    UserPreferencesManager.saveUserName(requireContext(), name)
                    UserPreferencesManager.setFirstLaunchComplete(requireContext())
                    Snackbar.make(binding.root, "Name saved!", Snackbar.LENGTH_SHORT).show()
                }
            } else {
                Snackbar.make(binding.root, "Please enter a name", Snackbar.LENGTH_SHORT).show()
            }
        }

        // Toggle notifikací
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            viewLifecycleOwner.lifecycleScope.launch {
                UserPreferencesManager.setNotificationsEnabled(requireContext(), isChecked)
                val message = if (isChecked) {
                    "Notifications enabled 🔔"
                } else {
                    "Notifications disabled 🔕"
                }
                Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
            }
        }

        // Reset dat
        binding.btnResetData.setOnClickListener {
            showResetConfirmation()
        }
    }

    private fun showResetConfirmation() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Reset Data")
            .setMessage("This will clear your name and reset all preferences. Continue?")
            .setPositiveButton("Reset") { _, _ ->
                resetData()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun resetData() {
        viewLifecycleOwner.lifecycleScope.launch {
            UserPreferencesManager.saveUserName(requireContext(), "")
            UserPreferencesManager.setNotificationsEnabled(requireContext(), true)

            binding.etUserName.setText("")
            binding.switchNotifications.isChecked = true

            Snackbar.make(binding.root, "Data reset successfully", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}