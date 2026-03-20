package ua.kpi.ipze.part.services.passwordStore

import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import ua.kpi.ipze.part.providers.GlobalApplicationContext

actual class PasswordStore actual constructor() {
    companion object {
        private const val PASSWORD_KEY = "local_app_password"
        private const val PREFERENCE_FILE_NAME = "local_secure_data"
    }

    private val masterKey = MasterKey.Builder(GlobalApplicationContext.context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    private val sharedPrefs = EncryptedSharedPreferences.create(
        GlobalApplicationContext.context,
        PREFERENCE_FILE_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    @Suppress("FunctionName")
    actual fun __upsertPassword(password: String) {
        sharedPrefs.edit {
            putString(PASSWORD_KEY, password)
        }
    }


    actual fun clearPassword() {
        sharedPrefs.edit {
            remove(PASSWORD_KEY)
        }
    }

    @Suppress("FunctionName")
    actual fun __comparePassword(passwordString: String): PasswordState {
        val savedString = sharedPrefs.getString(PASSWORD_KEY, null) ?: return PasswordState.NOT_SET
        return if (savedString == passwordString) PasswordState.CORRECT else PasswordState.INCORRECT
    }
}