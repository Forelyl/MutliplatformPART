package ua.kpi.ipze.part.services.passwordStore

enum class PasswordState {
    NOT_SET, CORRECT, INCORRECT
}

expect class PasswordStore() {
    fun clearPassword()

    @Suppress("FunctionName")
            /** Internal implementation without hashing - should be called with hashed value */
    fun __comparePassword(passwordString: String): PasswordState

    @Suppress("FunctionName")
            /** Internal implementation without hashing - should be called with hashed value */
    fun __upsertPassword(password: String)
}

fun PasswordStore.upsertPassword(password: String) {
    // TODO: hash password before storing
    __upsertPassword(password)
}

fun PasswordStore.comparePassword(password: String): PasswordState {
    // TODO: hash password before comparing
    return __comparePassword(password)
}