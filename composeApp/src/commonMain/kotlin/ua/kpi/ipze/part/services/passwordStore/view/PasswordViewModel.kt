package ua.kpi.ipze.part.services.passwordStore.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import co.touchlab.kermit.Logger
import ua.kpi.ipze.part.services.passwordStore.PasswordState
import ua.kpi.ipze.part.services.passwordStore.PasswordStore
import ua.kpi.ipze.part.services.passwordStore.comparePassword
import ua.kpi.ipze.part.services.passwordStore.upsertPassword

class PasswordViewModel : ViewModel() {
    private val passwordStore = PasswordStore()

    var passwordInput by mutableStateOf("")

    var isAuthenticated by mutableStateOf(false)
        private set

    var passwordExists by mutableStateOf(false)
        private set

    init {
        checkPasswordExists()
    }

    fun clearPassword() {
        passwordStore.clearPassword()
        passwordExists = false
    }

    private fun checkPasswordExists() {
        passwordExists = passwordStore.comparePassword("") != PasswordState.NOT_SET
        Logger.d(
            "PasswordCheck",
            message = { "Password exist $passwordExists" })
    }

    fun createPass() {
        passwordStore.upsertPassword(passwordInput)
        isAuthenticated = true
        passwordExists = true
    }

    fun handleLogin() {
        isAuthenticated = passwordStore.comparePassword(passwordInput) == PasswordState.CORRECT
    }
}