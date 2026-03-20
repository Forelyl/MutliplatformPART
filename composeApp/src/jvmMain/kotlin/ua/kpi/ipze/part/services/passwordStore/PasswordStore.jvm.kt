package ua.kpi.ipze.part.services.passwordStore

import ua.kpi.ipze.part.utils.filesystem.ApplicationFolder
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.KeyStore
import javax.crypto.spec.SecretKeySpec

actual class PasswordStore actual constructor() {
    companion object {
        private const val KEYSTORE_TYPE = "PKCS12"
        private const val KEY_ALIAS = "local_app_password"
        private val KEYSTORE_PASSWORD = "app_keystore".toCharArray()
    }

    private val keystoreFile = File(ApplicationFolder, "secure.p12")

    private val keyStore = KeyStore.getInstance(KEYSTORE_TYPE).also { ks ->
        if (keystoreFile.exists()) {
            FileInputStream(keystoreFile).use { ks.load(it, KEYSTORE_PASSWORD) }
        } else {
            ks.load(null, KEYSTORE_PASSWORD)
        }
    }

    @Suppress("FunctionName")
    actual fun __upsertPassword(password: String) {
        val secretKey = SecretKeySpec(password.toByteArray(), "AES")
        keyStore.setKeyEntry(KEY_ALIAS, secretKey, KEYSTORE_PASSWORD, null)
        FileOutputStream(keystoreFile).use { keyStore.store(it, KEYSTORE_PASSWORD) }
    }

    actual fun clearPassword() {
        if (keyStore.containsAlias(KEY_ALIAS)) {
            keyStore.deleteEntry(KEY_ALIAS)
            FileOutputStream(keystoreFile).use { keyStore.store(it, KEYSTORE_PASSWORD) }
        }
    }

    @Suppress("FunctionName")
    actual fun __comparePassword(passwordString: String): PasswordState {
        val savedKey = keyStore.getKey(KEY_ALIAS, KEYSTORE_PASSWORD) ?: return PasswordState.NOT_SET
        val savedString = String(savedKey.encoded)
        return if (savedString == passwordString) PasswordState.CORRECT else PasswordState.INCORRECT
    }
}