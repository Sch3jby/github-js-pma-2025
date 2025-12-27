package com.example.codequizmaster.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.codequizmaster.data.database.QuizDatabase
import com.example.codequizmaster.data.repository.QuizRepository
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: QuizRepository

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    init {
        val database = QuizDatabase.getDatabase(application)
        repository = QuizRepository(
            database.questionDao(),
            database.userDao(),
            database.gameSessionDao()
        )
    }

    fun loginOrRegister(username: String) {
        if (username.isBlank()) {
            _loginResult.value = LoginResult.Error("Jméno nemůže být prázdné")
            return
        }

        if (username.length < 3) {
            _loginResult.value = LoginResult.Error("Jméno musí mít alespoň 3 znaky")
            return
        }

        viewModelScope.launch {
            try {
                var user = repository.getUserByUsername(username)

                if (user == null) {
                    val userId = repository.createUser(username)
                    user = repository.getUserById(userId)
                    _loginResult.value = LoginResult.Success(
                        userId = userId,
                        isNewUser = true
                    )
                } else {
                    _loginResult.value = LoginResult.Success(
                        userId = user.id,
                        isNewUser = false
                    )
                }
            } catch (e: Exception) {
                _loginResult.value = LoginResult.Error("Chyba při přihlášení: ${e.message}")
            }
        }
    }

    sealed class LoginResult {
        data class Success(val userId: Long, val isNewUser: Boolean) : LoginResult()
        data class Error(val message: String) : LoginResult()
    }
}