package com.example.codequizmaster.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.codequizmaster.databinding.ActivityLoginBinding
import com.example.codequizmaster.ui.main.MainActivity
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            hideKeyboard()
            viewModel.loginOrRegister(username)
        }

        binding.etUsername.setOnEditorActionListener { _, _, _ ->
            binding.btnLogin.performClick()
            true
        }
    }

    private fun observeViewModel() {
        viewModel.loginResult.observe(this) { result ->
            when (result) {
                is LoginViewModel.LoginResult.Success -> {
                    hideLoading()

                    val message = if (result.isNewUser) {
                        "Vítej v Code Quiz Master!"
                    } else {
                        "Vítej zpět!"
                    }

                    Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
                    navigateToMain(result.userId)
                }
                is LoginViewModel.LoginResult.Error -> {
                    hideLoading()
                    Snackbar.make(
                        binding.root,
                        result.message,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
            showLoading()
        }
    }

    private fun navigateToMain(userId: Long) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_USER_ID, userId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnLogin.isEnabled = true
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}